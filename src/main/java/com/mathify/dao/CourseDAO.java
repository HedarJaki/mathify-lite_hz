package com.mathify.dao;

import com.mathify.db.DBUtil;
import com.mathify.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, title, description, category FROM courses ORDER BY created_at ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getString("course_id"));
                course.setTitle(rs.getString("title"));
                course.setDescription(rs.getString("description"));
                course.setCategory(rs.getString("category"));
                courses.add(course);
            }
        }
        return courses;
    }

    public Course getCourseById(String courseId) throws SQLException {
        Course course = null;
        String sql = "SELECT course_id, title, description, category FROM courses WHERE course_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    course = new Course();
                    course.setCourseId(rs.getString("course_id"));
                    course.setTitle(rs.getString("title"));
                    course.setDescription(rs.getString("description"));
                    course.setCategory(rs.getString("category"));
                }
            }
        }

        if (course != null) {
            course.setChapters(getChaptersForCourse(courseId));
        }

        return course;
    }

    public List<Chapter> getChaptersForCourse(String courseId) throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT chapter_id, title, description, xp_reward, order_index FROM chapters WHERE course_id = ? ORDER BY order_index ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Chapter chapter = new Chapter();
                    chapter.setChapterId(rs.getString("chapter_id"));
                    chapter.setTitle(rs.getString("title"));
                    chapter.setDescription(rs.getString("description"));
                    chapter.setXpReward(rs.getInt("xp_reward"));
                    chapters.add(chapter);
                }
            }
        }
        return chapters;
    }

    public void loadChapterContent(Chapter chapter) throws SQLException {
        chapter.setModules(getModulesForChapter(chapter.getChapterId()));
        chapter.setQuizzes(getQuizzesForChapter(chapter.getChapterId()));
    }

    private List<LearningModule> getModulesForChapter(String chapterId) throws SQLException {
        List<LearningModule> modules = new ArrayList<>();
        String sql = "SELECT module_id, title, order_index, module_type, content_url, duration_secs, slide_count, created_at " +
                     "FROM learning_modules WHERE chapter_id = ? ORDER BY order_index ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chapterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("module_id");
                    String title = rs.getString("title");
                    int orderIndex = rs.getInt("order_index");
                    java.sql.Timestamp createdAtTs = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : LocalDateTime.now();

                    ModuleInfo info = new ModuleInfo(id, title, orderIndex, createdAt);
                    String typeStr = rs.getString("module_type");

                    if ("VIDEO".equals(typeStr)) {
                        int durationSecs = rs.getInt("duration_secs");
                        String url = rs.getString("content_url");
                        modules.add(new VideoModule(info, url, Duration.ofSeconds(durationSecs), null));
                    } else if ("SLIDE".equals(typeStr)) {
                        int slideCount = rs.getInt("slide_count");
                        List<Slide> slides = new ArrayList<>();
                        for (int i = 0; i < slideCount; i++) {
                            slides.add(new Slide(i, "", "")); // mock slide
                        }
                        modules.add(new SlideModule(info, slides, 30));
                    }
                }
            }
        }
        return modules;
    }

    private List<Quiz> getQuizzesForChapter(String chapterId) throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT quiz_id, title, passing_score FROM quizzes WHERE chapter_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chapterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz();
                    quiz.setQuizId(rs.getString("quiz_id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setPassingScore(rs.getInt("passing_score"));
                    
                    quiz.setQuestions(getQuestionsForQuiz(quiz.getQuizId()));
                    quizzes.add(quiz);
                }
            }
        }
        return quizzes;
    }

    private List<Question> getQuestionsForQuiz(String quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, prompt, points, question_type, order_index FROM questions WHERE quiz_id = ? ORDER BY order_index ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final String qId = rs.getString("question_id");
                    final int points = rs.getInt("points");
                    final String type = rs.getString("question_type");
                    
                    questions.add(new Question() {
                        @Override public QuestionInfo getInfo() { return new QuestionInfo(qId, "", points); }
                        @Override public QuestionType getType() { return QuestionType.valueOf(type); }
                        @Override public boolean evaluate(Answer a) { return false; }
                    });
                }
            }
        }
        return questions;
    }
}
