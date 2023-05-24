package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

@WebServlet("/GoFindKeyword")
public class GoFindKeyword extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;

    public GoFindKeyword() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = "WEB-INF/BuyPage.html";
        final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //here we receive the parameters from the form as the Keyword for the search
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        List<Auction> auctionListOpen = new ArrayList<>();
        AuctionDAO auctionDAO = new AuctionDAO(connection);

        try {
            auctionListOpen = auctionDAO.findAuctionsListByWordSearch(keyword);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Internal db error in finding auctions by keyword");
        }

        final WebContext ctw = new WebContext(request, response, getServletContext(),request.getLocale());
        String path = "WEB-INF/BuyPage.html";

        if (auctionListOpen.size() == 0) {
            ctw.setVariable("NoAuctionsMsg", "for the keyword \""+keyword+"\" there are not any open auctions");
        }else{
            ctw.setVariable("auctions", auctionListOpen);
        }

        templateEngine.process(path, ctw,response.getWriter());
    }

    @Override
    public void destroy() {
        ConnectionHandler.closeConnection(connection);
    }

}
