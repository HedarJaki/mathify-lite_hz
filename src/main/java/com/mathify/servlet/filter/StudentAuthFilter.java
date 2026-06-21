package com.mathify.servlet.filter;

import com.mathify.dao.UserDAO;
import com.mathify.model.Student;
import com.mathify.util.AuthUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebFilter("/student/*")
public class StudentAuthFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 1. Check if user already has an active session
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null && "STUDENT".equals(session.getAttribute("userRole"))) {
            chain.doFilter(request, response);
            return;
        }

        // 2. No session, check for the persistent auth cookie
        String token = AuthUtil.getAuthCookieValue(req);
        if (token != null) {
            String userId = AuthUtil.validateToken(token);
            if (userId != null) {
                try {
                    // 3. Validate user exists in DB
                    Student student = userDAO.getStudentById(userId);
                    if (student != null) {
                        // 4. Recreate the session and populate attributes
                        session = req.getSession(true);
                        session.setAttribute("userId", student.getUserId());
                        session.setAttribute("userName", student.getName());
                        session.setAttribute("userRole", "STUDENT");
                        
                        // Proceed to the requested page
                        chain.doFilter(request, response);
                        return;
                    }
                } catch (SQLException e) {
                    req.getServletContext().log("DB error during cookie auth", e);
                }
            }
        }

        // 5. If we reach here, there's no session and no valid cookie. Redirect to login.
        // Clear cookie just in case it was invalid.
        AuthUtil.clearAuthCookie(resp);
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
