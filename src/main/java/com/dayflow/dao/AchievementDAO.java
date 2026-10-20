package com.dayflow.dao;

import com.dayflow.model.Achievement;
import com.dayflow.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AchievementDAO {
    public List<Achievement> all(long userId, int completedTasks, int currentStreak) throws Exception {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "SELECT a.ACHIEVEMENT_ID,a.NAME,a.DESCRIPTION,a.REQUIRED_VALUE,"
                + "CASE WHEN ua.USER_ID IS NULL THEN 0 ELSE 1 END "
                + "FROM ACHIEVEMENTS a LEFT JOIN USER_ACHIEVEMENTS ua "
                + "ON ua.ACHIEVEMENT_ID=a.ACHIEVEMENT_ID AND ua.USER_ID=? "
                + "ORDER BY a.REQUIRED_VALUE,a.ACHIEVEMENT_ID";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Achievement achievement = new Achievement();
                    achievement.setAchievementId(rows.getLong(1));
                    achievement.setName(rows.getString(2));
                    achievement.setDescription(rows.getString(3));
                    achievement.setRequiredValue(rows.getInt(4));
                    achievement.setUnlocked(rows.getInt(5) == 1);
                    int progress = achievement.getName().toUpperCase(Locale.ROOT).contains("STREAK")
                            ? currentStreak : completedTasks;
                    achievement.setProgress(progress);
                    int required = achievement.getRequiredValue();
                    achievement.setProgressPercent(required <= 0 ? 0
                            : Math.min(100, (int) Math.round(progress * 100.0 / required)));
                    achievements.add(achievement);
                }
            }
        }
        return achievements;
    }

    /** Uses the definitions already stored in ACHIEVEMENTS and the existing user/achievement key. */
    public void unlockEligible(long userId, int completedTasks, int currentStreak) throws Exception {
        String sql = "INSERT INTO USER_ACHIEVEMENTS(USER_ID,ACHIEVEMENT_ID) "
                + "SELECT ?,a.ACHIEVEMENT_ID FROM ACHIEVEMENTS a WHERE "
                + "((UPPER(a.NAME) LIKE '%STREAK%' AND ? >= a.REQUIRED_VALUE) OR "
                + "(UPPER(a.NAME) NOT LIKE '%STREAK%' AND ? >= a.REQUIRED_VALUE)) "
                + "AND NOT EXISTS (SELECT 1 FROM USER_ACHIEVEMENTS u "
                + "WHERE u.USER_ID=? AND u.ACHIEVEMENT_ID=a.ACHIEVEMENT_ID)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setInt(2, currentStreak);
            statement.setInt(3, completedTasks);
            statement.setLong(4, userId);
            statement.executeUpdate();
        }
    }
}
