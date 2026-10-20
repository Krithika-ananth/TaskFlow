package com.dayflow.util;

import java.sql.*;

public final class DBConnection {
    private static final String URL = env("DAYFLOW_DB_URL", "jdbc:oracle:thin:@localhost:1521/XEPDB1");
    private static final String USER = env("DAYFLOW_DB_USER", "DAYFLOW");
    private static final String PASSWORD = requiredEnv("DAYFLOW_DB_PASSWORD");
    private DBConnection() {}
    private static String env(String key, String fallback) { String v=System.getenv(key); return v==null||v.isBlank()?fallback:v; }
    private static String requiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing required environment variable: " + key);
        return value;
    }
    static { try { Class.forName("oracle.jdbc.OracleDriver"); } catch (ClassNotFoundException e) { throw new ExceptionInInitializerError(e); } }
    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL, USER, PASSWORD); }
}
