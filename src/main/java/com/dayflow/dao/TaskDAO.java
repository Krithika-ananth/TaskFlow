package com.dayflow.dao;

import com.dayflow.model.Task;
import com.dayflow.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class TaskDAO {
    private static final String BASE = "SELECT t.TASK_ID,t.USER_ID,t.CATEGORY_ID,t.TITLE,t.DESCRIPTION,"
            + "t.PRIORITY,t.STATUS,t.DUE_DATE,t.DUE_TIME,t.CREATED_AT,t.COMPLETED_AT,t.REPEAT_TYPE,"
            + "c.CATEGORY_NAME,u.FULL_NAME OWNER_NAME,t.IMPORTANT_FLAG FROM TASKS t "
            + "LEFT JOIN CATEGORIES c ON c.CATEGORY_ID=t.CATEGORY_ID AND c.USER_ID=t.USER_ID "
            + "JOIN USERS u ON u.USER_ID=t.USER_ID ";

    public List<Task> findByUser(long uid, String filter, String sort, String search) throws Exception {
        StringBuilder sql = new StringBuilder(BASE).append("WHERE t.USER_ID=? ");
        List<Object> args = new ArrayList<>(); args.add(uid);
        addSearch(sql, args, search);
        addFilter(sql, args, filter);
        sql.append("ORDER BY ").append(sortSql(sort));
        return query(sql.toString(), args);
    }

    public List<Task> findSharedWithMe(long uid, String filter, String sort, String search) throws Exception {
        StringBuilder sql = new StringBuilder(BASE)
                .append("WHERE t.USER_ID<>? AND EXISTS (SELECT 1 FROM TASK_SHARES s ")
                .append("WHERE s.TASK_ID=t.TASK_ID AND s.USER_ID=?) ");
        List<Object> args = new ArrayList<>(List.of(uid, uid));
        addSearch(sql, args, search);
        addFilter(sql, args, filter);
        sql.append("ORDER BY ").append(sortSql(sort));
        List<Task> tasks = query(sql.toString(), args);
        for (Task task : tasks) task.setSharedWithMe(true);
        return tasks;
    }

    private void addSearch(StringBuilder sql, List<Object> args, String search) {
        if (search == null || search.isBlank()) return;
        String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        sql.append("AND (LOWER(t.TITLE) LIKE ? OR LOWER(NVL(t.DESCRIPTION,'')) LIKE ? ")
                .append("OR LOWER(NVL(c.CATEGORY_NAME,'')) LIKE ?) ");
        args.add(pattern); args.add(pattern); args.add(pattern);
    }

    private void addFilter(StringBuilder sql, List<Object> args, String rawFilter) {
        if (rawFilter == null || rawFilter.isBlank() || "ALL".equalsIgnoreCase(rawFilter)) return;
        String filter = rawFilter.toUpperCase(Locale.ROOT);
        switch (filter) {
            case "TODAY" -> sql.append("AND t.DUE_DATE=TRUNC(SYSDATE) ");
            case "UPCOMING" -> sql.append("AND t.STATUS='PENDING' AND t.DUE_DATE>=TRUNC(SYSDATE) ");
            case "OVERDUE" -> sql.append("AND t.STATUS='PENDING' AND t.DUE_DATE<TRUNC(SYSDATE) ");
            case "COMPLETED", "PENDING" -> { sql.append("AND t.STATUS=? "); args.add(filter); }
            case "HIGH", "MEDIUM", "LOW" -> { sql.append("AND t.PRIORITY=? "); args.add(filter); }
            case "IMPORTANT" -> sql.append("AND t.IMPORTANT_FLAG='Y' ");
            default -> {
                if (filter.startsWith("CATEGORY_")) {
                    try { long id = Long.parseLong(filter.substring(9)); sql.append("AND t.CATEGORY_ID=? "); args.add(id); }
                    catch (NumberFormatException ignored) { sql.append("AND 1=0 "); }
                }
            }
        }
    }

    private String sortSql(String raw) {
        String sort = raw == null ? "DUE" : raw.toUpperCase(Locale.ROOT);
        return switch (sort) {
            case "NEWEST", "CREATED" -> "t.CREATED_AT DESC,t.TASK_ID DESC";
            case "OLDEST" -> "t.CREATED_AT ASC,t.TASK_ID ASC";
            case "PRIORITY" -> "CASE t.PRIORITY WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END,t.DUE_DATE NULLS LAST";
            case "ALPHABETICAL", "TITLE" -> "LOWER(t.TITLE),t.TASK_ID";
            default -> "t.DUE_DATE NULLS LAST,t.DUE_TIME NULLS LAST,t.TASK_ID";
        };
    }

    public Task findById(long uid, long id) throws Exception {
        List<Task> rows = query(BASE + "WHERE t.USER_ID=? AND t.TASK_ID=?", List.of(uid, id));
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Task findAccessibleById(long uid, long id) throws Exception {
        String sql = BASE + "WHERE t.TASK_ID=? AND (t.USER_ID=? OR EXISTS "
                + "(SELECT 1 FROM TASK_SHARES s WHERE s.TASK_ID=t.TASK_ID AND s.USER_ID=?))";
        List<Task> rows = query(sql, List.of(id, uid, uid));
        if (rows.isEmpty()) return null;
        Task task = rows.get(0); task.setSharedWithMe(task.getUserId() != uid); return task;
    }

    public List<Task> today(long uid) throws Exception {
        return query(BASE + "WHERE t.USER_ID=? AND t.DUE_DATE=TRUNC(SYSDATE) ORDER BY t.DUE_TIME NULLS LAST", List.of(uid));
    }
    public List<Task> upcoming(long uid) throws Exception {
        return query(BASE + "WHERE t.USER_ID=? AND t.STATUS='PENDING' AND t.DUE_DATE>=TRUNC(SYSDATE) "
                + "ORDER BY t.DUE_DATE,t.DUE_TIME FETCH FIRST 6 ROWS ONLY", List.of(uid));
    }
    public List<Task> overdue(long uid) throws Exception {
        return query(BASE + "WHERE t.USER_ID=? AND t.STATUS='PENDING' AND t.DUE_DATE<TRUNC(SYSDATE) "
                + "ORDER BY t.DUE_DATE DESC FETCH FIRST 6 ROWS ONLY", List.of(uid));
    }

    public long add(Task t) throws Exception {
        if (!ownsCategory(t.getUserId(), t.getCategoryId())) throw new SQLException("Category does not belong to this user");
        String sql = "INSERT INTO TASKS(USER_ID,CATEGORY_ID,TITLE,DESCRIPTION,PRIORITY,DUE_DATE,IMPORTANT_FLAG) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql, new String[]{"TASK_ID"})) {
            p.setLong(1, t.getUserId()); setNullableLong(p, 2, t.getCategoryId());
            p.setString(3, t.getTitle()); p.setString(4, t.getDescription()); p.setString(5, t.getPriority());
            p.setDate(6, t.getDueDate() == null ? null : java.sql.Date.valueOf(t.getDueDate())); p.setString(7, t.isImportant() ? "Y" : "N"); p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) { if (!r.next()) throw new SQLException("Task insert returned no id"); return r.getLong(1); }
        }
    }

    public boolean update(Task t) throws Exception {
        if (!ownsCategory(t.getUserId(), t.getCategoryId())) throw new SQLException("Category does not belong to this user");
        String sql = "UPDATE TASKS SET TITLE=?,DESCRIPTION=?,PRIORITY=?,DUE_DATE=?,CATEGORY_ID=?,IMPORTANT_FLAG=? WHERE TASK_ID=? AND USER_ID=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, t.getTitle()); p.setString(2, t.getDescription()); p.setString(3, t.getPriority());
            p.setDate(4, t.getDueDate() == null ? null : java.sql.Date.valueOf(t.getDueDate()));
            setNullableLong(p, 5, t.getCategoryId()); p.setString(6, t.isImportant() ? "Y" : "N"); p.setLong(7, t.getTaskId()); p.setLong(8, t.getUserId());
            return p.executeUpdate() == 1;
        }
    }

    private boolean ownsCategory(long uid, Long categoryId) throws SQLException {
        if (categoryId == null) return true;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT 1 FROM CATEGORIES WHERE CATEGORY_ID=? AND USER_ID=?")) {
            p.setLong(1, categoryId); p.setLong(2, uid);
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        }
    }

    private void setNullableLong(PreparedStatement p, int index, Long value) throws SQLException {
        if (value == null) p.setNull(index, Types.NUMERIC); else p.setLong(index, value);
    }

    public boolean setCompletion(long uid, long id, boolean completed) throws Exception {
        String status = completed ? "COMPLETED" : "PENDING";
        String sql = "UPDATE TASKS SET STATUS=?,COMPLETED_AT=CASE WHEN ?='COMPLETED' THEN CURRENT_TIMESTAMP ELSE NULL END "
                + "WHERE TASK_ID=? AND (USER_ID=? OR EXISTS (SELECT 1 FROM TASK_SHARES s "
                + "WHERE s.TASK_ID=TASKS.TASK_ID AND s.USER_ID=?))";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, status); p.setString(2, status); p.setLong(3, id); p.setLong(4, uid); p.setLong(5, uid);
            return p.executeUpdate() == 1;
        }
    }

    public boolean isPrivateOwnedTask(long uid, long id) throws Exception {
        String sql = "SELECT 1 FROM TASKS t WHERE t.TASK_ID=? AND t.USER_ID=? AND NOT EXISTS "
                + "(SELECT 1 FROM TASK_SHARES s WHERE s.TASK_ID=t.TASK_ID)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.setLong(2, uid);
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        }
    }

    public boolean delete(long uid, long id) throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM TASKS WHERE TASK_ID=? AND USER_ID=?")) {
            p.setLong(1, id); p.setLong(2, uid); return p.executeUpdate() == 1;
        }
    }

    public int count(long uid, String condition) throws Exception {
        String safeCondition = switch (condition == null ? "" : condition) {
            case " AND STATUS='PENDING'", " AND STATUS='COMPLETED'", " AND DUE_DATE=TRUNC(SYSDATE)" -> condition;
            default -> "";
        };
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM TASKS WHERE USER_ID=?" + safeCondition)) {
            p.setLong(1, uid); try (ResultSet r = p.executeQuery()) { r.next(); return r.getInt(1); }
        }
    }

    public Map<String,Integer> categoryCounts(long uid) throws Exception {
        Map<String,Integer> map = new LinkedHashMap<>();
        String sql = "SELECT NVL(c.CATEGORY_NAME,'Other'),COUNT(*) FROM TASKS t LEFT JOIN CATEGORIES c "
                + "ON c.CATEGORY_ID=t.CATEGORY_ID AND c.USER_ID=t.USER_ID WHERE t.USER_ID=? "
                + "GROUP BY NVL(c.CATEGORY_NAME,'Other') ORDER BY COUNT(*) DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, uid); try (ResultSet r=p.executeQuery()) { while(r.next()) map.put(r.getString(1),r.getInt(2)); }
        }
        return map;
    }

    public Map<String,Integer> priorityCounts(long uid) throws Exception {
        Map<String,Integer> map = new LinkedHashMap<>();
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(
                "SELECT PRIORITY,COUNT(*) FROM TASKS WHERE USER_ID=? GROUP BY PRIORITY ORDER BY PRIORITY")) {
            p.setLong(1,uid); try(ResultSet r=p.executeQuery()){while(r.next())map.put(r.getString(1),r.getInt(2));}
        }
        return map;
    }

    public Map<String,Integer> weeklyCompleted(long uid) throws Exception {
        Map<String,Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(COMPLETED_AT,'DY','NLS_DATE_LANGUAGE=English'),COUNT(*) FROM TASKS "
                + "WHERE USER_ID=? AND STATUS='COMPLETED' AND COMPLETED_AT>=TRUNC(SYSDATE)-6 "
                + "AND NOT EXISTS (SELECT 1 FROM TASK_SHARES s WHERE s.TASK_ID=TASKS.TASK_ID) "
                + "GROUP BY TO_CHAR(COMPLETED_AT,'DY','NLS_DATE_LANGUAGE=English')";
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,uid);try(ResultSet r=p.executeQuery()){while(r.next())map.put(r.getString(1).trim(),r.getInt(2));}
        }
        return map;
    }

    public List<Task> forDate(long uid, LocalDate date) throws Exception {
        return query(BASE+"WHERE t.USER_ID=? AND t.DUE_DATE=? ORDER BY t.DUE_TIME NULLS LAST",List.of(uid,date));
    }
    public List<Task> forDateRange(long uid, LocalDate start, LocalDate end) throws Exception {
        String sql=BASE+"WHERE t.USER_ID=? AND t.DUE_DATE>=? AND t.DUE_DATE<? ORDER BY t.DUE_DATE,t.DUE_TIME NULLS LAST,t.TASK_ID";
        return query(sql,List.of(uid,start,end));
    }
    public List<Task> forAccessibleDateRange(long uid, LocalDate start, LocalDate end) throws Exception {
        String sql=BASE+"WHERE t.DUE_DATE>=? AND t.DUE_DATE<? AND (t.USER_ID=? OR EXISTS "
                +"(SELECT 1 FROM TASK_SHARES s WHERE s.TASK_ID=t.TASK_ID AND s.USER_ID=?)) "
                +"ORDER BY t.DUE_DATE,t.DUE_TIME NULLS LAST,t.TASK_ID";
        List<Task> tasks=query(sql,List.of(start,end,uid,uid));
        for(Task task:tasks)task.setSharedWithMe(task.getUserId()!=uid);
        return tasks;
    }

    private List<Task> query(String sql,List<Object> args) throws Exception {
        List<Task> rows=new ArrayList<>();
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            for(int i=0;i<args.size();i++){
                Object value=args.get(i);
                if(value instanceof LocalDate d)p.setDate(i+1,java.sql.Date.valueOf(d));
                else if(value instanceof Long l)p.setLong(i+1,l);
                else p.setString(i+1,String.valueOf(value));
            }
            try(ResultSet r=p.executeQuery()){while(r.next())rows.add(map(r));}
        }
        return rows;
    }

    private Task map(ResultSet r)throws SQLException{
        Task t=new Task();t.setTaskId(r.getLong(1));t.setUserId(r.getLong(2));
        long categoryId=r.getLong(3);t.setCategoryId(r.wasNull()?null:categoryId);
        t.setTitle(r.getString(4));t.setDescription(r.getString(5));t.setPriority(r.getString(6));t.setStatus(r.getString(7));
        if(r.getDate(8)!=null)t.setDueDate(r.getDate(8).toLocalDate());t.setDueTime(r.getString(9));
        if(r.getTimestamp(10)!=null)t.setCreatedAt(r.getTimestamp(10).toLocalDateTime());
        if(r.getTimestamp(11)!=null)t.setCompletedAt(r.getTimestamp(11).toLocalDateTime());
        t.setRepeatType(r.getString(12));t.setCategoryName(r.getString(13));t.setOwnerName(r.getString(14));t.setImportant("Y".equals(r.getString(15)));return t;
    }
}
