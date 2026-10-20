package com.dayflow.dao;

import com.dayflow.model.TaskHistoryEvent;
import com.dayflow.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Reads only event dates that the current TASKS row actually stores.
 * This is a partial timeline, not a persistent audit log.
 */
public class TaskHistoryDAO {
    public List<TaskHistoryEvent> findAvailableEvents(long userId, String filter, String search)
            throws Exception {
        String created = "SELECT TASK_ID,TITLE,'CREATED' EVENT_TYPE,CREATED_AT OCCURRED_AT "
                + "FROM TASKS WHERE USER_ID=? AND CREATED_AT IS NOT NULL";
        String completed = "SELECT TASK_ID,TITLE,'COMPLETED' EVENT_TYPE,COMPLETED_AT OCCURRED_AT "
                + "FROM TASKS WHERE USER_ID=? AND STATUS='COMPLETED' AND COMPLETED_AT IS NOT NULL";
        boolean includeCreated = "ALL".equals(filter) || "CREATED".equals(filter);
        boolean includeCompleted = "ALL".equals(filter) || "COMPLETED".equals(filter);
        List<String> queries = new ArrayList<>();
        if (includeCreated) queries.add(created + (hasSearch(search) ? " AND LOWER(TITLE) LIKE ?" : ""));
        if (includeCompleted) queries.add(completed + (hasSearch(search) ? " AND LOWER(TITLE) LIKE ?" : ""));
        String sql;
        if (queries.size() == 1) sql = queries.get(0) + " ORDER BY OCCURRED_AT DESC,TASK_ID DESC";
        else sql = "SELECT TASK_ID,TITLE,EVENT_TYPE,OCCURRED_AT FROM (" + String.join(" UNION ALL ", queries)
                + ") ORDER BY OCCURRED_AT DESC,TASK_ID DESC";

        List<TaskHistoryEvent> events = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameter = 1;
            if (includeCreated) {
                statement.setLong(parameter++, userId);
                if (hasSearch(search)) statement.setString(parameter++, pattern(search));
            }
            if (includeCompleted) {
                statement.setLong(parameter++, userId);
                if (hasSearch(search)) statement.setString(parameter, pattern(search));
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    TaskHistoryEvent event = new TaskHistoryEvent();
                    event.setTaskId(rows.getLong("TASK_ID"));
                    event.setTaskTitle(rows.getString("TITLE"));
                    event.setEventType(rows.getString("EVENT_TYPE"));
                    Timestamp occurredAt = rows.getTimestamp("OCCURRED_AT");
                    if (occurredAt != null) event.setOccurredAt(occurredAt.toLocalDateTime());
                    events.add(event);
                }
            }
        }
        return events;
    }

    private boolean hasSearch(String search) {
        return search != null && !search.isBlank();
    }

    private String pattern(String search) {
        return "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
