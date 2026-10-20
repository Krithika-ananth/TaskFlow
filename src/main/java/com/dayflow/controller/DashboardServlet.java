package com.dayflow.controller;

import com.dayflow.dao.ProductivityDAO;
import com.dayflow.dao.TaskDAO;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            long userId = user.getUserId();
            TaskDAO tasks = new TaskDAO();

            request.setAttribute("pageTitle", "Dashboard");
            request.setAttribute("pageSubtitle", "Small steps become momentum.");
            request.setAttribute("todayCount", tasks.count(userId, "AND DUE_DATE=TRUNC(SYSDATE)"));
            request.setAttribute("completed", tasks.count(userId, "AND STATUS='COMPLETED'"));
            request.setAttribute("upcomingCount", tasks.count(userId,
                    "AND STATUS='PENDING' AND DUE_DATE>=TRUNC(SYSDATE)"));

            // Keep the existing, real task overview intact; these are not placeholder values.
            request.setAttribute("total", tasks.count(userId, ""));
            request.setAttribute("pending", tasks.count(userId, "AND STATUS='PENDING'"));
            request.setAttribute("overdue", tasks.count(userId,
                    "AND STATUS='PENDING' AND DUE_DATE<TRUNC(SYSDATE)"));
            request.setAttribute("streak", new ProductivityDAO().currentStreak(userId));
            request.setAttribute("todayTasks", tasks.today(userId));
            request.setAttribute("upcoming", tasks.upcoming(userId));
            request.setAttribute("overdueTasks", tasks.overdue(userId));
            request.setAttribute("categories", tasks.categoryCounts(userId));
            request.setAttribute("weekly", tasks.weeklyCompleted(userId));
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
