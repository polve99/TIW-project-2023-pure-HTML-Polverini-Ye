package dao;

import beans.Article;
import beans.Auction;
import beans.Bid;
import beans.User;

import java.sql.*;
import java.util.ArrayList;

public class AuctionDAO {

    private Connection connection;

    public AuctionDAO(Connection connection){
        this.connection = connection;
    }

    public boolean createAuction(int idAuction, float initialPrice, float minRise, String expirationDateTime, String userMail) throws SQLException {
        if (isAuctionInDB(idAuction)) return false;

        String query = "INSERT INTO dbaste.auctions (idAuction, initialPrice, minRise, expirationDateTime, userMail) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setFloat(2, initialPrice);
            pStatement.setFloat(3, minRise);
            pStatement.setString(4, expirationDateTime);
            pStatement.setString(5, userMail);
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
        if(!isAuctionOpen(idAuction)) return false;

        //lavoro sulla tabella di article
        String query = "UPDATE dbaste.articles SET idAuction = ? WHERE articleCode = ?";
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

    public boolean addArticlesInAuction(int idAuction, ArrayList<Article> articlesToAdd) throws SQLException{
        if(!isAuctionOpen(idAuction)) return false;

        //è chiamato dal controller(?) quando deve creare un'asta con più articoli
        for (Article article : articlesToAdd) {
            addArticleInAuction(idAuction, article.getArticleCode());
        }
        return true;
    }

    public Auction findAuctionByIdAuction(int idAuction) throws SQLException {
        String query = "SELECT * FROM dbaste.auctions WHERE idAuction = ?";
        PreparedStatement pStatement = null;
        Auction auction = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            ResultSet result = pStatement.executeQuery();

            if (result.next()) {
                auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getString("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
            }
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
        return auction;
    }

    public ArrayList<Auction> findAuctionsListByUserMail(String userMail) throws SQLException{
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE userMail = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getString("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auctions.add(auction);
            }
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
        return auctions;
    }

    public ArrayList<Auction> findAuctionsListByWordSearch(String word) throws SQLException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions JOIN articles " +
                "ON auctions.idAuction = articles.idAuction " +
                "WHERE articles.name LIKE ? OR articles.description LIKE ? AND expirationDateTime > ? " +
                "ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, "%" + word + "%");
            pStatement.setString(2, "%" + word + "%");
            pStatement.setTimestamp(3, now);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getString("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auctions.add(auction);
            }
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try{
                if (pStatement != null){
                    pStatement.close();
                }
            } catch (Exception e2){
                throw new SQLException(e2);
            }
        }
        return auctions;
    }

    public ArrayList<Object> getAuctionClosedInfos(Auction auction) throws SQLException{
        if(isAuctionOpen(auction.getIdAuction())) return null;
        //l'asta deve essere chiusa per poter calcolare i dati finali

        ArrayList<Object> auctionClosedInfos = new ArrayList<>();

        //calcola i dati finali dell'asta ogni volta che viene chiamato
        BidDAO bidDAO = new BidDAO(connection);
        Bid maxBid = bidDAO.findMaxBidInAuction(auction.getIdAuction());

        float finalPrice = maxBid.getBidValue();
        String winnerMail = maxBid.getUserMail();

        UserDAO userDAO = new UserDAO(connection);
        User winner = userDAO.findUserByUserMail(winnerMail);
        String shippingAddress = winner.getAddress();

        auctionClosedInfos.add(finalPrice);
        auctionClosedInfos.add(winnerMail);
        auctionClosedInfos.add(shippingAddress);

        return auctionClosedInfos;
    }

    private boolean isAuctionInDB(int idAuction) throws SQLException{
        String query = "SELECT * FROM dbaste.auctions WHERE idAuction = ?";
        PreparedStatement pStatement = null;
        boolean inDB = false;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            ResultSet result = pStatement.executeQuery();

            if (result.next()) {
                inDB = true;
            }
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
        return inDB;
    }

    public boolean isAuctionOpen(int idAuction) throws SQLException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String query = "SELECT * FROM dbaste.auctions WHERE idAuction = ? AND expirationDateTime > ?";
        PreparedStatement pStatement = null;
        boolean isOpen = false;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setTimestamp(2, now);
            ResultSet result = pStatement.executeQuery();

            if (result.next()) {
                isOpen = true;
            }
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try{
                if (pStatement != null){
                    pStatement.close();
                }
            } catch (Exception e2){
                throw new SQLException(e2);
            }
        }
        return isOpen;
    }

    public ArrayList<Auction> getAllOpenAuctions() throws SQLException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE expirationDateTime > ? ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setTimestamp(1, now);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getString("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auctions.add(auction);
            }
        } catch (SQLException e){
        	e.printStackTrace();
            throw new SQLException(e);
        } finally {
            try{
                if (pStatement != null){
                    pStatement.close();
                }
            } catch (Exception e2){
            	e2.printStackTrace();
                throw new SQLException(e2);
            }
        }
        return auctions;
    }
    
    public ArrayList<Auction> getAllOpenAuctionsByUser(String userMail) throws SQLException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String mail = userMail;
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE expirationDateTime > ? AND userMail = ? ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setTimestamp(1, now);
            pStatement.setString(2, mail);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getString("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auctions.add(auction);
            }
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try{
                if (pStatement != null){
                    pStatement.close();
                }
            } catch (Exception e2){
                throw new SQLException(e2);
            }
        }
        return auctions;
    }
    
    public ArrayList<Auction> getAllClosedAuctionsByUser(String userMail) throws SQLException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String mail = userMail;
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE expirationDateTime <= ? AND userMail = ? ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setTimestamp(1, now);
            pStatement.setString(2, mail);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getString("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auctions.add(auction);
            }
        } catch (SQLException e){
            throw new SQLException(e);
        } finally {
            try{
                if (pStatement != null){
                    pStatement.close();
                }
            } catch (Exception e2){
                throw new SQLException(e2);
            }
        }
        return auctions;
    }


}
