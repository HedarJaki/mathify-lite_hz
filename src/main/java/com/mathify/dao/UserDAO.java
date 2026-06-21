package com.mathify.dao;

import com.mathify.db.DBUtil;
import com.mathify.model.Student;
import com.mathify.model.User;

import java.sql.*;
import java.util.UUID;

public class UserDAO {

    /**
     * Looks up a student by email.
     * Returns null if the email doesn't belong to a student account.
     */
    public Student findStudentByEmail(String email) throws SQLException {
        String sql =
            "SELECT u.user_id, u.name, u.email, u.password_hash, s.energy " +
            "FROM users u JOIN students s ON s.student_id = u.user_id " +
            "WHERE u.email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student(rs.getString("user_id"));
                    student.setUserId(rs.getString("user_id"));
                    student.setName(rs.getString("name"));
                    student.setEmail(rs.getString("email"));
                    student.setPasswordHash(rs.getString("password_hash"));
                    student.setEnergy(rs.getInt("energy"));
                    return student;
                }
            }
        }
        return null;
    }

    /**
     * Looks up a student by ID.
     */
    public Student getStudentById(String studentId) throws SQLException {
        String sql =
            "SELECT u.user_id, u.name, u.email, u.password_hash, s.energy " +
            "FROM users u JOIN students s ON s.student_id = u.user_id " +
            "WHERE u.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student(rs.getString("user_id"));
                    student.setUserId(rs.getString("user_id"));
                    student.setName(rs.getString("name"));
                    student.setEmail(rs.getString("email"));
                    student.setPasswordHash(rs.getString("password_hash"));
                    student.setEnergy(rs.getInt("energy"));
                    return student;
                }
            }
        }
        return null;
    }

    /** Returns true if the email is already registered in any account type. */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts a new student account (users + students + user_progress rows)
     * inside a single transaction. The password must already be hashed.
     */
    public void createStudent(String name, String email, String passwordHash) throws SQLException {
        String userId = UUID.randomUUID().toString();

        String sqlUser     = "INSERT INTO users (user_id, name, email, password_hash) VALUES (?, ?, ?, ?)";
        String sqlStudent  = "INSERT INTO students (student_id, energy) VALUES (?, 0)";
        String sqlProgress = "INSERT INTO user_progress (student_id) VALUES (?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                    ps.setString(1, userId);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setString(4, passwordHash);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlStudent)) {
                    ps.setString(1, userId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlProgress)) {
                    ps.setString(1, userId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
