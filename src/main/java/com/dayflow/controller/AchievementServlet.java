package com.dayflow.controller;

import com.dayflow.dao.AchievementDAO;
import com.dayflow.dao.StatisticsDAO;
import com.dayflow.model.ProductivityStats;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/achievements")
public class AchievementServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AchievementServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            long userId = user.getUserId();
            ProductivityStats stats = new StatisticsDAO().load(userId);
            AchievementDAO achievements = new AchievementDAO();
            achievements.unlockEligible(userId, stats.getCompleted(), stats.getCurrentStreak());
            request.setAttribute("achievements", achievements.all(userId,
                    stats.getCompleted(), stats.getCurrentStreak()));
            request.setAttribute("stats", stats);
            request.setAttribute("pageTitle", "Achievements");
            request.setAttribute("pageSubtitle", "Celebrate milestones built from your real progress.");
            request.getRequestDispatcher("/achievements.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load achievements for user " + user.getUserId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your achievements. Please try again.");
        }
    }
}
