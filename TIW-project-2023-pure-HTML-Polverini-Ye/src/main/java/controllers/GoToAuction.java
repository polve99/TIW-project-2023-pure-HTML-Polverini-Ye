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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

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
            if(!auctionDAO.isAuctionInDB(idAuction)){
                String errorString = "Previous idAuction in request not found. Back to previous page.";
                request.setAttribute("errorString", errorString);

                //checks attribute 'from' to go back
                if(session.getAttribute("from").equals("BuyPage")) {
                	request.getRequestDispatcher("/GoToBuy").forward(request, response);
                }else if(session.getAttribute("from").equals("SellPage")){
                	request.getRequestDispatcher("/GoToSell").forward(request, response);
                }else {
                	response.sendRedirect("GoToHome");
                }
                return;
            }
        } catch (SQLException | ServletException e) {
            throw new RuntimeException(e);
        }

        Auction auction;
        boolean isAuctionNotExpired = false;
        List<Article> articles;
        Bid maxBid;
        float initialPrice;
        List<Bid> bids;
        List<Object> closedAuctionInfo = null;

        try {
            auction = auctionDAO.findAuctionByIdAuction(idAuction);
            isAuctionNotExpired = auctionDAO.isAuctionNotExpired(idAuction);
            articles = articleDAO.findArticlesListByIdAuction(idAuction);
            maxBid = bidDAO.findMaxBidInAuction(idAuction);
            initialPrice = auction.getInitialPrice();
            bids = bidDAO.findBidsListByIdAuction(idAuction);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in retrieving auction details");
            return;
        }

        if(!isAuctionNotExpired){
            try{
                closedAuctionInfo = auctionDAO.getAuctionClosedInfos(auction);
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in retrieving closed auction info");
                return;
            }
            request.setAttribute("closedAuctionInfo", closedAuctionInfo);
        }

        // Salva l'ID dell'asta nella sessione
        session.setAttribute("idAuction", idAuction);

        String template;
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("user", user);
        templateVariables.put("auction", auction);
        templateVariables.put("articles", articles);
        templateVariables.put("maxBid", maxBid);
        templateVariables.put("initialPrice", initialPrice);
        templateVariables.put("bids", bids);
        templateVariables.put("timeLeftFormatted", formatTimeLeft(auction.getExpirationDateTime()));
        if(closedAuctionInfo != null) templateVariables.put("closedAuctionInfo", closedAuctionInfo);

        if (auction.isOpen()) { //checks the isOpen attribute
            template = "OpenAuctionPage.html";
        } else {
            template = "ClosedAuctionPage.html";
        }

        WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        ctx.setVariables(templateVariables);

        if(bids.isEmpty()){
            ctx.setVariable("NoBidsMsg", "There are no bids at this time for this auction.");
        }

        String msgBid = (String) request.getAttribute("msgBid");
        if (msgBid != null) {
            ctx.setVariable("msgBid", msgBid);
        }
        
        String closeMsg = (String) request.getAttribute("closeMsg");
        if (closeMsg != null) {
            ctx.setVariable("closeMsg", closeMsg);
        }

        if(auction.getUserMail().equals(user.getUserMail())) {
            ctx.setVariable("bidform", "false");
        } else {
            ctx.setVariable("bidform", "true");
        }

        String path = "/WEB-INF/templates/" + template;

        templateEngine.process(path, ctx, response.getWriter());

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
        
        if(timeLeftMillis<0) {
        	String msg = "expired";
        	return msg;
        }

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
