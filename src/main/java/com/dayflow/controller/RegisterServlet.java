package com.dayflow.controller;
import com.dayflow.dao.UserDAO; import com.dayflow.util.PasswordUtil; import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import java.io.IOException;
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException {
        request.getRequestDispatcher("/register.jsp").forward(request,response);
    }
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException {
        String name=value(request,"fullName"),username=value(request,"username"),email=value(request,"email").toLowerCase(),password=request.getParameter("password"),confirm=request.getParameter("confirmPassword");
        request.setAttribute("fullName",name);request.setAttribute("username",username);request.setAttribute("email",email);
        if(name.isBlank()||name.length()>120||!username.matches("[A-Za-z0-9._-]{3,30}")||email.length()>180||!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")||password==null||password.length()<8||password.length()>128||!password.equals(confirm)){
            request.setAttribute("error","Check your name, username, email and password. Usernames need 3–30 letters, numbers, dots, underscores or hyphens; passwords need 8–128 characters and must match.");doGet(request,response);return;
        }
        try {
            UserDAO dao=new UserDAO();
            if(dao.usernameExists(username)){request.setAttribute("error","That username is already taken.");doGet(request,response);return;}
            if(dao.emailExists(email)){request.setAttribute("error","An account with that email already exists.");doGet(request,response);return;}
            dao.create(name,username,email,PasswordUtil.hash(password));response.sendRedirect(request.getContextPath()+"/login?registered=1");
        } catch(Exception ex) {
            if(ex instanceof java.sql.SQLException sql && sql.getErrorCode()==1){request.setAttribute("error","That username or email is already in use. Please choose another.");doGet(request,response);return;}
            throw new ServletException(ex);
        }
    }
    private String value(HttpServletRequest request,String field){String value=request.getParameter(field);return value==null?"":value.trim();}
}
