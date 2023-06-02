package controllers;

import beans.Auction;
import beans.Bid;
import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import org.thymeleaf.TemplateEngine;

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
    private TemplateEngine templateEngine = null;

    public MakeBid() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            request.setAttribute("msgBid", "You must be logged in to make a bid");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        boolean isValid = true;

        int idAuction = (int) session.getAttribute("idAuction");
        User user = (User) session.getAttribute("user");
        if(request.getParameter("bidValue") == null || request.getParameter("bidValue").isEmpty() ){
            isValid = false;
            request.setAttribute("msgBid", "Bid value null or empty");
        }
        float bidValue = Float.parseFloat(request.getParameter("bidValue"));
        String userMail = user.getUserMail();

        BidDAO bidDAO = new BidDAO(connection);
        AuctionDAO auctionDAO = new AuctionDAO(connection);

        if(bidValue <= 0){
            request.setAttribute("msgBid", "Bid value must be greater than 0");
        } else {
            try {
                Auction auction = auctionDAO.findAuctionByIdAuction(idAuction);
                float minRise = auction.getMinRise();
                Bid maxBid = bidDAO.findMaxBidInAuction(idAuction);
                float maxBidValue = maxBid.getBidValue();
                if(bidValue < maxBidValue + minRise) {
                    isValid = false;
                    request.setAttribute("msgBid", "Bid value too low (must be greater than the current bid value (" + maxBidValue + ") + min rise (" + minRise + "))");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("msgBid", "Error in db, bid not created. Please retry.");
            }
            if(isValid){
                try {
                    bidDAO.createBid(bidValue, userMail, idAuction);
                    request.setAttribute("msgBid", "Bid successfully created!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("msgBid", "Error in db, bid not created. Please retry.");
                }
            }
        }

        request.getRequestDispatcher("/GoToAuction").forward(request, response);
    }
}
