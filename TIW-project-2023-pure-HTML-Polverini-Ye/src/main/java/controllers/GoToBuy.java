package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
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

@WebServlet("/GoToBuy")
public class GoToBuy extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;
    private AuctionDAO auctionDAO;
    private ArticleDAO articleDAO;
    private BidDAO bidDAO;

    public GoToBuy() {
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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String keyword = request.getParameter("keyword");

        List<Auction> auctionListOpen = new ArrayList<>();
        AuctionDAO auctionDAO = new AuctionDAO(connection);

        try {
            if (keyword != null && !keyword.isBlank()) {
                auctionListOpen = auctionDAO.findAuctionsListByWordSearch(keyword);
            } else {
                auctionListOpen = auctionDAO.getAllOpenAuctions();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in finding auctions");
            return;
        }

        List<Map<String, Object>> auctionInfoList = new ArrayList<>();

        for (Auction auction : auctionListOpen) {
            List<Article> articles = null;
            try {
                articles = articleDAO.findArticlesListByIdAuction(auction.getIdAuction());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Bid maxBid = null;
            try {
                maxBid = bidDAO.findMaxBidInAuction(auction.getIdAuction());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Timestamp expirationDateTime = auction.getExpirationDateTime();

            Map<String, Object> auctionInfo = new HashMap<>();
            auctionInfo.put("auctionId", auction.getIdAuction());
            auctionInfo.put("articles", articles);
            auctionInfo.put("maxBid", maxBid != null ? maxBid.getBidValue() : null); // Imposta il valore bidValue

            auctionInfo.put("expirationDateTime", expirationDateTime);

            auctionInfoList.add(auctionInfo);
        }

        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        String path = "/WEB-INF/templates/BuyPage.html";

        if (auctionListOpen.isEmpty()) {
            if (keyword != null && !keyword.isBlank()) {
                ctx.setVariable("NoAuctionsMsg", "There are no open auctions for the keyword \"" + keyword + "\"");
            } else {
                ctx.setVariable("NoAuctionsMsg", "There are no open auctions at this time.");
            }
        } else {
            ctx.setVariable("auctionInfoList", auctionInfoList);
        }

        ctx.setVariable("user", user.getName());

        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }
}
