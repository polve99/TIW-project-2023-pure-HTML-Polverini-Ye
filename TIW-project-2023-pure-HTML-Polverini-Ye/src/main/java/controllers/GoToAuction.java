package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import beans.Article;
import beans.Bid;
import dao.AuctionDAO;
import dao.ArticleDAO;
import dao.BidDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import utilis.ConnectionHandler;
import utilis.ThymeleafTemplateEngineCreator;

@WebServlet("/GoToAuction")
public class GoToAuction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;
    private AuctionDAO auctionDAO;
    private ArticleDAO articleDAO;
    private BidDAO bidDAO;

    public GoToAuction() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
        auctionDAO = new AuctionDAO(connection);
        articleDAO = new ArticleDAO(connection);
        bidDAO = new BidDAO(connection);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO questa pagina serve poi quando cliccando su una idAuction dalla pagina di BuyPAge deve rimandare alla pagina delle info di Auction
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }
}
