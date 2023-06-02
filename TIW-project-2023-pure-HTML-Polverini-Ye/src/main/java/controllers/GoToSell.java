package controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

@WebServlet("/GoToSell")
public class GoToSell extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;
    private AuctionDAO auctionDAO;
    private ArticleDAO articleDAO;
    private BidDAO bidDAO;

    public GoToSell() {
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


        List<Auction> auctionListOpen = new ArrayList<>();

        try {
        	
         auctionListOpen = auctionDAO.getAllOpenAuctionsByUser(user.getUserMail());
         
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
            auctionInfo.put("idAuction", auction.getIdAuction());
            auctionInfo.put("articles", articles);
            auctionInfo.put("maxBid", maxBid != null ? maxBid.getBidValue() : null);
            auctionInfo.put("timeLeftFormatted", formatTimeLeft(expirationDateTime));

            auctionInfoList.add(auctionInfo);
        }

        try {
            ArrayList<Auction> ownClosedAuctions = auctionDAO.getAllClosedAuctionsByUser(user.getUserMail());
            ArrayList<Map<String, Object>> ownClosedAuctionInfoList = new ArrayList<>();

            for (Auction auction : ownClosedAuctions) {
                ArrayList<Object> auctionClosedInfos = auctionDAO.getAuctionClosedInfosForTable(auction);

                Map<String, Object> auctionInfo = new HashMap<>();

                auctionInfo.put("idAuction", auction.getIdAuction());
                auctionInfo.put("maxBid", ((Bid) auctionClosedInfos.get(0)).getBidValue());
                auctionInfo.put("articles", auctionClosedInfos.get(1));

                ownClosedAuctionInfoList.add(auctionInfo);
            }

            final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            String path = "/WEB-INF/templates/SellPage.html";
            
            
            List<String> imageList1 = new ArrayList<>();
        	ArticleDAO articleDAO = new ArticleDAO(connection);
        	try {
    			imageList1 = articleDAO.findImagesByUser(user.getUserMail());
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	List<String> imageList = new ArrayList<>();
        	for (String image : imageList1) {
        		String userHome = System.getProperty("user.home");
                String pathString = userHome + "/git/TIW-project-2023-pure-HTML-Polverini-Ye/TIW-project-2023-pure-HTML-Polverini-Ye/src/main/webapp";
                Path imagePath = Paths.get(pathString);
        		//System.out.println("Webapp path: " + webappPath);

        		String imageDirectory = imagePath+image;
        		imageList.add(imageDirectory);
        	}
        	
        	 
        	
        	if(!imageList.isEmpty()) {
        		ctx.setVariable("imageList", imageList1);
        	} else {
                ctx.setVariable("NoArticle", "You have no articles at this time.");
                //prova
                ctx.setVariable("imageList", imageList1);
        	}
            

            if (auctionListOpen.isEmpty()) {
                
                ctx.setVariable("NoOpenAuctionsMsg", "You have no open auctions at this time.");
                //prova
                ctx.setVariable("auctionInfoListOpen", auctionInfoList);
             
            } else {
                ctx.setVariable("auctionInfoListOpen", auctionInfoList);
            }

            if (ownClosedAuctionInfoList.isEmpty()) {
                ctx.setVariable("NoWonAuctionsMsg", "You haven't any auctions closed yet.");
                //prova
                ctx.setVariable("auctionInfoListWon", ownClosedAuctionInfoList);
            } else {
                ctx.setVariable("auctionInfoListWon", ownClosedAuctionInfoList);
            }

            ctx.setVariable("user", user.getName());
           

            templateEngine.process(path, ctx, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error retrieving won auctions");
        }
        
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
