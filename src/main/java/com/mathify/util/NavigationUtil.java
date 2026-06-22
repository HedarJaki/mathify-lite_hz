package com.mathify.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class NavigationUtil {

    /**
     * Redirects the user to the specified URL, while showing a loading screen.
     * This is useful for heavy operations or major screen transitions.
     * 
     * @param req The HttpServletRequest
     * @param resp The HttpServletResponse
     * @param url The target URL to redirect to
     * @param message The message to display on the loading screen
     */
    public static void redirectWithLoading(HttpServletRequest req, HttpServletResponse resp, String url, String message) throws ServletException, IOException {
        req.setAttribute("redirectUrl", url);
        req.setAttribute("loadingMessage", message);
        req.getRequestDispatcher("/loading.jsp").forward(req, resp);
    }
}
