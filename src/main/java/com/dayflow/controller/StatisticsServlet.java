package com.dayflow.controller;

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

@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(StatisticsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        try {
            ProductivityStats stats = new StatisticsDAO().load(user.getUserId());
            request.setAttribute("stats", stats);
            request.setAttribute("pageTitle", "Statistics");
            request.setAttribute("pageSubtitle", "A personal picture of the progress you have made.");
            request.getRequestDispatcher("/statistics.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load statistics for user " + user.getUserId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your statistics. Please try again.");
        }
    }
}
