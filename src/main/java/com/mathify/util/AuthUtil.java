package com.mathify.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthUtil {
    
    private static final String SECRET_KEY = "mathify-secret-key-2026-very-secure";
    public static final String COOKIE_NAME = "mathify_auth";
    public static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60; // 30 days

    /**
     * Generates a secure token combining the userId and a hash signature.
     * Format: base64(userId + ":" + SHA256(userId + secret))
     */
    public static String generateToken(String userId) {
        String signature = hash(userId + SECRET_KEY);
        String raw = userId + ":" + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates a token and returns the userId if valid, or null if invalid.
     */
    public static String validateToken(String token) {
        if (token == null || token.isEmpty()) return null;
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length != 2) return null;
            
            String userId = parts[0];
            String signature = parts[1];
            
            String expectedSignature = hash(userId + SECRET_KEY);
            if (expectedSignature.equals(signature)) {
                return userId;
            }
        } catch (Exception e) {
            // Invalid base64 or other parsing error
        }
        return null;
    }

    /**
     * Adds the auth cookie to the response.
     */
    public static void addAuthCookie(HttpServletResponse resp, String userId) {
        String token = generateToken(userId);
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/"); // Available across the whole app
        cookie.setHttpOnly(true); // Protect against XSS
        resp.addCookie(cookie);
    }
    
    /**
     * Clears the auth cookie from the response.
     */
    public static void clearAuthCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    /**
     * Retrieves the auth cookie from the request.
     */
    public static String getAuthCookieValue(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
