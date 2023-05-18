package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
		String userMail = request.getParameter("userMail");
		String password = request.getParameter("password");
		if (userMail == null || password == null || userMail.isBlank() || password.isBlank()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.getUserAfterAuthentication(userMail, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error, please retry later");
			return;
		}
		if (user == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found: mail or password not correct");
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