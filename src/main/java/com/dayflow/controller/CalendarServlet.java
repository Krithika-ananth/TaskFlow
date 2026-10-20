package com.dayflow.controller;

import com.dayflow.dao.TaskDAO;
import com.dayflow.model.Task;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/calendar")
public class CalendarServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CalendarServlet.class.getName());
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMMM uuuu", Locale.ENGLISH);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LocalDate today = LocalDate.now();
        try {
            String rawDate = trim(request.getParameter("date"));
            String rawMonth = trim(request.getParameter("month"));
            LocalDate requestedDate = rawDate.isEmpty() ? null : LocalDate.parse(rawDate);
            YearMonth month = rawMonth.isEmpty()
                    ? requestedDate == null ? YearMonth.from(today) : YearMonth.from(requestedDate)
                    : YearMonth.parse(rawMonth);
            LocalDate selectedDate = requestedDate != null && YearMonth.from(requestedDate).equals(month)
                    ? requestedDate
                    : month.equals(YearMonth.from(today)) ? today : month.atDay(1);

            LocalDate firstOfMonth = month.atDay(1);
            LocalDate gridStart = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            int offset = firstOfMonth.getDayOfWeek().getValue() - 1;
            int gridDays = ((offset + month.lengthOfMonth() + 6) / 7) * 7;
            LocalDate gridEnd = gridStart.plusDays(gridDays);

            List<LocalDate> days = new ArrayList<>(gridDays);
            Map<LocalDate, List<Task>> tasksByDate = new LinkedHashMap<>();
            for (int i = 0; i < gridDays; i++) {
                LocalDate day = gridStart.plusDays(i);
                days.add(day);
                tasksByDate.put(day, new ArrayList<>());
            }

            User user = (User) request.getSession().getAttribute("user");
            List<Task> tasks = new TaskDAO().forAccessibleDateRange(user.getUserId(), gridStart, gridEnd);
            for (Task task : tasks) {
                List<Task> dayTasks = tasksByDate.get(task.getDueDate());
                if (dayTasks != null) dayTasks.add(task);
            }

            request.setAttribute("pageTitle", "Calendar");
            request.setAttribute("pageSubtitle", "See your plans take shape, one day at a time.");
            request.setAttribute("viewMonth", month);
            request.setAttribute("monthLabel", month.format(MONTH_LABEL));
            request.setAttribute("previousMonth", month.minusMonths(1));
            request.setAttribute("nextMonth", month.plusMonths(1));
            request.setAttribute("selectedDate", selectedDate);
            request.setAttribute("selectedDateLabel", selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)));
            request.setAttribute("today", today);
            request.setAttribute("calendarDays", days);
            request.setAttribute("calendarTasks", tasksByDate);
            request.setAttribute("dateTasks", tasksByDate.getOrDefault(selectedDate, List.of()));
            request.getRequestDispatcher("/calendar.jsp").forward(request, response);
        } catch (DateTimeException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Choose a valid calendar date.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load calendar for the signed-in user", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load your calendar. Please try again.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
