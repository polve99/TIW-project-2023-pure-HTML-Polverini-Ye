package controllers;

import beans.Article;
import beans.Auction;
import beans.Bid;
import beans.User;
import dao.ArticleDAO;
import dao.AuctionDAO;
import dao.BidDAO;

import utilis.ConnectionHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public CloseAuction() {
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

        boolean isValid = true;
        String closeMsg = null;

        BidDAO bidDAO = new BidDAO(connection);
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        ArticleDAO articleDAO = new ArticleDAO(connection);

        int idAuction = (int) session.getAttribute("idAuction");
        if (request.getParameter("idAuction") == null || request.getParameter("idAuction").isEmpty()) {
            isValid = false;
            request.setAttribute("errorString", "idAuction value null or empty");
        }

        Auction auction = null;
        try {
            auction = auctionDAO.findAuctionByIdAuction(idAuction);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover auction in database");
            return;
        }

        if (auction == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction not found");
            return;
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if(!auction.getUserMail().equals(userMail)) {
            closeMsg = "You are not the owner of this auction";
            isValid = false;
        } else if (auction.getExpirationDateTime().after(now)){
            closeMsg = "Auction not expired yet. Check Time Left.";
            isValid = false;
        } else if (!auction.isOpen()) {
            closeMsg = "Auction already closed";
            isValid = false;
        }

        if(isValid){
            try {
                auctionDAO.closeAuction(idAuction);
                Bid maxBid = bidDAO.findMaxBidInAuction(idAuction);

                if(maxBid==null){
                    ArrayList<Article> articles = articleDAO.findArticlesListByIdAuction(idAuction);
                    auctionDAO.putBackArticles(idAuction);
                    closeMsg = "Auction closed successfully. It had No bids so all articles will be put back: ";
                    for (Article article : articles) {
                        closeMsg += " " + article.getArticleName();
                    }
                    closeMsg += ".";
                } else {
                    closeMsg = "Auction closed successfully.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to close auction in database");
                return;
            }
        }
        if (closeMsg !=null) request.setAttribute("closeMsg", closeMsg);

        request.getRequestDispatcher("/GoToAuction").forward(request, response);

    }
}