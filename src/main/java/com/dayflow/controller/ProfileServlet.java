package com.dayflow.controller;

import com.dayflow.dao.UserDAO;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final DateTimeFormatter MEMBER_SINCE = DateTimeFormatter.ofPattern("MMMM uuuu");

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        request.setAttribute("pageTitle", "Your Profile");
        request.setAttribute("pageSubtitle", "Your details, just the way you like them.");
        request.setAttribute("memberSince", user.getCreatedAt() == null ? "Not available"
                : user.getCreatedAt().format(MEMBER_SINCE));
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String name = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email")).toLowerCase();
        request.setAttribute("formName", name);
        request.setAttribute("formEmail", email);

        if (name.isBlank() || name.length() > 120 || email.length() > 180
                || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            request.setAttribute("error", "Enter a name and a valid email address.");
            doGet(request, response);
            return;
        }

        try {
            UserDAO users = new UserDAO();
            if (users.emailExistsForOther(email, user.getUserId())) {
                request.setAttribute("error", "That email address is already used by another account.");
                doGet(request, response);
                return;
            }
            users.updateProfile(user.getUserId(), name, email);
            user.setFullName(name);
            user.setEmail(email);
            request.getSession().setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/profile?saved=1");
        } catch (Exception e) {
            if (e instanceof java.sql.SQLException sql && sql.getErrorCode() == 1) {
                request.setAttribute("error", "That email address is already used by another account.");
                doGet(request, response);
                return;
            }
            throw new ServletException(e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
