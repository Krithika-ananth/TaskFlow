package com.dayflow.controller;
import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/pomodoro") public class PomodoroServlet extends HttpServlet{protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{req.getRequestDispatcher("/pomodoro.jsp").forward(req,resp);}}
