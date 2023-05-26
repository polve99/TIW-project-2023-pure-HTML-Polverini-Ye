package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Article;
import beans.Auction;
import beans.Bid;
import beans.User;
import dao.ArticleDAO;
import dao.AuctionDAO;
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
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(getServletContext());
        auctionDAO = new AuctionDAO(connection);
        articleDAO = new ArticleDAO(connection);
        bidDAO = new BidDAO(connection);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idAuctionParam = request.getParameter("idAuction");
        request.setAttribute("idAuction", idAuctionParam);
        if (idAuctionParam == null || idAuctionParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid auction ID");
            return;
        }

        int idAuction;
        try {
            idAuction = Integer.parseInt(idAuctionParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid auction ID");
            return;
        }

        Auction auction;
        List<Article> articles;
        List<Bid> bids;

        try {
            auction = auctionDAO.findAuctionByIdAuction(idAuction);
            articles = articleDAO.findArticlesListByIdAuction(idAuction);
            bids = bidDAO.findBidsListByIdAuction(idAuction);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in retrieving auction details");
            return;
        }

        boolean isAuctionOpen = false;
        try {
            isAuctionOpen = auctionDAO.isAuctionOpen(idAuction);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Salva l'ID dell'asta nella sessione
        session.setAttribute("idAuction", idAuction);

        String template;
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("auction", auction);
        templateVariables.put("articles", articles);
        templateVariables.put("bids", bids);
        templateVariables.put("timeLeftFormatted", formatTimeLeft(auction.getExpirationDateTime()));

        if (isAuctionOpen) {
            // Auction is still open
            template = "OpenAuctionPage.html";
        } else {
            // Auction is closed
            template = "ClosedAuctionPage.html";
        }

        WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
        webContext.setVariables(templateVariables);

        String path = "/WEB-INF/templates/" + template;

        templateEngine.process(path, webContext, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chiamiamo il metodo doGet per gestire la logica comune alle richieste GET e POST
        doGet(request, response);
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }

    private String formatTimeLeft(Timestamp expirationDateTime) {
        long timeLeftMillis = expirationDateTime.getTime() - System.currentTimeMillis();

        long seconds = timeLeftMillis / 1000;
        long days = seconds / (24 * 60 * 60);
        seconds %= (24 * 60 * 60);
        long hours = seconds / (60 * 60);
        seconds %= (60 * 60);
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%d days, %02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
