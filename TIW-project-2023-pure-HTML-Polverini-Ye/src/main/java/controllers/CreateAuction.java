package controllers;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import dao.ArticleDAO;
import dao.AuctionDAO;
import beans.User;
import beans.Article;
import utilis.ConnectionHandler;
import utilis.ThymeleafTemplateEngineCreator;


@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    //private TemplateEngine templateEngine = null;

    public CreateAuction() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        //templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
        
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	ArrayList<Article> articleList = new ArrayList<Article>();
    	User user = (User) request.getSession().getAttribute("user");
    	
    	int i = 0;
    	float initialPrice = 0;
    	
    	LocalDateTime dateTime = LocalDateTime.now();
    	
        int daysToAdd = Integer.parseInt(request.getParameter("expirationDate")); 

        LocalDateTime newDateTime = dateTime.plusDays(daysToAdd); 
        Timestamp time = Timestamp.valueOf(newDateTime);

        //System.out.println(time +" "+ dateTime);
    	
    	
        String minRise = request.getParameter("minRise");
        String[] selectedImages = request.getParameterValues("selectedImages");
        
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        ArticleDAO articleDAO = new ArticleDAO(connection);
        
        int aucId = 0;
        
        for(String image : selectedImages) {
        	try {
        		Article article = new Article();
        		article = articleDAO.findArticleByImage(image);
        		articleList.add(article);
        		
        	} catch (SQLException e) {
    			e.printStackTrace();
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error, retry later");
    			return;
    		}
        	initialPrice += articleList.get(i).getArticlePrice();
        	i++;
        	System.out.println(initialPrice);
        }
        
        try {
			aucId = auctionDAO.createAuction(initialPrice, Float.parseFloat(minRise), time, user.getUserMail());
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
			auctionDAO.addArticlesInAuction(aucId, articleList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        response.sendRedirect("GoToSell");
        
    }

}
