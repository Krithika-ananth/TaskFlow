package com.dayflow.dao;

import com.dayflow.model.Friend;
import com.dayflow.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendDAO {
    public List<Friend> search(long currentUserId, String query) throws Exception {
        List<Friend> results = new ArrayList<>();
        if (query == null || query.isBlank()) return results;
        String sql = "SELECT u.USER_ID,u.FULL_NAME,u.USERNAME,"
                + "CASE "
                + "WHEN EXISTS (SELECT 1 FROM FRIEND_REQUESTS f WHERE f.STATUS='ACCEPTED' AND "
                + "((f.SENDER_USER_ID=? AND f.RECEIVER_USER_ID=u.USER_ID) OR "
                + "(f.RECEIVER_USER_ID=? AND f.SENDER_USER_ID=u.USER_ID))) THEN 'FRIENDS' "
                + "WHEN EXISTS (SELECT 1 FROM FRIEND_REQUESTS f WHERE f.STATUS='PENDING' "
                + "AND f.SENDER_USER_ID=? AND f.RECEIVER_USER_ID=u.USER_ID) THEN 'SENT' "
                + "WHEN EXISTS (SELECT 1 FROM FRIEND_REQUESTS f WHERE f.STATUS='PENDING' "
                + "AND f.SENDER_USER_ID=u.USER_ID AND f.RECEIVER_USER_ID=?) THEN 'RECEIVED' "
                + "ELSE 'NONE' END RELATION_STATE FROM USERS u "
                + "WHERE u.USER_ID<>? AND (LOWER(u.USERNAME) LIKE ? OR LOWER(u.FULL_NAME) LIKE ?) "
                + "ORDER BY LOWER(u.FULL_NAME),u.USER_ID FETCH FIRST 20 ROWS ONLY";
        String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, currentUserId); p.setLong(2, currentUserId);
            p.setLong(3, currentUserId); p.setLong(4, currentUserId);
            p.setLong(5, currentUserId); p.setString(6, pattern); p.setString(7, pattern);
            try (ResultSet r = p.executeQuery()) { while (r.next()) results.add(map(r, false)); }
        }
        return results;
    }

    public List<Friend> friends(long userId) throws Exception {
        String sql = "SELECT u.USER_ID,u.FULL_NAME,u.USERNAME FROM FRIEND_REQUESTS f "
                + "JOIN USERS u ON u.USER_ID=CASE WHEN f.SENDER_USER_ID=? THEN f.RECEIVER_USER_ID ELSE f.SENDER_USER_ID END "
                + "WHERE f.STATUS='ACCEPTED' AND (f.SENDER_USER_ID=? OR f.RECEIVER_USER_ID=?) "
                + "ORDER BY LOWER(u.FULL_NAME)";
        return queryPeople(sql, userId, false);
    }

    public List<Friend> incoming(long userId) throws Exception {
        String sql = "SELECT u.USER_ID,u.FULL_NAME,u.USERNAME,f.REQUEST_ID FROM FRIEND_REQUESTS f "
                + "JOIN USERS u ON u.USER_ID=f.SENDER_USER_ID WHERE f.RECEIVER_USER_ID=? "
                + "AND f.STATUS='PENDING' ORDER BY f.CREATED_AT,f.REQUEST_ID";
        List<Friend> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) { Friend f = map(r, false); f.setRequestId(r.getLong(4)); rows.add(f); }
            }
        }
        return rows;
    }

    public Friend publicProfile(long viewerId, long profileId) throws Exception {
        String sql = "SELECT u.USER_ID,u.FULL_NAME,u.USERNAME FROM USERS u WHERE u.USER_ID=? AND "
                + "(u.USER_ID=? OR EXISTS (SELECT 1 FROM FRIEND_REQUESTS f WHERE f.STATUS='ACCEPTED' AND "
                + "((f.SENDER_USER_ID=? AND f.RECEIVER_USER_ID=u.USER_ID) OR "
                + "(f.RECEIVER_USER_ID=? AND f.SENDER_USER_ID=u.USER_ID))))";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, profileId); p.setLong(2, viewerId); p.setLong(3, viewerId); p.setLong(4, viewerId);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r, false) : null; }
        }
    }

    /** Returns FRIENDS, SENT, RECEIVED, NONE, or SELF. */
    public String send(long senderId, long receiverId) throws Exception {
        if (senderId == receiverId) return "SELF";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                String state = relationship(c, senderId, receiverId);
                if (!"NONE".equals(state)) { c.rollback(); return state; }
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO FRIEND_REQUESTS(SENDER_USER_ID,RECEIVER_USER_ID,STATUS) VALUES(?,?,'PENDING')")) {
                    p.setLong(1, senderId); p.setLong(2, receiverId); p.executeUpdate();
                }
                c.commit();
                return "SENT";
            } catch (SQLException e) {
                c.rollback();
                if (isUniqueViolation(e)) return relationship(senderId, receiverId);
                throw e;
            } finally { c.setAutoCommit(true); }
        }
    }

    public boolean respond(long receiverId, long requestId, boolean accept) throws Exception {
        String status = accept ? "ACCEPTED" : "REJECTED";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("UPDATE FRIEND_REQUESTS SET STATUS=?,RESPONDED_AT=CURRENT_TIMESTAMP "
                     + "WHERE REQUEST_ID=? AND RECEIVER_USER_ID=? AND STATUS='PENDING'")) {
            p.setString(1, status); p.setLong(2, requestId); p.setLong(3, receiverId);
            return p.executeUpdate() == 1;
        }
    }

    public boolean remove(long userId, long friendId) throws Exception {
        if (userId == friendId) return false;
        String sql = "DELETE FROM FRIEND_REQUESTS WHERE STATUS='ACCEPTED' AND "
                + "((SENDER_USER_ID=? AND RECEIVER_USER_ID=?) OR (SENDER_USER_ID=? AND RECEIVER_USER_ID=?))";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId); p.setLong(2, friendId); p.setLong(3, friendId); p.setLong(4, userId);
            return p.executeUpdate() == 1;
        }
    }

    private String relationship(long a, long b) throws Exception {
        try (Connection c = DBConnection.getConnection()) { return relationship(c, a, b); }
    }

    private String relationship(Connection c, long a, long b) throws SQLException {
        String sql = "SELECT STATUS,SENDER_USER_ID FROM FRIEND_REQUESTS WHERE "
                + "(SENDER_USER_ID=? AND RECEIVER_USER_ID=?) OR (SENDER_USER_ID=? AND RECEIVER_USER_ID=?) "
                + "ORDER BY CASE STATUS WHEN 'ACCEPTED' THEN 1 WHEN 'PENDING' THEN 2 ELSE 3 END";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, a); p.setLong(2, b); p.setLong(3, b); p.setLong(4, a);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return "NONE";
                if ("ACCEPTED".equals(r.getString(1))) return "FRIENDS";
                if ("PENDING".equals(r.getString(1))) return r.getLong(2) == a ? "SENT" : "RECEIVED";
                return "NONE";
            }
        }
    }

    private List<Friend> queryPeople(String sql, long userId, boolean unused) throws Exception {
        List<Friend> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId); p.setLong(2, userId); p.setLong(3, userId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) rows.add(map(r, false)); }
        }
        return rows;
    }

    private Friend map(ResultSet r, boolean unused) throws SQLException {
        Friend f = new Friend(); f.setUserId(r.getLong("USER_ID"));
        f.setFullName(r.getString("FULL_NAME")); f.setUsername(r.getString("USERNAME"));
        try { f.setRelationshipState(r.getString("RELATION_STATE")); } catch (SQLException ignored) { }
        return f;
    }

    private boolean isUniqueViolation(SQLException e) {
        for (SQLException x=e; x!=null; x=x.getNextException()) if (x.getErrorCode()==1) return true;
        return false;
    }
}
