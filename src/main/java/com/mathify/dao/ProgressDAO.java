package com.mathify.dao;

import com.mathify.db.DBUtil;
import com.mathify.model.UserProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgressDAO {

    public record EnrolledCourse(String courseId, String title, String category, int progressPercent) {}

    /**
     * Loads the high-level progress (xp, level, streak) for a student.
     */
    public UserProgress getUserProgress(String studentId) throws SQLException {
        UserProgress progress = new UserProgress(studentId);
        String sql = "SELECT total_xp, level, current_streak FROM user_progress WHERE student_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    progress.addXP(rs.getInt("total_xp"));
                    // UserProgress level defaults to 0 natively.
                    progress.addLevel(rs.getInt("level")); 
                    progress.addStreak(rs.getInt("current_streak"));
                }
            }
        }
        return progress;
    }

    /**
     * Gets the count of courses the student has completed.
     */
    public int getCompletedCoursesCount(String studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM course_enrollments WHERE student_id = ? AND completed_at IS NOT NULL";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Gets courses the student is currently enrolled in (not necessarily completed).
     * Calculates a simple progress percentage based on completed chapters vs total chapters.
     */
    public List<EnrolledCourse> getRecentEnrollments(String studentId) throws SQLException {
        List<EnrolledCourse> list = new ArrayList<>();
        String sql = 
            "SELECT c.course_id, c.title, c.category, " +
            "  (SELECT COUNT(*) FROM chapters ch WHERE ch.course_id = c.course_id) AS total_chapters, " +
            "  (SELECT COUNT(*) FROM chapter_progress cp " +
            "   JOIN chapters ch ON cp.chapter_id = ch.chapter_id " +
            "   WHERE ch.course_id = c.course_id AND cp.student_id = ?) AS completed_chapters " +
            "FROM course_enrollments ce " +
            "JOIN courses c ON ce.course_id = c.course_id " +
            "WHERE ce.student_id = ? " +
            "ORDER BY ce.enrolled_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String courseId = rs.getString("course_id");
                    String title = rs.getString("title");
                    String category = rs.getString("category");
                    int total = rs.getInt("total_chapters");
                    int completed = rs.getInt("completed_chapters");
                    
                    int percent = (total > 0) ? (int) Math.round((double) completed / total * 100.0) : 0;
                    list.add(new EnrolledCourse(courseId, title, category, percent));
                }
            }
        }
        return list;
    }
}
