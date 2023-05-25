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
		boolean isBadRequest = true;
		String badRequestMessage = "";
		if (name == null || passw == null || repeatedPassword == null || userMail == null ||
				name.isBlank() || passw.isBlank() || repeatedPassword.isBlank() || userMail.isBlank()) {
			badRequestMessage = "Missing parameters";
		} else if (!emailValid) {
			badRequestMessage = "Email not valid";
		} else if (!passw.equals(repeatedPassword)) {
			badRequestMessage = "Password and repeated password are different";
		} else if (passw.length() < 8 || passw.length()>50) {
			badRequestMessage = "Password must be at least of 8 characters and less than 50 characters";
		} else {
			isBadRequest = false;
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, badRequestMessage);
			return;
		}
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.createUser(userMail, passw, name, surname, telephone, address);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error, retry later");
			return;
		}
		if  (user == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username or password not correct");
			return;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);
		session.setAttribute("language", request.getLocale().getLanguage());
		response.sendRedirect("GoToHome");
	}
    
    @Override
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

    
}
