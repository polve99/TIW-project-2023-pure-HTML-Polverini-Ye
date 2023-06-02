package controllers;

import beans.User;
import dao.BidDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import utilis.ConnectionHandler;
import utilis.ThymeleafTemplateEngineCreator;

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

@WebServlet("/MakeBid")
public class MakeBid extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public MakeBid() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int idAuction = (int) session.getAttribute("idAuction");
        User user = (User) session.getAttribute("user");

        int bidValue = Integer.parseInt(request.getParameter("bidValue"));
        String userMail = user.getUserMail();

        BidDAO bidDAO = new BidDAO(connection);

        try {
            bidDAO.createBid(bidValue, userMail, idAuction);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create the bid requested.");
            return;
        }

        // Assuming you have successfully created the bid, you can store a flag in the session
        request.getSession().setAttribute("bidCreated", true);

        // Redirect to the OpenAuctionPage servlet
        response.sendRedirect("GoToAuction?idAuction=" + idAuction);
    }
}
