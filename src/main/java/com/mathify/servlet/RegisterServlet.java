package com.mathify.servlet;

import com.mathify.dao.UserDAO;
import com.mathify.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String name     = trim(req.getParameter("fullName"));
        String email    = trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        String terms    = req.getParameter("terms");

        if (name.isEmpty() || email.isEmpty() || password == null || password.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=missing_fields");
            return;
        }
        if (password.length() < 8) {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=weak_password");
            return;
        }
        if (terms == null) {
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=terms_required");
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                resp.sendRedirect(req.getContextPath() + "/register.jsp?error=email_taken");
                return;
            }
            userDAO.createStudent(name, email, User.hash(password));
            resp.sendRedirect(req.getContextPath() + "/login.jsp?registered=true");
        } catch (SQLException e) {
            getServletContext().log("Register DB error", e);
            resp.sendRedirect(req.getContextPath() + "/register.jsp?error=server_error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/register.jsp");
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
