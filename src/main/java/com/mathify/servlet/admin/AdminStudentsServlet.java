package com.mathify.servlet.admin;

import com.mathify.dao.UserDAO;
import com.mathify.model.AdminStudentDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/students.do")
public class AdminStudentsServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<AdminStudentDTO> students = userDAO.getAllStudentSummaries();
            req.setAttribute("students", students);
            req.setAttribute("studentsLoaded", true);
            req.getRequestDispatcher("/admin/students.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("DB error fetching admin students list", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading students.");
        }
    }
}
