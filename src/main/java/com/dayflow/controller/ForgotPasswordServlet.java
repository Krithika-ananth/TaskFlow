package com.dayflow.controller;

import com.dayflow.dao.UserDAO;
import com.dayflow.model.User;
import com.dayflow.util.PasswordResetMailer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final String MESSAGE = "If an account exists for that email, a password reset link has been sent.";

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isBlank() || email.length() > 180 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            req.setAttribute("error", "Enter a valid email address.");
            doGet(req, resp);
            return;
        }
        try {
            User user = new UserDAO().findByEmail(email.trim());
            if (user != null) {
                String token = new UserDAO().createPasswordResetToken(user.getUserId());
                String link = req.getScheme() + "://" + req.getServerName()
                        + (req.getServerPort() == 80 || req.getServerPort() == 443 ? "" : ":" + req.getServerPort())
                        + req.getContextPath() + "/reset-password?token="
                        + URLEncoder.encode(token, StandardCharsets.UTF_8);
                PasswordResetMailer.send(user.getEmail(), link);
            }
            req.setAttribute("message", MESSAGE);
            doGet(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Could not process password reset request", ex);
        }
    }
}
