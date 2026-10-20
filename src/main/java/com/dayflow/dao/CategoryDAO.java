package com.dayflow.dao;

import com.dayflow.model.Category;
import com.dayflow.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> findByUser(long userId) throws Exception {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT CATEGORY_ID,USER_ID,CATEGORY_NAME FROM CATEGORIES WHERE USER_ID=? ORDER BY CATEGORY_NAME";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) categories.add(map(rows));
            }
        }
        return categories;
    }

    public Category findById(long userId, long categoryId) throws Exception {
        String sql = "SELECT CATEGORY_ID,USER_ID,CATEGORY_NAME FROM CATEGORIES WHERE USER_ID=? AND CATEGORY_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, categoryId);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? map(rows) : null;
            }
        }
    }

    public long create(long userId, String name) throws Exception {
        String sql = "INSERT INTO CATEGORIES(USER_ID,CATEGORY_NAME) VALUES(?,?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[] {"CATEGORY_ID"})) {
            statement.setLong(1, userId);
            statement.setString(2, name);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new java.sql.SQLException("Category insert returned no generated id");
                return keys.getLong(1);
            }
        }
    }

    public boolean rename(long userId, long categoryId, String name) throws Exception {
        String sql = "UPDATE CATEGORIES SET CATEGORY_NAME=? WHERE CATEGORY_ID=? AND USER_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setLong(2, categoryId);
            statement.setLong(3, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(long userId, long categoryId) throws Exception {
        String sql = "DELETE FROM CATEGORIES WHERE CATEGORY_ID=? AND USER_ID=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private Category map(ResultSet row) throws java.sql.SQLException {
        Category category = new Category();
        category.setCategoryId(row.getLong(1));
        category.setUserId(row.getLong(2));
        category.setCategoryName(row.getString(3));
        return category;
    }
}
