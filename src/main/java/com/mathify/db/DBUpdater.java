package com.mathify.db;

import java.sql.Connection;
import java.sql.Statement;

public class DBUpdater {
    public static void main(String[] args) {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Add is_disabled column
            String sql = "ALTER TABLE users ADD COLUMN is_disabled BOOLEAN NOT NULL DEFAULT FALSE";
            stmt.executeUpdate(sql);
            System.out.println("SUCCESS: Added is_disabled column to users table.");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            // It might already exist, which is fine
        }
    }
}
