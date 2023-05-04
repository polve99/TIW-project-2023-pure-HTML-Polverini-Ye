package dao;

import beans.Article;
import beans.Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuctionDAO {

    private Connection connection;

    public AuctionDAO(Connection connection){
        this.connection = connection;
    }

    public boolean createAuction(Auction auction) throws SQLException {
        String query = "INSERT INTO Auction (idAuction, initialPrice, minRise, expirationDateTime, userMail) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, auction.getIdAuction());
            pStatement.setFloat(2, auction.getInitialPrice());
            pStatement.setFloat(3, auction.getMinRise());
            pStatement.setString(4, auction.getExpirationDateTime());
            pStatement.setString(5, auction.getUserMail());
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

    public boolean addArticleInAuction(int idAuction, int articleCode) throws SQLException{
        //lavoro sulla tabella di article
        String query = "UPDATE Article SET idAuction = ? WHERE articleCode = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setInt(2, articleCode);
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

    public boolean addArticlesInAuction(int idAuction, ArrayList<Article> articleArrayList) throws SQLException{
        //è chiamato dal controller(?) quando deve creare un'asta con più articoli

        for (Article article : articleArrayList) {
            addArticleInAuction(idAuction, article.getArticleCode());
        }
        return true;

    }

    //TODO non so se va qui? c'entra con il DB o andrebbe nel controller? perché per ora ho messo attributo isClosed in Auction
    public boolean closeAuction(int idAuction) throws SQLException {
        String query = "UPDATE Auction SET closed = 1 WHERE idAuction = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
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

    public boolean findAuctionByIdAuction(int idAuction) throws SQLException {
        boolean found = false;
        String query = "SELECT * FROM Auction WHERE idAuction = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
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

    public boolean findAuctionByMail(String userMail) throws SQLException {
        boolean found = false;
        String query = "SELECT * FROM Auction WHERE userMail = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
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

    public boolean findAuctionBySearchInNameAndDescription(String word) throws SQLException {
        boolean found = false;
        //faccio la ricerca sulla tabella Article
        String query = "SELECT * FROM Article WHERE articleName LIKE ? OR articleDescription LIKE ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, "%" + word + "%");
            pStatement.setString(2, "%" + word + "%");
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

}

