package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import dao.AuctionDAO;
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

    public GoToAuction() {
        super();
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
        auctionDAO = new AuctionDAO(connection);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String auctionIdParam = request.getParameter("auctionId");
        int auctionId = Integer.parseInt(auctionIdParam);

        try {
            Auction auction = auctionDAO.findAuctionByIdAuction(auctionId);

            if (auction != null) {
                List<Object> auctionClosedInfos = auctionDAO.getAuctionClosedInfos(auction);

                if (auctionClosedInfos == null) {
                    // Auction is still open
                    // TODO: Implement the logic for displaying the auction details
                    // For now, let's just forward to a placeholder page
                    String path = "WEB-INF/templates/AuctionDetailsOpen.html";
                    final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                    templateEngine.process(path, ctx, response.getWriter());
                } else {
                    // Auction is closed
                    // TODO: Implement the logic for displaying the closed auction details
                    // For now, let's just forward to a placeholder page
                    String path = "WEB-INF/templates/AuctionDetailsClosed.html";
                    final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
                    ctx.setVariable("auctionClosedInfos", auctionClosedInfos);
                    templateEngine.process(path, ctx, response.getWriter());
                }
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
