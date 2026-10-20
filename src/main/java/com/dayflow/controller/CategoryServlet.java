package com.dayflow.controller;

import com.dayflow.dao.CategoryDAO;
import com.dayflow.model.Category;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long userId = currentUser(request).getUserId();
            showPage(request, response, new CategoryDAO().findByUser(userId), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load categories", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your categories. Please try again.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long userId = currentUser(request).getUserId();
        String action = request.getParameter("action");
        String name = trim(request.getParameter("name"));
        try {
            CategoryDAO categories = new CategoryDAO();
            if ("create".equals(action) || "rename".equals(action)) {
                if (name.isBlank() || name.length() > 80) {
                    showPage(request, response, categories.findByUser(userId),
                            "Category names must contain 1 to 80 characters.");
                    return;
                }
                if ("create".equals(action)) {
                    categories.create(userId, name);
                    response.sendRedirect(request.getContextPath() + "/categories?created=1");
                    return;
                }
                long categoryId = parseId(request.getParameter("id"));
                if (categories.findById(userId, categoryId) == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                categories.rename(userId, categoryId, name);
                response.sendRedirect(request.getContextPath() + "/categories?updated=1");
                return;
            }
            if ("delete".equals(action)) {
                long categoryId = parseId(request.getParameter("id"));
                if (!categories.delete(userId, categoryId)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                response.sendRedirect(request.getContextPath() + "/categories?deleted=1");
                return;
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            if (e instanceof java.sql.SQLException sql && sql.getErrorCode() == 1) {
                try {
                    showPage(request, response, new CategoryDAO().findByUser(userId),
                            "You already have a category with that name.");
                } catch (Exception loadError) {
                    LOGGER.log(Level.SEVERE, "Unable to reload categories after duplicate name", loadError);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                return;
            }
            LOGGER.log(Level.SEVERE, "Unable to update categories for user " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't save this category. Please try again.");
        }
    }

    private void showPage(HttpServletRequest request, HttpServletResponse response,
                          List<Category> categories, String error) throws ServletException, IOException {
        request.setAttribute("categories", categories);
        request.setAttribute("error", error);
        request.setAttribute("pageTitle", "Your Categories");
        request.setAttribute("pageSubtitle", "Give your plans a little shape.");
        request.getRequestDispatcher("/categories.jsp").forward(request, response);
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    private long parseId(String raw) {
        try {
            long id = Long.parseLong(raw);
            if (id < 1) throw new IllegalArgumentException("Invalid category id");
            return id;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid category id", e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
