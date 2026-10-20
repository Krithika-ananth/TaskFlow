package com.dayflow.controller;

import com.dayflow.dao.UserDAO;
import com.dayflow.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        try {
            if (token == null || !new UserDAO().isPasswordResetTokenValid(token)) {
                req.setAttribute("error", "This reset link is invalid or has expired. Request a new one.");
            } else req.setAttribute("token", token);
            req.getRequestDispatcher("/reset-password.jsp").forward(req, resp);
        } catch (Exception ex) { throw new ServletException("Could not validate password reset link", ex); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token"), password = req.getParameter("password"), confirm = req.getParameter("confirmPassword");
        if (password == null || password.length() < 8 || password.length() > 128 || !password.equals(confirm)) {
            req.setAttribute("error", "Passwords must match and contain 8 to 128 characters.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/reset-password.jsp").forward(req, resp);
            return;
        }
        try {
            if (!new UserDAO().resetPassword(token, PasswordUtil.hash(password))) {
                req.setAttribute("error", "This reset link is invalid or has expired. Request a new one.");
                req.getRequestDispatcher("/reset-password.jsp").forward(req, resp);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/login?passwordReset=1");
        } catch (Exception ex) { throw new ServletException("Could not reset password", ex); }
    }
}
