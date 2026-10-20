package com.dayflow.dao;

import com.dayflow.model.Resource;
import com.dayflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {
    private static final String COLUMNS = "SELECT RESOURCE_ID,USER_ID,RESOURCE_TYPE,TITLE,DESCRIPTION,ORIGINAL_FILENAME,CONTENT_TYPE,FILE_SIZE,STORAGE_PATH,RESOURCE_URL,CREATED_AT,UPDATED_AT FROM RESOURCES ";

    public List<Resource> findByUser(long userId, String search, String type) throws Exception {
        StringBuilder sql = new StringBuilder(COLUMNS).append("WHERE USER_ID=?");
        List<String> params = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(TITLE) LIKE ? OR LOWER(DESCRIPTION) LIKE ? OR LOWER(ORIGINAL_FILENAME) LIKE ? OR LOWER(RESOURCE_URL) LIKE ?)");
            String pattern = "%" + search.trim().toLowerCase() + "%";
            params.add(pattern); params.add(pattern); params.add(pattern); params.add(pattern);
        }
        if ("DOCUMENT".equals(type) || "LINK".equals(type)) sql.append(" AND RESOURCE_TYPE=?");
        if ("DOCUMENT".equals(type) || "LINK".equals(type)) params.add(type);
        sql.append(" ORDER BY CREATED_AT DESC, RESOURCE_ID DESC");

        List<Resource> resources = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql.toString())) {
            int index = 1;
            p.setLong(index++, userId);
            for (String param : params) p.setString(index++, param);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) resources.add(map(r));
            }
        }
        return resources;
    }

    public Resource findById(long userId, long resourceId) throws Exception {
        String sql = COLUMNS + "WHERE USER_ID=? AND RESOURCE_ID=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId); p.setLong(2, resourceId);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    public long createDocument(long userId, String title, String description, String originalFilename,
                               String contentType, long fileSize, String storagePath) throws Exception {
        String sql = "INSERT INTO RESOURCES(USER_ID,RESOURCE_TYPE,TITLE,DESCRIPTION,ORIGINAL_FILENAME,CONTENT_TYPE,FILE_SIZE,STORAGE_PATH) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql, new String[]{"RESOURCE_ID"})) {
            p.setLong(1, userId); p.setString(2, "DOCUMENT"); p.setString(3, title); p.setString(4, description);
            p.setString(5, originalFilename); p.setString(6, contentType); p.setLong(7, fileSize); p.setString(8, storagePath);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Resource insert returned no generated id");
                return keys.getLong(1);
            }
        }
    }

    public long createLink(long userId, String title, String description, String url) throws Exception {
        String sql = "INSERT INTO RESOURCES(USER_ID,RESOURCE_TYPE,TITLE,DESCRIPTION,RESOURCE_URL) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql, new String[]{"RESOURCE_ID"})) {
            p.setLong(1, userId); p.setString(2, "LINK"); p.setString(3, title); p.setString(4, description); p.setString(5, url);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Resource insert returned no generated id");
                return keys.getLong(1);
            }
        }
    }

    public boolean updateDocumentMetadata(long userId, long resourceId, String title, String description) throws Exception {
        String sql = "UPDATE RESOURCES SET TITLE=?,DESCRIPTION=?,UPDATED_AT=CURRENT_TIMESTAMP WHERE RESOURCE_ID=? AND USER_ID=? AND RESOURCE_TYPE='DOCUMENT'";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, title); p.setString(2, description); p.setLong(3, resourceId); p.setLong(4, userId);
            return p.executeUpdate() == 1;
        }
    }

    public boolean updateLink(long userId, long resourceId, String title, String description, String url) throws Exception {
        String sql = "UPDATE RESOURCES SET TITLE=?,DESCRIPTION=?,RESOURCE_URL=?,UPDATED_AT=CURRENT_TIMESTAMP WHERE RESOURCE_ID=? AND USER_ID=? AND RESOURCE_TYPE='LINK'";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, title); p.setString(2, description); p.setString(3, url); p.setLong(4, resourceId); p.setLong(5, userId);
            return p.executeUpdate() == 1;
        }
    }

    public Resource delete(long userId, long resourceId) throws Exception {
        Resource resource = findById(userId, resourceId);
        if (resource == null) return null;
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM RESOURCES WHERE RESOURCE_ID=? AND USER_ID=?")) {
            p.setLong(1, resourceId); p.setLong(2, userId);
            if (p.executeUpdate() != 1) return null;
            return resource;
        }
    }

    private Resource map(ResultSet r) throws SQLException {
        Resource resource = new Resource();
        resource.setResourceId(r.getLong("RESOURCE_ID"));
        resource.setUserId(r.getLong("USER_ID"));
        resource.setResourceType(r.getString("RESOURCE_TYPE"));
        resource.setTitle(r.getString("TITLE"));
        resource.setDescription(r.getString("DESCRIPTION"));
        resource.setOriginalFilename(r.getString("ORIGINAL_FILENAME"));
        resource.setContentType(r.getString("CONTENT_TYPE"));
        resource.setFileSize(r.getLong("FILE_SIZE"));
        resource.setStoragePath(r.getString("STORAGE_PATH"));
        resource.setResourceUrl(r.getString("RESOURCE_URL"));
        Timestamp created = r.getTimestamp("CREATED_AT");
        Timestamp updated = r.getTimestamp("UPDATED_AT");
        if (created != null) resource.setCreatedAt(created.toLocalDateTime());
        if (updated != null) resource.setUpdatedAt(updated.toLocalDateTime());
        return resource;
    }
}
