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
        String auctionIdParam = request.getParameter("auctionId");
        int auctionId = Integer.parseInt(auctionIdParam);

        try {
            Auction auction = auctionDAO.findAuctionByIdAuction(auctionId);

            if (auction != null) {
                List<Article> articles = articleDAO.findArticlesListByIdAuction(auctionId);
                Bid maxBid = bidDAO.findMaxBidInAuction(auctionId);
                long timeLeftMillis = auction.getExpirationDateTime().getTime() - System.currentTimeMillis();

                // Conversione del tempo rimanente in giorni, ore, minuti e secondi
                long seconds = timeLeftMillis / 1000;
                long days = seconds / (24 * 60 * 60);
                seconds %= (24 * 60 * 60);
                long hours = seconds / (60 * 60);
                seconds %= (60 * 60);
                long minutes = seconds / 60;
                seconds %= 60;

                String timeLeftFormatted = String.format("%d days, %02d:%02d:%02d", days, hours, minutes, seconds);

                Map<String, Object> auctionInfo = new HashMap<>();
                auctionInfo.put("idAuction", auction.getIdAuction());
                auctionInfo.put("articles", articles);
                auctionInfo.put("maxBid", maxBid != null ? maxBid.getBidValue() : "No bids yet");
                auctionInfo.put("timeLeft", timeLeftFormatted);

                request.setAttribute("auctionInfo", auctionInfo);

                // Non viene richiamato il motore del template Thymeleaf

            } else {
                // Auction not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction not found");
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }
}
