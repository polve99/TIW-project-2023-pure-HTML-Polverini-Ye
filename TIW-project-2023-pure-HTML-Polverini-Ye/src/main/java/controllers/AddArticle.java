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
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import dao.ArticleDAO;
import beans.User;
import utilis.ConnectionHandler;

@WebServlet("/AddArticle")
@MultipartConfig
public class AddArticle extends HttpServlet{
    
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public AddArticle() {
        super();
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(servletContext);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("user");
        
        float price = 0;
        String articleName = request.getParameter("articleName");
        String articleDesc = request.getParameter("articleDesc");
        String articlePrice = request.getParameter("price");
        
        if(articleName.length()<=0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing name in the article you want to add");
            return;
        }
        
        if(!isNumber(articlePrice)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "price must be a number");
            return;
        }
        
        if (articlePrice != null && !articlePrice.isEmpty()) {
            try {
                price = Float.parseFloat(articlePrice);
                if(price < 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "price must be greater or equal to zero");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Price in input is not a valid number");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Price is null");
            return;
        }
        
        String userHome = System.getProperty("user.home");
        String pathString = userHome + "/git/TIW-project-2023-pure-HTML-Polverini-Ye/TIW-project-2023-pure-HTML-Polverini-Ye/src/main/webapp/images";
        String pathString1 = userHome + "/git/TIW-project-2023-RIA-Polverini-Ye/TIW-project-2023-RIA-Polverini-Ye/src/main/webapp/images";
        Path path = Paths.get(pathString);
        Path path1 = Paths.get(pathString1);
        
        String uploadDirectory = path.toString();
        String uploadDirectory1 = path1.toString();
        
        File dir = new File(uploadDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File dir1 = new File(uploadDirectory1);
        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        
        String fileName = null;
        
        try {
            Part filePart = request.getPart("imageToUpload");
            if (filePart.getSize() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no file uploaded" );
                return;
            }
            
            fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
            String fileExtension = getFileExtension(fileName);
            
            if (isAllowedExtension(fileExtension)) {
                File file = new File(uploadDirectory, fileName);
                File file1 = new File(uploadDirectory1, fileName);
                
                try (InputStream inputStream = filePart.getInputStream()) { 
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(inputStream, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file type. Only JPG, JPEG, PNG, and GIF files are allowed.");
                return;
            }
            
        } catch (Exception ex) {
            response.getWriter().println("Error uploading file: " + ex.getMessage());
            return;
        }
        
        ArticleDAO articleDAO = new ArticleDAO(connection);
        
        try {
            connection.setAutoCommit(false);

            articleDAO.createArticle(articleName, articleDesc, price, fileName, user.getUserMail());

            connection.commit();
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server db error, retry later");
            
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        response.sendRedirect("GoToSell");
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    private boolean isAllowedExtension(String fileExtension) {
        String[] allowedExtensions = {"jpg", "jpeg", "png"};
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
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
