package controllers;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import dao.ArticleDAO;
import beans.User;
import utilis.ConnectionHandler;
import utilis.ThymeleafTemplateEngineCreator;

@WebServlet("/AddArticle")
@MultipartConfig
public class AddArticle extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine = null;

    public AddArticle() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
        templateEngine = ThymeleafTemplateEngineCreator.getTemplateEngine(servletContext);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//String shortDir = "/images";
		String userHome = System.getProperty("user.home");
        String pathString = userHome + "/git/TIW-project-2023-pure-HTML-Polverini-Ye/TIW-project-2023-pure-HTML-Polverini-Ye/src/main/webapp/images";
        String pathString1 = userHome + "/git/TIW-project-2023-pure-HTML-Polverini-Ye/TIW-project-2023-pure-HTML-Polverini-Ye";
        Path path = Paths.get(pathString);
		//System.out.println("Webapp path: " + webappPath);

		String uploadDirectory = path.toString();
	    //String filePath = null;
	    //String shortFilePath = null;
	    String fileName = null;
	    //System.out.println(uploadDirectory);
	    
	    
	    
	    

	    // crea nuova directory se non esiste
	    File dir = new File(uploadDirectory);
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }

	    try {
	        Part filePart = request.getPart("imageToUpload"); // riceve la image part dalla richiesta
	        fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName(); // estrae il nome
	        String fileExtension = getFileExtension(fileName);
	        //response.getWriter().println(fileName+" "+ fileExtension);

	        // Check if the file extension is allowed
	        if (isAllowedExtension(fileExtension)) {
	            //filePath = uploadDirectory + File.separator + fileName;
	            //shortFilePath = shortDir+ File.separator + fileName;
	            //response.getWriter().println(filePath);

	            // salvataggio del file nella cartella images
	            
	            //response.getWriter().println(uploadDirectory);
	            try (InputStream inputStream = filePart.getInputStream()) {
	                File file = new File(uploadDirectory, fileName);
	                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	            }
	            
	            //response.getWriter().println("File uploaded successfully!");
	            
	        } else {
	        	
	            //response.getWriter().println("Invalid file type. Only JPG, JPEG, PNG, and GIF files are allowed.");
	        }
	    } catch (Exception ex) {
	    	
	        response.getWriter().println("Error uploading file: " + ex.getMessage());
	    }
	
	    float price = 0;
	    String articleName = request.getParameter("articleName");
	    String articleDesc = request.getParameter("articleDesc");
	    String articlePrice = request.getParameter("price");
	    User user = (User) request.getSession().getAttribute("user");
	    
	    if (articlePrice != null && !articlePrice.isEmpty()) {
	        try {
	            price = Float.parseFloat(articlePrice);
	        } catch (NumberFormatException e) {
	            response.getWriter().println("Il valore inserito non è un numero valido.");
	        }
	    } else {
	        response.getWriter().println("Il numero non è stato fornito nella richiesta.");
	    }
	    
	    ArticleDAO article = new ArticleDAO(connection);
	    //PER CONTROLLARE IN FUTURO IL PATH DOVE VENGONO SALVATE LE IMMAGINI, COMMENTARE DAL TRY FINO A DOPO LA SENDREDIRECT E DECOMMENTARE LE RESPONSE.GETWRITER()
	   try {
		   Part filePart = request.getPart("imageToUpload"); 
	       fileName = filePart.getSubmittedFileName(); 
	       String fileExtension = getFileExtension(fileName);
	       if(isAllowedExtension(fileExtension)) {
	       	article.createArticle(articleName, articleDesc, price, fileName,user.getUserMail());
	       }
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error, retry later");
			return;
		}
	   
	    response.sendRedirect("GoToSell");
	}

	//TODO: INUTILE?
	private String getFileName(Part part) {
	    String contentDisposition = part.getHeader("content-disposition");
	    String[] tokens = contentDisposition.split(";");
	    for (String token : tokens) {
	        if (token.trim().startsWith("filename")) {
	            return token.substring(token.indexOf("=") + 2, token.length() - 1);
	        }
	    }
	    return "";
	}

	// prende l'estensione del file
	private String getFileExtension(String fileName) {
	    int dotIndex = fileName.lastIndexOf(".");
	    if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
	        return fileName.substring(dotIndex + 1);
	    }
	    return "";
	}

	// controlla che l'estensione vada bene
	private boolean isAllowedExtension(String fileExtension) {
	    String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
	    for (String ext : allowedExtensions) {
	        if (ext.equalsIgnoreCase(fileExtension)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	
}
