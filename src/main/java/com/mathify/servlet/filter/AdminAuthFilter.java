package com.mathify.servlet.filter;

import com.mathify.dao.UserDAO;
import com.mathify.model.Admin;
import com.mathify.model.Student;
import com.mathify.util.AuthUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 1. Check if user already has an active session
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            if ("ADMIN".equals(session.getAttribute("userRole"))) {
                chain.doFilter(request, response);
                return;
            } else {
                // Logged in as student, not an admin
                com.mathify.util.NavigationUtil.redirectWithLoading(req, resp, req.getContextPath() + "/student/dashboard.do", "Redirecting to student dashboard...");
                return;
            }
        }

        // 2. No session, check for the persistent auth cookie
        String token = AuthUtil.getAuthCookieValue(req);
        if (token != null) {
            String userId = AuthUtil.validateToken(token);
            if (userId != null) {
                try {
                    // 3. Validate user exists in DB
                    Admin admin = userDAO.getAdminById(userId);
                    if (admin != null) {
                        // 4. Recreate the admin session
                        session = req.getSession(true);
                        session.setAttribute("userId", admin.getUserId());
                        session.setAttribute("userName", admin.getName());
                        session.setAttribute("userRole", "ADMIN");
                        
                        chain.doFilter(request, response);
                        return;
                    }
                    
                    Student student = userDAO.getStudentById(userId);
                    if (student != null) {
                        // 4. Recreate the student session and redirect away
                        session = req.getSession(true);
                        session.setAttribute("userId", student.getUserId());
                        session.setAttribute("userName", student.getName());
                        session.setAttribute("userRole", "STUDENT");
                        
                        com.mathify.util.NavigationUtil.redirectWithLoading(req, resp, req.getContextPath() + "/student/dashboard.do", "Redirecting to student dashboard...");
                        return;
                    }
                } catch (SQLException e) {
                    req.getServletContext().log("DB error during cookie auth", e);
                }
            }
        }

        // 5. If we reach here, there's no valid session or cookie. Redirect to login.
        AuthUtil.clearAuthCookie(resp);
        com.mathify.util.NavigationUtil.redirectWithLoading(req, resp, req.getContextPath() + "/login.jsp", "Redirecting to login...");
    }
}
