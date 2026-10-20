package com.dayflow.dao;

import com.dayflow.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Completion-date calculations use private, owner-created tasks only. */
public class ProductivityDAO {
    public int currentStreak(long userId) throws Exception {
        List<LocalDate> dates = completionDates(userId);
        if (dates.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate latest = dates.get(dates.size() - 1);
        LocalDate cursor = latest.equals(today) ? today
                : latest.equals(today.minusDays(1)) ? today.minusDays(1) : null;
        if (cursor == null) return 0;

        int streak = 0;
        for (int i = dates.size() - 1; i >= 0 && dates.get(i).equals(cursor); i--, cursor = cursor.minusDays(1)) {
            streak++;
        }
        return streak;
    }

    public int longestStreak(long userId) throws Exception {
        List<LocalDate> dates = completionDates(userId);
        int longest = 0;
        int run = 0;
        LocalDate previous = null;
        for (LocalDate date : dates) {
            run = previous != null && date.equals(previous.plusDays(1)) ? run + 1 : 1;
            longest = Math.max(longest, run);
            previous = date;
        }
        return longest;
    }

    private List<LocalDate> completionDates(long userId) throws Exception {
        String sql = "SELECT DISTINCT TRUNC(t.COMPLETED_AT) FROM TASKS t "
                + "WHERE t.USER_ID=? AND t.STATUS='COMPLETED' AND t.COMPLETED_AT IS NOT NULL "
                + "AND t.COMPLETED_AT<? AND NOT EXISTS "
                + "(SELECT 1 FROM TASK_SHARES s WHERE s.TASK_ID=t.TASK_ID) "
                + "ORDER BY TRUNC(t.COMPLETED_AT)";
        List<LocalDate> dates = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    java.sql.Date date = rows.getDate(1);
                    if (date != null) dates.add(date.toLocalDate());
                }
            }
        }
        return dates;
    }

    /** PRODUCTIVITY currently has no rows and the app does not write to it. */
    public int averageFocus(long userId) throws Exception {
        String sql = "SELECT NVL(ROUND(AVG(FOCUS_MINUTES)),0) FROM PRODUCTIVITY WHERE USER_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return rows.getInt(1);
            }
        }
    }
}
