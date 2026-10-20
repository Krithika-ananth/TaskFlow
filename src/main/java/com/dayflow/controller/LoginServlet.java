package com.dayflow.controller;
import com.dayflow.dao.UserDAO; import com.dayflow.model.User; import com.dayflow.util.PasswordUtil; import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import java.io.IOException;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException {
        if(request.getSession(false)!=null&&request.getSession(false).getAttribute("user")!=null){response.sendRedirect(request.getContextPath()+"/dashboard");return;}
        if(request.getCookies()!=null)for(Cookie cookie:request.getCookies())if("dayflow_user".equals(cookie.getName()))request.setAttribute("rememberedLogin",cookie.getValue());
        request.getRequestDispatcher("/login.jsp").forward(request,response);
    }
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException {
        try {
            String login=request.getParameter("usernameOrEmail");if(login==null)login=request.getParameter("email");String password=request.getParameter("password");
            if(login==null||password==null||login.isBlank()||password.isBlank()){request.setAttribute("error","Username/email and password are required.");request.setAttribute("rememberedLogin",login);doGet(request,response);return;}
            User user=new UserDAO().findByLogin(login.trim());
            if(user==null||!PasswordUtil.verify(password,user.getPasswordHash())){request.setAttribute("error","Invalid username/email or password.");request.setAttribute("rememberedLogin",login);doGet(request,response);return;}
            if(PasswordUtil.needsRehash(user.getPasswordHash())){String upgradedHash=PasswordUtil.hash(password);new UserDAO().updatePassword(user.getUserId(),upgradedHash);user.setPasswordHash(upgradedHash);}
            HttpSession session=request.getSession(true);request.changeSessionId();session.setAttribute("user",user);
            if("on".equals(request.getParameter("remember"))){Cookie cookie=new Cookie("dayflow_user",user.getUsername());cookie.setMaxAge(60*60*24*30);cookie.setHttpOnly(true);cookie.setPath(request.getContextPath().isBlank()?"/":request.getContextPath());response.addCookie(cookie);}
            response.sendRedirect(request.getContextPath()+"/dashboard");
        } catch(Exception ex){throw new ServletException(ex);}
    }
}
