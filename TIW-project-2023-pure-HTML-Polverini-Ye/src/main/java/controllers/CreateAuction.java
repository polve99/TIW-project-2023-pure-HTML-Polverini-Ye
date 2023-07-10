package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

import dao.ArticleDAO;
import dao.AuctionDAO;
import beans.User;
import beans.Article;
import utilis.ConnectionHandler;

@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public CreateAuction() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	ArrayList<Article> articleList = new ArrayList<Article>();
    	User user = (User) request.getSession().getAttribute("user");
    	
    	int i = 0;
    	float initialPrice = 0;
    	int daysToAdd = 0;
    	
    	LocalDateTime dateTime = LocalDateTime.now();
    	
    	if(!isNumber(request.getParameter("expirationDate"))) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the duration must be a number");
    		return;
        }
    	
    	try {
    		daysToAdd = Integer.parseInt(request.getParameter("expirationDate")); 
    	} catch (Exception e) {
    		e.printStackTrace();
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the number inserted doesn't have the right format");
    		return;
    	}
        
        if (daysToAdd < 1 || daysToAdd > 20) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the number inserted doesn't respect the range of possibilities proposed");
    		return;
        }

        LocalDateTime newDateTime = dateTime.plusDays(daysToAdd); 
        Timestamp time = Timestamp.valueOf(newDateTime);
    	
        String minRise = request.getParameter("minRise");
        if(!isNumber(minRise)) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the rise must be a number");
    		return;
        }
        
        float rise = 0;
        try {
        	rise = Float.parseFloat(minRise);
        	if (rise <= 0) {
        		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the rise must be greater than zero");
        		return;
        	}
        } catch(Exception e) {
        	e.printStackTrace();
    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the rise inserted doesn't have the right format");
    		return;
        }
        
        if(minRise.length()<=0 || minRise.isEmpty()) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST,"invalid minimum rise");
        	return;
        }
        
        String[] selectedImages = request.getParameterValues("selectedImages");
        
        if(selectedImages[0] == null) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no articles selected");
        	return;
        }
        
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
			aucId = auctionDAO.createAuction(initialPrice, rise, time, user.getUserMail());
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
    
    private boolean isNumber(String num) {
    	Pattern numberPattern = Pattern.compile("\\d+");
		if (num.length()>0) {
			return numberPattern.matcher(num).matches();
		} else {
			return false;
		}
    }

}
