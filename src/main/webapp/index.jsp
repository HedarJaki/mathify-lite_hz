<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("redirectUrl", request.getContextPath() + "/student/dashboard.do");
    request.setAttribute("loadingMessage", "Loading Mathify...");
    request.getRequestDispatcher("/loading.jsp").forward(request, response);
%>
