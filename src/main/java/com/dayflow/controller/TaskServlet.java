package com.dayflow.controller;

import com.dayflow.dao.TaskDAO;
import com.dayflow.dao.TaskShareDAO;
import com.dayflow.dao.AchievementDAO;
import com.dayflow.dao.StatisticsDAO;
import com.dayflow.model.Task;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.dayflow.model.Friend;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TaskServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        long userId = user.getUserId();
        String action = request.getParameter("action");
        try {
            TaskDAO dao = new TaskDAO();
            if ("view".equals(action)) {
                long taskId = parseId(request.getParameter("id"));
                Task task = dao.findAccessibleById(userId, taskId);
                if (task == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("task", task);
                request.setAttribute("participants", new TaskShareDAO().participants(task.getUserId(), taskId));
                request.setAttribute("pageTitle", "Task Details");
                request.setAttribute("pageSubtitle", "Review your plan and make your next move.");
                request.getRequestDispatcher("/task-details.jsp").forward(request, response);
                return;
            }

            String collection = "SHARED".equalsIgnoreCase(request.getParameter("collection")) ? "SHARED" : "MINE";
            String filter = request.getParameter("filter");
            String sort = request.getParameter("sort");
            String search = request.getParameter("q");
            List<Task> tasks = "SHARED".equals(collection)
                    ? dao.findSharedWithMe(userId, filter, sort, search)
                    : dao.findByUser(userId, filter, sort, search);
            request.setAttribute("tasks", tasks);
            request.setAttribute("collection", collection);
            request.setAttribute("filter", filter == null ? "ALL" : filter.toUpperCase(java.util.Locale.ROOT));
            request.setAttribute("sort", sort == null ? "DUE" : sort.toUpperCase(java.util.Locale.ROOT));
            request.setAttribute("search", search == null ? "" : search);
            request.setAttribute("pageTitle", "SHARED".equals(collection) ? "Shared With Me" : "My Tasks");
            request.setAttribute("pageSubtitle", "SHARED".equals(collection)
                    ? "Tasks your friends have invited you to work on."
                    : "Keep your plans clear and your progress moving.");
            request.getRequestDispatcher("/tasks.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load tasks for user " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your tasks. Please try again.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (!"delete".equals(request.getParameter("action"))) {
            String action = request.getParameter("action");
            if (!"complete".equals(action) && !"reopen".equals(action)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                long taskId = parseId(request.getParameter("id"));
                boolean completed = "complete".equals(action);
                TaskDAO taskDao = new TaskDAO();
                boolean privateOwnedTask = completed && taskDao.isPrivateOwnedTask(user.getUserId(), taskId);
                if (!taskDao.setCompletion(user.getUserId(), taskId, completed)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                if (privateOwnedTask) {
                    try {
                        var stats = new StatisticsDAO().load(user.getUserId());
                        new AchievementDAO().unlockEligible(user.getUserId(),
                                stats.getCompleted(), stats.getCurrentStreak());
                    } catch (Exception achievementError) {
                        LOGGER.log(Level.WARNING, "Task completed but progress unlock could not be refreshed for user "
                                + user.getUserId(), achievementError);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/tasks?" + (completed ? "completed=1" : "reopened=1"));
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unable to change task status for user " + user.getUserId(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "We couldn't update this task. Please try again.");
            }
            return;
        }
        try {
            long taskId = parseId(request.getParameter("id"));
            TaskDAO dao = new TaskDAO();
            Task accessible = dao.findAccessibleById(user.getUserId(), taskId);
            if (accessible != null && accessible.getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to delete this shared task.");
                return;
            }
            if (!dao.delete(user.getUserId(), taskId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/tasks?deleted=1");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to delete task for user " + user.getUserId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't delete this task. Please try again.");
        }
    }

    private long parseId(String rawId) {
        try {
            long id = Long.parseLong(rawId);
            if (id < 1) throw new IllegalArgumentException("Invalid task id");
            return id;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid task id", e);
        }
    }
}
