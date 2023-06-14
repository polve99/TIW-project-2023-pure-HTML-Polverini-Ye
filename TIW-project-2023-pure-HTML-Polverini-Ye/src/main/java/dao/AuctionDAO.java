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

    public int createAuction(float initialPrice, float minRise, Timestamp expirationDateTime, String userMail) throws SQLException {
        String query = "INSERT INTO dbaste.auctions (initialPrice, minRise, expirationDateTime, userMail, isOpen) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pStatement = null;
        ResultSet keys = null;
        int idAuction = 0;

        try {
            pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pStatement.setFloat(1, initialPrice);
            pStatement.setFloat(2, minRise);
            pStatement.setTimestamp(3, expirationDateTime);
            pStatement.setString(4, userMail);
            pStatement.setBoolean(5, true);
            pStatement.executeUpdate();
            keys = pStatement.getGeneratedKeys();
            boolean auctionCreated = keys.next();
            if (!auctionCreated) {
                return 0;
            }
            idAuction = keys.getInt(1);
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
        return idAuction;
    }

    public boolean closeAuction(int idAuction) throws SQLException {
        String query = "UPDATE dbaste.auctions SET isOpen = ? WHERE idAuction = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setBoolean(1, false);
            pStatement.setInt(2, idAuction);
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

    public boolean putBackArticles(int idAuction) {
        String query = "UPDATE dbaste.articles SET idAuction = NULL WHERE idAuction = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean addArticleInAuction(int idAuction, int articleCode) throws SQLException{
        if(!isAuctionNotExpired(idAuction)) return false;

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
        if(!isAuctionNotExpired(idAuction)) return false;

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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auction.setOpen(result.getBoolean("isOpen"));
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

    public ArrayList<Auction> findAuctionsListByWordSearch(String word) throws SQLException {
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions " +
                "JOIN dbaste.articles ON dbaste.auctions.idAuction = dbaste.articles.idAuction " +
                "WHERE (dbaste.articles.articleName LIKE ? OR dbaste.articles.articleDescription LIKE ?) " +
                "AND dbaste.auctions.isOpen = 1 " +
                "ORDER BY dbaste.auctions.expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, "%" + word + "%");
            pStatement.setString(2, "%" + word + "%");
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auction.setOpen(result.getBoolean("isOpen"));
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

    public boolean isAuctionInDB(int idAuction) throws SQLException{
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

    public boolean isAuctionNotExpired(int idAuction) throws SQLException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String query = "SELECT * FROM dbaste.auctions WHERE idAuction = ? AND expirationDateTime > ?";
        PreparedStatement pStatement = null;
        boolean isNotExpired = false;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setTimestamp(2, now);
            ResultSet result = pStatement.executeQuery();

            if (result.next()) {
            	isNotExpired = true;
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
        return isNotExpired;
    }

    public ArrayList<Auction> getAllOpenAuctions() throws SQLException{
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE isOpen = 1 ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auction.setOpen(result.getBoolean("isOpen"));
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

    public ArrayList<Auction> getAllOpenAuctionsByUser(String userMail) throws SQLException{
        String mail = userMail;
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE isOpen = 1 AND userMail = ? ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, mail);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auction.setOpen(result.getBoolean("isOpen"));
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
        String mail = userMail;
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions WHERE isOpen = 0 AND userMail = ? ORDER BY expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, mail);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auction.setOpen(false);
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

    public ArrayList<Auction> getWonClosedAuctionsByUser(String userMail) throws SQLException {
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT a.idAuction, a.initialPrice, a.minRise, a.expirationDateTime, a.userMail " +
                "FROM dbaste.auctions AS a " +
                "INNER JOIN ( " +
                "SELECT b.idAuction, MAX(b.bidValue) AS maxBidValue " +
                "FROM dbaste.bids AS b " +
                "WHERE b.idAuction IN ( " +
                "SELECT idAuction " +
                "FROM dbaste.bids " +
                "WHERE userMail = ? " +
                ") " +
                "GROUP BY b.idAuction " +
                ") AS maxBids ON a.idAuction = maxBids.idAuction " +
                "WHERE a.expirationDateTime <= CURRENT_TIMESTAMP() " +
                "AND a.isOpen = 0 " +
                "AND a.userMail != ? " +
                "AND a.idAuction IN ( " +
                "SELECT idAuction " +
                "FROM dbaste.bids " +
                "WHERE userMail = ? " +
                "AND bidValue = maxBids.maxBidValue " +
                ") " +
                "ORDER BY a.expirationDateTime ASC";

        try (PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, userMail);
            pStatement.setString(2, userMail);
            pStatement.setString(3, userMail);
            ResultSet result = pStatement.executeQuery();

            while (result.next()) {
                Auction auction = new Auction();
                auction.setIdAuction(result.getInt("idAuction"));
                auction.setInitialPrice(result.getFloat("initialPrice"));
                auction.setMinRise(result.getFloat("minRise"));
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
                auction.setUserMail(result.getString("userMail"));
                auction.setOpen(false);
                auctions.add(auction);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return auctions;
    }

    public ArrayList<Object> getAuctionClosedInfosForTable(Auction auction) throws SQLException{
        //if(isAuctionOpen(auction.getIdAuction())) return null;
    	if(auction.isOpen()) return null;
        //l'asta deve essere chiusa per poter calcolare i dati finali

        ArrayList<Object> auctionClosedInfos = new ArrayList<>();
        BidDAO bidDAO = new BidDAO(connection);
        Bid maxBid = bidDAO.findMaxBidInAuction(auction.getIdAuction());

        ArticleDAO articleDAO = new ArticleDAO(connection);
        ArrayList<Article> articles = articleDAO.findArticlesListByIdAuction(auction.getIdAuction());

        auctionClosedInfos.add(maxBid);
        auctionClosedInfos.add(articles);

        return auctionClosedInfos;
    }

    public ArrayList<Object> getAuctionClosedInfos(Auction auction) throws SQLException{
        if(auction.isOpen()) return null;
        //l'asta deve essere chiusa per poter calcolare i dati finali

        ArrayList<Object> auctionClosedInfos = new ArrayList<>();

        //calcola i dati finali dell'asta ogni volta che viene chiamato
        BidDAO bidDAO = new BidDAO(connection);
        Bid maxBid = bidDAO.findMaxBidInAuction(auction.getIdAuction());
        
        if(maxBid==null) {
        	float finalPrice = auction.getInitialPrice();
        	auctionClosedInfos.add(finalPrice);
        	auctionClosedInfos.add("no one");
            auctionClosedInfos.add("no address");
            
        } else {
        	float finalPrice = maxBid.getBidValue();
        	auctionClosedInfos.add(finalPrice);
        	
            String winnerMail = maxBid.getUserMail();
            UserDAO userDAO = new UserDAO(connection);
            User winner = userDAO.findUserByUserMail(winnerMail);
            
            if(winner==null) {
            	auctionClosedInfos.add("no one");
                auctionClosedInfos.add("no address");
            } else {
            	String shippingAddress = winner.getAddress();
                auctionClosedInfos.add(winnerMail);
                auctionClosedInfos.add(shippingAddress);
            }
            
        }

        return auctionClosedInfos;
    }

}
