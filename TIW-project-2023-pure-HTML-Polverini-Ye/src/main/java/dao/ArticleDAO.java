package dao;

import beans.Article;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArticleDAO {

    private Connection connection;

    public ArticleDAO(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public boolean createArticleWithoutImage(Article article) throws SQLException {
        String query = "INSERT INTO Article (articleCode, articleName, articleDescription, image, articlePrice, idAuction) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, article.getArticleCode());
            pStatement.setString(2, article.getArticleName());
            pStatement.setString(3, article.getArticleDescription());
            pStatement.setString(4, article.getImage());
            pStatement.setFloat(5, article.getArticlePrice());
            pStatement.setInt(6, article.getIdAuction());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return true;
    }


    //TODO: gestione immagini -> per ora nella createArticle ho impostato image come un path, è da vedere come impostare FileUtils*/
    public void createArticle(Article article) throws SQLException, IOException {
        // caricamento dell'immagine dal file system
        File imageFile = new File(article.getImage());

        // salvataggio dell'immagine in una cartella specifica
        String fileName = imageFile.getName();
        String newFilePath = "path/to/image/folder/" + fileName;
        File newImageFile = new File(newFilePath);
        //TODO: rimettere dopo aver impostato il pom.xml correttamente
        //FileUtils.copyFile(imageFile, newImageFile);

        // aggiornamento dell'oggetto "Article" con il nuovo percorso dell'immagine
        article.setImage(newFilePath);

        // salvataggio dell'articolo nel database
        String query = "INSERT INTO articles (articleCode, articleName, articleDescription, image, articlePrice, idAuction) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, article.getArticleCode());
            statement.setString(2, article.getArticleName());
            statement.setString(3, article.getArticleDescription());
            statement.setString(4, article.getImage());
            statement.setFloat(5, article.getArticlePrice());
            statement.setInt(6, article.getIdAuction());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error saving article", e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e2) {
                throw new SQLException("Error closing statement", e2);
            }
        }
    }

    public boolean deleteArticle(int articleCode) throws SQLException {
        String query = "DELETE FROM Article WHERE articleCode = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, articleCode);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return true;
    }

    public boolean updateArticle(Article article) throws SQLException {
        String query = "UPDATE Article SET articleName = ?, articleDescription = ?, image = ?, articlePrice = ?, idAuction = ? WHERE articleCode = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, article.getArticleName());
            pStatement.setString(2, article.getArticleDescription());
            pStatement.setString(3, article.getImage());
            pStatement.setFloat(4, article.getArticlePrice());
            pStatement.setInt(5, article.getIdAuction());
            pStatement.setInt(6, article.getArticleCode());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return true;
    }

    public boolean findArticleByArticleCode(int articleCode) throws SQLException {
        boolean found = false;
        String query = "SELECT * FROM Article WHERE articleCode = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, articleCode);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) found = true;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return found;
    }

    public boolean findArticleByIdAuction(int articleCode, int idAuction) throws SQLException {
        boolean found = false;
        String query = "SELECT * FROM Article WHERE articleCode = ? AND idAuction = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, articleCode);
            pStatement.setInt(2, idAuction);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) found = true;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return found;
    }



    //TODO: non so se vanno nei DAO perchè nei DAO dovrebbero esserci solo le query però se ho bisogno che restituisca un oggetto servono per forza

    public Article findArticleObjByArticleCode(int articleCode, int idAuction) throws SQLException {
        Article article = null;
        String query = "SELECT * FROM Article WHERE articleCode = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, articleCode);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                article = new Article();
                article.setArticleCode(resultSet.getInt("articleCode"));
                article.setArticleName(resultSet.getString("articleName"));
                article.setArticleDescription(resultSet.getString("articleDescription"));
                article.setImage(resultSet.getString("image"));
                article.setArticlePrice(resultSet.getFloat("articlePrice"));
                article.setIdAuction(resultSet.getInt("idAuction"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return article;
    }

    public Article findArticleObjByArticleCodeAndIdAuction(int articleCode, int idAuction) throws SQLException {
        Article article = null;
        String query = "SELECT * FROM Article WHERE articleCode = ? AND idAuction = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, articleCode);
            pStatement.setInt(2, idAuction);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                article = new Article();
                article.setArticleCode(resultSet.getInt("articleCode"));
                article.setArticleName(resultSet.getString("articleName"));
                article.setArticleDescription(resultSet.getString("articleDescription"));
                article.setImage(resultSet.getString("image"));
                article.setArticlePrice(resultSet.getFloat("articlePrice"));
                article.setIdAuction(resultSet.getInt("idAuction"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return article;
    }

    public ArrayList<Article> findArticlesListByAuctionId(int idAuction) throws SQLException {
        ArrayList<Article> articles = new ArrayList<Article>();
        String query = "SELECT * FROM Article WHERE idAuction = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            resultSet = pStatement.executeQuery();

            while (resultSet.next()) {
                Article article = new Article();
                article.setArticleCode(resultSet.getInt("articleCode"));
                article.setArticleName(resultSet.getString("articleName"));
                article.setArticleDescription(resultSet.getString("articleDescription"));
                article.setImage(resultSet.getString("image"));
                article.setArticlePrice(resultSet.getFloat("articlePrice"));
                article.setIdAuction(resultSet.getInt("idAuction"));
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return articles;
    }

    public ArrayList<Article> findArticlesListByAuctionIdAndArticleCode(int idAuction, int articleCode) throws SQLException {
        ArrayList<Article> articles = new ArrayList<Article>();
        String query = "SELECT * FROM Article WHERE idAuction = ? AND articleCode = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setInt(2, articleCode);
            resultSet = pStatement.executeQuery();

            while (resultSet.next()) {
                Article article = new Article();
                article.setArticleCode(resultSet.getInt("articleCode"));
                article.setArticleName(resultSet.getString("articleName"));
                article.setArticleDescription(resultSet.getString("articleDescription"));
                article.setImage(resultSet.getString("image"));
                article.setArticlePrice(resultSet.getFloat("articlePrice"));
                article.setIdAuction(resultSet.getInt("idAuction"));
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return articles;
    }

}

