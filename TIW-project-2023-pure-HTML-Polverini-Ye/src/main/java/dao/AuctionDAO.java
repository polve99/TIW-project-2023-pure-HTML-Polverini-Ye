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
        //if (isAuctionInDB(idAuction)) return false;

        String query = "INSERT INTO dbaste.auctions (initialPrice, minRise, expirationDateTime, userMail) VALUES (?, ?, ?, ?)";
        PreparedStatement pStatement = null;
        ResultSet keys = null;
        int idAuction=0;

        try {
            pStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pStatement.setFloat(1, initialPrice);
            pStatement.setFloat(2, minRise);
            pStatement.setTimestamp(3, expirationDateTime);
            pStatement.setString(4, userMail);
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
        return  idAuction;
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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
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

    public ArrayList<Auction> findAuctionsListByWordSearch(String word) throws SQLException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        String query = "SELECT * FROM dbaste.auctions " +
                "JOIN dbaste.articles ON dbaste.auctions.idAuction = dbaste.articles.idAuction " +
                "WHERE (dbaste.articles.articleName LIKE ? OR dbaste.articles.articleDescription LIKE ?) " +
                "AND dbaste.auctions.expirationDateTime > ? " +
                "ORDER BY dbaste.auctions.expirationDateTime ASC";
        PreparedStatement pStatement = null;

        try {
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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
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
                auction.setExpirationDateTime(result.getTimestamp("expirationDateTime"));
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
                auctions.add(auction);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }

        return auctions;
    }

    public ArrayList<Object> getAuctionClosedInfosForTable(Auction auction) throws SQLException{
        if(isAuctionOpen(auction.getIdAuction())) return null;
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

}
