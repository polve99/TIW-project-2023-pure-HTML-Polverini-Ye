package controllers;

import beans.Auction;
import beans.Bid;
import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;

import utilis.ConnectionHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        String userMail = user.getUserMail();

        BidDAO bidDAO = new BidDAO(connection);
        AuctionDAO auctionDAO = new AuctionDAO(connection);

        boolean isValid = true;

        int idAuction = (int) session.getAttribute("idAuction");
        if(request.getParameter("idAuction") == null || request.getParameter("idAuction").isEmpty() ){
            isValid = false;
            request.setAttribute("msgBid", "idAuction value null or empty");
        }

        float bidValue = Float.parseFloat(request.getParameter("bidValue"));
        if(request.getParameter("bidValue") == null || request.getParameter("bidValue").isEmpty() ){
            isValid = false;
            request.setAttribute("msgBid", "Bid value null or empty");
        }

        if(bidValue <= 0){
            request.setAttribute("msgBid", "Bid value must be greater than 0");
        } else {
            try {
                Auction auction = auctionDAO.findAuctionByIdAuction(idAuction);

                if(userMail.equals(auction.getUserMail())) {
                    isValid = false;
                    request.setAttribute("msgBid", "You created this auction! You cannot place a bid!");
                } else if(!auction.isOpen()){
                    isValid = false;
                    request.setAttribute("msgBid", "Auction is closed");
                } else if(!auctionDAO.isAuctionNotExpired(idAuction)) {
                	isValid = false;
                    request.setAttribute("msgBid", "Auction is expired");
                }

                float minRise = auction.getMinRise();
                Bid maxBid = bidDAO.findMaxBidInAuction(idAuction);

                String maxBidderMail= null;
                if(maxBid != null){
                    maxBidderMail = maxBid.getUserMail();
                }
                if(maxBidderMail != null && maxBidderMail.equals(userMail)){
                    isValid = false;
                    request.setAttribute("msgBid", "You are the current max bidder! You cannot place a bid!");
                }

                if(maxBid == null){
                    if(bidValue <= auction.getInitialPrice()) {
                        isValid = false;
                        request.setAttribute("msgBid", "Bid value too low (must be greater than/equal the initial price (" + auction.getInitialPrice() + "))");
                    }
                } else {
                    float maxBidValue = maxBid.getBidValue();
                    if(bidValue < maxBidValue + minRise) {
                        isValid = false;
                        request.setAttribute("msgBid", "Bid value too low (must be greater than the current bid value (" + maxBidValue + ") + min rise (" + minRise + "))");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error in db. Please, retry later.");
                return;
            }
            
            if(isValid){
                try {
                    bidDAO.createBid(bidValue, userMail, idAuction);
                    request.setAttribute("msgBid", "Bid successfully created!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error in db, bid not created. Please, retry later.");
                    return;
                }
            }
        }

        request.getRequestDispatcher("/GoToAuction").forward(request, response);
    }
}
