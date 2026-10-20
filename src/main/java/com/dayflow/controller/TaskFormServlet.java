package com.dayflow.controller;

import com.dayflow.dao.TaskDAO;
import com.dayflow.dao.CategoryDAO;
import com.dayflow.dao.FriendDAO;
import com.dayflow.dao.TaskShareDAO;
import com.dayflow.model.Friend;
import com.dayflow.model.Task;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/task-form")
public class TaskFormServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TaskFormServlet.class.getName());
    private static final Set<String> PRIORITIES = Set.of("LOW", "MEDIUM", "HIGH");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long userId = currentUser(request).getUserId();
            String rawId = request.getParameter("id");
            Task task = null;
            if (rawId != null && !rawId.isBlank()) {
                long taskId = parseId(rawId);
                task = new TaskDAO().findById(userId, taskId);
                if (task == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
            request.setAttribute("task", task == null ? new Task() : task);
            request.setAttribute("categories", new CategoryDAO().findByUser(userId));
            populateShareChoices(request, userId, task);
            setPage(request, task == null ? "Add Task" : "Edit Task");
            request.getRequestDispatcher("/task-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load task form", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load the task form. Please try again.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        String rawId = trim(request.getParameter("id"));
        String title = trim(request.getParameter("title"));
        String description = trim(request.getParameter("description"));
        String priority = trim(request.getParameter("priority")).toUpperCase(java.util.Locale.ROOT);
        String rawDueDate = trim(request.getParameter("dueDate"));
        String rawCategoryId = trim(request.getParameter("categoryId"));
        List<Long> sharedWith;
        try { sharedWith = parseIds(request.getParameterValues("sharedWith")); }
        catch (IllegalArgumentException e) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Choose valid friends to share with."); return; }
        Task task = new Task();
        task.setUserId(user.getUserId());
        task.setTitle(title);
        task.setDescription(description.isEmpty() ? null : description);
        task.setPriority(priority);
        task.setImportant("on".equals(request.getParameter("important")));

        if (!rawId.isEmpty()) {
            try {
                task.setTaskId(parseId(rawId));
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            try {
                if (new TaskDAO().findById(user.getUserId(), task.getTaskId()) == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to verify task ownership for user " + user.getUserId(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "We couldn't load this task. Please try again.");
                return;
            }
        }

        if (!rawCategoryId.isEmpty()) {
            try {
                task.setCategoryId(parseId(rawCategoryId));
            } catch (IllegalArgumentException e) {
                showForm(request, response, task, "Choose one of your available categories.");
                return;
            }
        }

        String error = validate(title, description, priority, rawDueDate, task);
        if (error == null && task.getCategoryId() != null) {
            try {
                if (new CategoryDAO().findById(user.getUserId(), task.getCategoryId()) == null) {
                    error = "Choose one of your available categories.";
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to verify category ownership for user " + user.getUserId(), e);
                error = "We couldn't verify that category. Please try again.";
            }
        }
        if (error != null) {
            showForm(request, response, task, error);
            return;
        }

        try {
            TaskDAO dao = new TaskDAO();
            boolean created = task.getTaskId() == 0;
            if (task.getTaskId() > 0) {
                if (dao.findById(user.getUserId(), task.getTaskId()) == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                if (!dao.update(task)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } else {
                task.setTaskId(dao.add(task));
            }
            if (!new TaskShareDAO().replaceParticipants(user.getUserId(), task.getTaskId(), sharedWith)) {
                if (created) dao.delete(user.getUserId(), task.getTaskId());
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/tasks?action=view&id=" + task.getTaskId()
                    + "&saved=1");
        } catch (IllegalArgumentException e) {
            showForm(request, response, task, "You can only share tasks with accepted friends.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to save task for user " + user.getUserId(), e);
            showForm(request, response, task, "We couldn't save this task. Please try again.");
        }
    }

    private String validate(String title, String description, String priority, String rawDueDate, Task task) {
        if (title.isBlank()) return "Add a title before saving your task.";
        if (title.length() > 180) return "Task titles must be 180 characters or fewer.";
        if (description.length() > 2000) return "Descriptions must be 2,000 characters or fewer.";
        if (!PRIORITIES.contains(priority)) return "Choose a valid priority.";
        if (!rawDueDate.isEmpty()) {
            try {
                task.setDueDate(LocalDate.parse(rawDueDate));
            } catch (DateTimeParseException e) {
                return "Enter a valid due date.";
            }
        }
        return null;
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Task task, String error)
            throws ServletException, IOException {
        request.setAttribute("task", task);
        request.setAttribute("error", error);
        try {
            request.setAttribute("categories", new CategoryDAO().findByUser(task.getUserId()));
            populateShareChoices(request, task.getUserId(), task.getTaskId() > 0 ? task : null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load categories for task form", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your categories. Please try again.");
            return;
        }
        setPage(request, task.getTaskId() > 0 ? "Edit Task" : "Add Task");
        request.getRequestDispatcher("/task-form.jsp").forward(request, response);
    }

    private void setPage(HttpServletRequest request, String title) {
        request.setAttribute("pageTitle", title);
        request.setAttribute("pageSubtitle", "A little progress goes a long way.");
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    private long parseId(String value) {
        try {
            long id = Long.parseLong(value);
            if (id < 1) throw new IllegalArgumentException("Invalid task id");
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task id", e);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private List<Long> parseIds(String[] values) {
        List<Long> ids = new java.util.ArrayList<>();
        if (values == null) return ids;
        if (values.length > 50) throw new IllegalArgumentException("Too many participants");
        for (String value : values) {
            try { long id = Long.parseLong(value); if (id < 1) throw new NumberFormatException(); ids.add(id); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid participant", e); }
        }
        return ids;
    }

    private void populateShareChoices(HttpServletRequest request, long userId, Task task) throws Exception {
        List<Friend> choices = new ArrayList<>(new FriendDAO().friends(userId));
        List<Long> selected = new ArrayList<>();
        if (task != null) {
            List<Friend> current = new TaskShareDAO().participants(userId, task.getTaskId());
            for (Friend person : current) {
                selected.add(person.getUserId());
                if (choices.stream().noneMatch(friend -> friend.getUserId() == person.getUserId())) choices.add(person);
            }
        }
        request.setAttribute("shareChoices", choices);
        request.setAttribute("selectedShareIds", selected);
    }
}
