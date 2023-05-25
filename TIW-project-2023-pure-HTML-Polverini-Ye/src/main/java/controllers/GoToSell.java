package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.Auction;
import beans.User;
import dao.AuctionDAO;
import utilis.ConnectionHandler;
import utilis.ThymeleafTemplateEngineCreator;

@WebServlet("/GoToSell")
public class GoToSell extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;

    public GoToSell() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = "WEB-INF/templates/SellPage.html";
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }

   
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //here's the list for the auctions that will be shown in the page

        List<Auction> auctionListOpen = new ArrayList<>();
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        try {
            auctionListOpen = auctionDAO.getAllOpenAuctionsByUser(user.getUserMail());
        } catch (SQLException e) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in finding open auctions");
        }

        final WebContext ctw = new WebContext(request, response, getServletContext(),request.getLocale());
        String path = "WEB-INF/templates/SellPage.html";

        if (auctionListOpen.size() == 0) {
            ctw.setVariable("NoAuctionsMsg", "there are not any open auctions at this time.");
        }else{
            ctw.setVariable("myauctions", auctionListOpen);
        }

        templateEngine.process(path, ctw,response.getWriter());
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }
}
