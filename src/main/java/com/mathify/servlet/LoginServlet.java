package com.mathify.servlet;

import com.mathify.dao.UserDAO;
import com.mathify.model.Admin;
import com.mathify.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String email    = trim(req.getParameter("email"));
        String password = req.getParameter("password");

        if (email.isEmpty() || password == null || password.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=missing_fields");
            return;
        }

        try {
            Admin admin = userDAO.findAdminByEmail(email);
            if (admin != null && admin.login(email, password)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("userId",   admin.getUserId());
                session.setAttribute("userName", admin.getName());
                session.setAttribute("userRole", "ADMIN");
                
                // Set persistent auth cookie
                com.mathify.util.AuthUtil.addAuthCookie(resp, admin.getUserId());
                
                com.mathify.util.NavigationUtil.redirectWithLoading(req, resp, req.getContextPath() + "/admin/dashboard.jsp", "Preparing admin dashboard...");
                return;
            }

            Student student = userDAO.findStudentByEmail(email);
            if (student != null && student.login(email, password)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("userId",   student.getUserId());
                session.setAttribute("userName", student.getName());
                session.setAttribute("userRole", "STUDENT");
                
                // Set persistent auth cookie
                com.mathify.util.AuthUtil.addAuthCookie(resp, student.getUserId());
                
                com.mathify.util.NavigationUtil.redirectWithLoading(req, resp, req.getContextPath() + "/student/dashboard.do", "Loading your dashboard...");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp?error=invalid_credentials");
            }
        } catch (SQLException e) {
            getServletContext().log("Login DB error", e);
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=server_error");
        }
    }

    /** Redirect GET requests back to the login page. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
