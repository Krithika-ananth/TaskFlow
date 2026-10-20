package com.dayflow.controller;

import com.dayflow.dao.FriendDAO;
import com.dayflow.model.Friend;
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

@WebServlet("/friends")
public class FriendServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(FriendServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        FriendDAO dao = new FriendDAO();
        try {
            request.setAttribute("friends", dao.friends(user.getUserId()));
            request.setAttribute("incoming", dao.incoming(user.getUserId()));
            String query = request.getParameter("q");
            request.setAttribute("query", query == null ? "" : query.trim());
            if (query != null && !query.isBlank()) request.setAttribute("results", dao.search(user.getUserId(), query));

            String profileId = request.getParameter("profile");
            if (profileId != null) {
                long id = parseId(profileId);
                Friend profile = dao.publicProfile(user.getUserId(), id);
                if (profile == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                request.setAttribute("publicProfile", profile);
            }
            request.getRequestDispatcher("/friends.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to load friends for user " + user.getUserId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't load Friends right now. Please try again.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String action = request.getParameter("action");
        String result;
        try {
            FriendDAO dao = new FriendDAO();
            switch (action == null ? "" : action) {
                case "send" -> {
                    String state = dao.send(user.getUserId(), parseId(request.getParameter("userId")));
                    result = switch (state) {
                        case "SELF" -> "self";
                        case "FRIENDS" -> "already-friends";
                        case "SENT" -> "sent";
                        case "RECEIVED" -> "incoming";
                        default -> "sent";
                    };
                }
                case "accept", "reject" -> result = dao.respond(user.getUserId(),
                        parseId(request.getParameter("requestId")), "accept".equals(action))
                        ? ("accept".equals(action) ? "accepted" : "rejected") : "not-found";
                case "remove" -> result = dao.remove(user.getUserId(), parseId(request.getParameter("userId")))
                        ? "removed" : "not-found";
                default -> { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
            }
            response.sendRedirect(request.getContextPath() + "/friends?message=" + result);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "That friend action wasn't valid.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to update friend relationship for user " + user.getUserId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "We couldn't update that friend request. Please try again.");
        }
    }

    private long parseId(String raw) {
        try { long id = Long.parseLong(raw); if (id > 0) return id; }
        catch (NumberFormatException | NullPointerException ignored) { }
        throw new IllegalArgumentException("Invalid id");
    }
}
