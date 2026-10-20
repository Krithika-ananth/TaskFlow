package com.dayflow.dao;

import com.dayflow.model.ProductivityBucket;
import com.dayflow.model.ProductivityStats;
import com.dayflow.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsDAO {
    private static final String PRIVATE_TASK = " AND NOT EXISTS "
            + "(SELECT 1 FROM TASK_SHARES s WHERE s.TASK_ID=t.TASK_ID) ";

    public ProductivityStats load(long userId) throws Exception {
        LocalDate today = LocalDate.now();
        ProductivityStats stats = new ProductivityStats();
        loadSummary(userId, today, stats);
        stats.setCategories(categoryCounts(userId));
        stats.setPriorities(priorityCounts(userId));
        stats.setWeekly(weekly(userId, today));
        stats.setMonthly(monthly(userId, YearMonth.from(today)));
        ProductivityDAO productivity = new ProductivityDAO();
        stats.setCurrentStreak(productivity.currentStreak(userId));
        stats.setLongestStreak(productivity.longestStreak(userId));
        stats.setCompletionRate(stats.getTotal() == 0 ? 0
                : (int) Math.round(stats.getCompleted() * 100.0 / stats.getTotal()));
        return stats;
    }

    private void loadSummary(long userId, LocalDate today, ProductivityStats stats) throws Exception {
        String sql = "SELECT COUNT(*),"
                + "NVL(SUM(CASE WHEN t.STATUS='COMPLETED' THEN 1 ELSE 0 END),0),"
                + "NVL(SUM(CASE WHEN t.STATUS='PENDING' THEN 1 ELSE 0 END),0),"
                + "NVL(SUM(CASE WHEN TRUNC(t.DUE_DATE)=? THEN 1 ELSE 0 END),0),"
                + "NVL(SUM(CASE WHEN t.STATUS='PENDING' AND t.DUE_DATE>=? THEN 1 ELSE 0 END),0),"
                + "NVL(SUM(CASE WHEN t.STATUS='PENDING' AND t.DUE_DATE<? THEN 1 ELSE 0 END),0),"
                + "NVL(SUM(CASE WHEN t.IMPORTANT_FLAG='Y' THEN 1 ELSE 0 END),0) "
                + "FROM TASKS t WHERE t.USER_ID=?" + PRIVATE_TASK;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(today));
            statement.setDate(2, java.sql.Date.valueOf(today));
            statement.setDate(3, java.sql.Date.valueOf(today));
            statement.setLong(4, userId);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                stats.setTotal(rows.getInt(1));
                stats.setCompleted(rows.getInt(2));
                stats.setPending(rows.getInt(3));
                stats.setToday(rows.getInt(4));
                stats.setUpcoming(rows.getInt(5));
                stats.setOverdue(rows.getInt(6));
                stats.setImportant(rows.getInt(7));
            }
        }
    }

    private Map<String, Integer> categoryCounts(long userId) throws Exception {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT NVL(c.CATEGORY_NAME,'Uncategorized'),COUNT(*) FROM TASKS t "
                + "LEFT JOIN CATEGORIES c ON c.CATEGORY_ID=t.CATEGORY_ID AND c.USER_ID=t.USER_ID "
                + "WHERE t.USER_ID=?" + PRIVATE_TASK
                + "GROUP BY NVL(c.CATEGORY_NAME,'Uncategorized') ORDER BY COUNT(*) DESC,NVL(c.CATEGORY_NAME,'Uncategorized')";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) counts.put(rows.getString(1), rows.getInt(2));
            }
        }
        return counts;
    }

    private Map<String, Integer> priorityCounts(long userId) throws Exception {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT t.PRIORITY,COUNT(*) FROM TASKS t WHERE t.USER_ID=?" + PRIVATE_TASK
                + "GROUP BY t.PRIORITY ORDER BY CASE t.PRIORITY WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) counts.put(rows.getString(1), rows.getInt(2));
            }
        }
        return counts;
    }

    private List<ProductivityBucket> weekly(long userId, LocalDate today) throws Exception {
        LocalDate start = today.minusDays(6);
        Map<LocalDate, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT TRUNC(t.COMPLETED_AT),COUNT(*) FROM TASKS t "
                + "WHERE t.USER_ID=? AND t.STATUS='COMPLETED' AND t.COMPLETED_AT>=? AND t.COMPLETED_AT<?"
                + PRIVATE_TASK + "GROUP BY TRUNC(t.COMPLETED_AT)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(start));
            statement.setDate(3, java.sql.Date.valueOf(today.plusDays(1)));
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    java.sql.Date date = rows.getDate(1);
                    if (date != null) counts.put(date.toLocalDate(), rows.getInt(2));
                }
            }
        }
        int max = counts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        List<ProductivityBucket> buckets = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            int count = counts.getOrDefault(date, 0);
            buckets.add(new ProductivityBucket(date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH),
                    count, percent(count, max)));
        }
        return buckets;
    }

    private List<ProductivityBucket> monthly(long userId, YearMonth current) throws Exception {
        YearMonth first = current.minusMonths(11);
        Map<YearMonth, Integer> counts = new LinkedHashMap<>();
        DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("uuuu-MM", Locale.ROOT);
        String sql = "SELECT TO_CHAR(t.COMPLETED_AT,'YYYY-MM'),COUNT(*) FROM TASKS t "
                + "WHERE t.USER_ID=? AND t.STATUS='COMPLETED' AND t.COMPLETED_AT>=? AND t.COMPLETED_AT<?"
                + PRIVATE_TASK + "GROUP BY TO_CHAR(t.COMPLETED_AT,'YYYY-MM')";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(first.atDay(1)));
            statement.setDate(3, java.sql.Date.valueOf(current.plusMonths(1).atDay(1)));
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) counts.put(YearMonth.parse(rows.getString(1), keyFormat), rows.getInt(2));
            }
        }
        int max = counts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        List<ProductivityBucket> buckets = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            YearMonth month = first.plusMonths(i);
            int count = counts.getOrDefault(month, 0);
            buckets.add(new ProductivityBucket(month.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)),
                    count, percent(count, max)));
        }
        return buckets;
    }

    private int percent(int count, int max) {
        return max == 0 ? 0 : (int) Math.round(count * 100.0 / max);
    }
}
