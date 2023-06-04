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

        //here starts the retreiving infos for open auctions table

        List<Auction> auctionListOpen = new ArrayList<>();
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
            Bid maxBid = null;
            float initialPrice;
            try {
                articles = articleDAO.findArticlesListByIdAuction(auction.getIdAuction());
                maxBid = bidDAO.findMaxBidInAuction(auction.getIdAuction());
                initialPrice = auction.getInitialPrice();
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in finding auctions' informations");
                return;
            }
            Timestamp expirationDateTime = auction.getExpirationDateTime();

            Map<String, Object> auctionInfo = new HashMap<>();
            auctionInfo.put("idAuction", auction.getIdAuction());
            auctionInfo.put("articles", articles);
            auctionInfo.put("maxBid", maxBid);
            auctionInfo.put("initialPrice", initialPrice);
            auctionInfo.put("timeLeftFormatted", formatTimeLeft(expirationDateTime));

            auctionInfoList.add(auctionInfo);
        }

        //here starts the retreiving infos for closed won auctions table

        ArrayList<Map<String, Object>> wonAuctionInfoList = new ArrayList<>();
        try {
            ArrayList<Auction> wonAuctions = auctionDAO.getWonClosedAuctionsByUser(user.getUserMail());

            for (Auction auction : wonAuctions) {
                ArrayList<Object> auctionClosedInfos = auctionDAO.getAuctionClosedInfosForTable(auction);

                Map<String, Object> auctionInfo = new HashMap<>();

                auctionInfo.put("idAuction", auction.getIdAuction());
                auctionInfo.put("maxBid", ((Bid) auctionClosedInfos.get(0)).getBidValue());
                auctionInfo.put("articles", auctionClosedInfos.get(1));

                wonAuctionInfoList.add(auctionInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error retrieving won auctions");
            return;
        }

        //response

        WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        String path = "/WEB-INF/templates/BuyPage.html";

        ctx.setVariable("auctionInfoListOpen", auctionInfoList);
        ctx.setVariable("auctionInfoListWon", wonAuctionInfoList);

        if (auctionInfoList.isEmpty()) {
            if (keyword != null && !keyword.isBlank()) {
                ctx.setVariable("NoOpenAuctionsMsg", "There are no open auctions for the keyword: "+keyword+".");
            } else {
                ctx.setVariable("NoOpenAuctionsMsg", "There are no open auctions at this time.");
            }
        }

        if (wonAuctionInfoList.isEmpty()) {
            ctx.setVariable("NoWonAuctionsMsg", "You haven't won any auctions yet.");
        }

        ctx.setVariable("user", user.getName());
        templateEngine.process(path, ctx, response.getWriter());
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

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }
}
