package com.dayflow.controller;

import com.dayflow.dao.TaskHistoryDAO;
import com.dayflow.model.TaskHistoryEvent;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(HistoryServlet.class.getName());
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String filter = trim(request.getParameter("type")).toUpperCase(Locale.ROOT);
        if (!List.of("ALL", "CREATED", "COMPLETED").contains(filter)) filter = "ALL";
        String search = trim(request.getParameter("q"));
        if (search.length() > 100) search = search.substring(0, 100);

        try {
            List<TaskHistoryEvent> events = new TaskHistoryDAO()
                    .findAvailableEvents(user.getUserId(), filter, search);
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            Map<String, List<TaskHistoryEvent>> groups = new LinkedHashMap<>();
            for (TaskHistoryEvent event : events) {
                if (event.getOccurredAt() == null) continue;
                LocalDate eventDate = event.getOccurredAt().toLocalDate();
                String group = eventDate.equals(today) ? "Today"
                        : eventDate.equals(today.minusDays(1)) ? "Yesterday"
                        : !eventDate.isBefore(weekStart) ? "Earlier this week" : "Earlier";
                event.setDateGroup(group);
                String time = event.getOccurredAt().format(TIME);
                event.setTimeLabel(group.equals("Today") || group.equals("Yesterday")
                        ? time : event.getOccurredAt().format(DateTimeFormatter.ofPattern("MMM d, ", Locale.ENGLISH)) + time);
                groups.computeIfAbsent(group, ignored -> new java.util.ArrayList<>()).add(event);
            }

            request.setAttribute("pageTitle", "Task History");
            request.setAttribute("pageSubtitle", "A quiet look at the steps you have taken.");
            request.setAttribute("historyGroups", groups);
            request.setAttribute("historyType", filter);
            request.setAttribute("historySearch", search);
            request.getRequestDispatcher("/history.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load task history for user " + user.getUserId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your task history. Please try again.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
