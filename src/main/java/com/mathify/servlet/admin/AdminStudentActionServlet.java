package com.mathify.servlet.admin;

import com.mathify.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/student-action.do")
public class AdminStudentActionServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String studentId = req.getParameter("studentId");
        
        if (action == null || studentId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/students.do");
            return;
        }
        
        try {
            switch (action) {
                case "disable":
                    userDAO.setStudentDisabled(studentId, true);
                    break;
                case "enable":
                    userDAO.setStudentDisabled(studentId, false);
                    break;
                case "delete":
                    userDAO.deleteUser(studentId);
                    break;
            }
        } catch (SQLException e) {
            getServletContext().log("Error executing student action: " + action, e);
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/students.do");
    }
}
