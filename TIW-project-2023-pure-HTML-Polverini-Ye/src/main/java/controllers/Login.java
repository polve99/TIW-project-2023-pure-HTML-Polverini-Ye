package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.User;
import dao.UserDAO;
import utilis.ConnectionHandler;
import utilis.ThymeleafTemplateEngineCreator;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;

    public Login() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = "WEB-INF/index.html";
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isValid=true;

        String userMail = request.getParameter("userMail");

        Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
        boolean emailValid = emailPattern.matcher(userMail).matches();

        if (userMail==null || userMail.isBlank() || userMail.length() < 5 || userMail.length() > 50 || !emailValid) {
            request.setAttribute("emailErrorMessage", "Invalid email. Email must be between 5 and 50 characters and have a valid format (e.g., email@mail.com)");
            isValid=false;
        }

        String passw = request.getParameter("password");
        if (passw==null || passw.isBlank() || passw.length() < 8 || passw.length() > 50) {
            request.setAttribute("passwordErrorMessage", "Password must be between 8 and 50 characters");
            isValid=false;
        }

        UserDAO userDAO = new UserDAO(connection);
        User user = null;

        if(isValid) {
            try {
                user = userDAO.getUserAfterAuthentication(userMail, passw);

            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error in retreiving user from db, please retry later");
                return;
            }
            if(user == null) {
                request.setAttribute("msgLogin", "Previouse authentication failed: email or password not correct.");
                isValid = false;
            }
        }

        if(isValid && user!=null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("language", request.getLocale().getLanguage());
            response.sendRedirect("GoToHome");
        } else {
            String path = "WEB-INF/index.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            templateEngine.process(path, ctx, response.getWriter());
        }
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }
}