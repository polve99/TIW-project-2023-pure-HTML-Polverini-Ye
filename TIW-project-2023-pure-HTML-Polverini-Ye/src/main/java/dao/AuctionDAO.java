package dao;

import beans.Article;
import beans.Auction;
import beans.Bid;
import beans.User;

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

    public boolean closeAuction(int idAuction) throws SQLException {
        BidDAO bidDAO = new BidDAO(connection);
        Bid bid = bidDAO.findMaxBid(idAuction); //nella tabella bid cerchi la più alta

        float finalPrice = bid.getBidValue();
        String winnerMail = bid.getUserMail();

        UserDAO userDAO = new UserDAO(connection);
        User winner = userDAO.findUser(winnerMail);
        String shippingAddress = winner.getAddress(); //nella tabella user

        //SELECT
        //FROM BID
        //WHERE idAuction = idAuction


        String query = "UPDATE Auction SET winnerMail = ? AND finalPrice = ? AND shippingAddress = ? WHERE idAuction = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, winnerMail);
            pStatement.setFloat(2, finalPrice);
            pStatement.setString(3, shippingAddress);
            pStatement.setInt(4, idAuction);
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

    public Auction findAuctionByIdAuction(int idAuction) throws SQLException {
        String query = "SELECT * FROM Auction WHERE idAuction = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()){
                Auction auction = new Auction();
                auction.setIdAuction(resultSet.getInt("idAuction"));
                auction.setInitialPrice(resultSet.getFloat("initialPrice"));
                auction.setMinRise(resultSet.getFloat("minRise"));
                auction.setExpirationDateTime(resultSet.getString("expirationDateTime"));
                auction.setUserMail(resultSet.getString("userMail"));
 //               auction.setWinnerMail(resultSet.getString("winnerMail"));
   //             auction.setFinalPrice(resultSet.getFloat("finalPrice"));
     //           auction.setShippingAddress(resultSet.getString("shippingAddress"));
                return auction;
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
        return null;
    }

    public Auction findAuctionByMail(String userMail) throws SQLException {
        String query = "SELECT * FROM Auction WHERE userMail = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            resultSet = pStatement.executeQuery();

            if (resultSet.next()){
                Auction auction = new Auction();
                auction.setIdAuction(resultSet.getInt("idAuction"));
                auction.setInitialPrice(resultSet.getFloat("initialPrice"));
                auction.setMinRise(resultSet.getFloat("minRise"));
                auction.setExpirationDateTime(resultSet.getString("expirationDateTime"));
                auction.setUserMail(resultSet.getString("userMail"));
   //             auction.setWinnerMail(resultSet.getString("winnerMail"));
   //             auction.setFinalPrice(resultSet.getFloat("finalPrice"));
   //             auction.setShippingAddress(resultSet.getString("shippingAddress"));
                return auction;
            };
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
        return null;
    }

    public ArrayList<Auction> findAuctionBySearchInNameAndDescription(String word) throws SQLException {
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
