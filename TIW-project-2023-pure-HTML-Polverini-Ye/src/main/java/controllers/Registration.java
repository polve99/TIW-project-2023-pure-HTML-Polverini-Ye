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

@WebServlet("/Registration")
public class Registration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;

    public Registration() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = "WEB-INF/registration.html";
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String userMail = request.getParameter("userMail");
        String passw = request.getParameter("password");
        String repeatedPassword = request.getParameter("repeatedPassword");
        String telephone = request.getParameter("telephone");
        String address = request.getParameter("address");
        Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
        boolean emailValid = emailPattern.matcher(userMail).matches();
        boolean isValid = false;

        if (name==null || name==" " || name.length() < 2 || name.length() > 20) {
            request.setAttribute("nameErrorMessage", "Name must be between 2 and 20 characters");
        }

        if (surname==null || surname.length() < 2 || surname.length() > 20) {
            request.setAttribute("surnameErrorMessage", "Surname must be between 2 and 20 characters");
        }

        if (userMail==null || userMail.length() < 5 || userMail.length() > 50 || !emailValid) {
            request.setAttribute("emailErrorMessage", "Invalid email. Email must be between 5 and 50 characters and have a valid format (e.g., email@mail.com)");
        }

        if (passw==null || passw.length() < 8 || passw.length() > 50) {
            request.setAttribute("passwordErrorMessage", "Password must be between 8 and 50 characters");
        }

        if (repeatedPassword==null || !passw.equals(repeatedPassword)) {
            request.setAttribute("repeatPasswordErrorMessage", "Password and repeated password do not match");
        }

        if (address==null || address.length() < 1 || address.length() > 50) {
            request.setAttribute("addressErrorMessage", "Address must be between 1 and 50 characters");
        }

        if (name.length() >= 2 && name.length() <= 20 &&
                surname.length() >= 2 && surname.length() <= 20 &&
                userMail.length() >= 5 && userMail.length() <= 50 && emailValid &&
                passw.length() >= 8 && passw.length() <= 50 &&
                passw.equals(repeatedPassword) &&
                address.length() >= 1 && address.length() <= 50) {
            isValid = true;
        }
        
        UserDAO userDAO = new UserDAO(connection);
        try {
            if(userDAO.isUserMailInDB(userMail)) {
                request.setAttribute("emailErrorMessage", "Email already in use");
                isValid=false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (isValid) {
            User user = null;
            try {
                user = userDAO.createUser(userMail, passw, name, surname, telephone, address);
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("errorMsg", "Error in creating user. Please try again.");
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("language", request.getLocale().getLanguage());
            response.sendRedirect("GoToHome");
        } else {
            String path = "WEB-INF/registration.html";
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