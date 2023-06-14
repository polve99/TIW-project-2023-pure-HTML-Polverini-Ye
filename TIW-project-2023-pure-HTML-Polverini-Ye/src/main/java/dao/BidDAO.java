package dao;

import beans.Bid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class BidDAO {

    public Connection connection;

    public BidDAO(Connection connection) {
        this.connection = connection;
    }

    public void createBid(float bidValue, String userMail, int idAuction) throws SQLException {
        String query = "INSERT INTO dbaste.bids (bidValue, bidDateTime, userMail, idAuction) VALUES (?,?,?,?)";
        PreparedStatement pstatement = null;
        Timestamp bidDateTime = new Timestamp(System.currentTimeMillis());
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setFloat(1, bidValue);
            pstatement.setTimestamp(2, bidDateTime);
            pstatement.setString(3, userMail);
            pstatement.setInt(4, idAuction);
            pstatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        }
    }

    public Bid findBidByIdBid(int idBid) throws SQLException{
        Bid bid = null;
        String query = "SELECT * FROM dbaste.bids WHERE idBid = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idBid);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
            	bid = new Bid();
                bid.setIdBid(resultSet.getInt("idBid"));
                bid.setBidValue(resultSet.getFloat("bidValue"));
                bid.setBidDateTime(resultSet.getTimestamp("bidDateTime"));
                bid.setUserMail(resultSet.getString("userMail"));
                bid.setIdAuction(resultSet.getInt("idAuction"));
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
        return bid;
    }

    public ArrayList<Bid> findBidsListByUserMail(String userMail) throws SQLException {
        ArrayList<Bid> bids = new ArrayList<Bid>();
        String query = "SELECT * FROM dbaste.bids WHERE userMail = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                Bid bid = new Bid();
                bid.setIdBid(resultSet.getInt("idBid"));
                bid.setBidValue(resultSet.getFloat("bidValue"));
                bid.setBidDateTime(resultSet.getTimestamp("bidDateTime"));
                bid.setUserMail(resultSet.getString("userMail"));
                bid.setIdAuction(resultSet.getInt("idAuction"));
                bids.add(bid);
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
        return bids;
    }

    public ArrayList<Bid> findBidsListByIdAuction(int idAuction) throws SQLException{
        ArrayList<Bid> bids = new ArrayList<Bid>();
        String query = "SELECT * FROM dbaste.bids WHERE idAuction = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                Bid bid = new Bid();
                bid.setIdBid(resultSet.getInt("idBid"));
                bid.setBidValue(resultSet.getFloat("bidValue"));
                bid.setBidDateTime(resultSet.getTimestamp("bidDateTime"));
                bid.setUserMail(resultSet.getString("userMail"));
                bid.setIdAuction(resultSet.getInt("idAuction"));
                bids.add(bid);
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
        return bids;
    }

    public Bid findMaxBidInAuction(int idAuction) throws SQLException {
        Bid bid = null;
        String query = "SELECT * FROM dbaste.bids WHERE idAuction = ? AND bidValue = (SELECT MAX(bidValue) FROM dbaste.bids WHERE idAuction = ?)";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setInt(2, idAuction);
            resultSet = pStatement.executeQuery();
            if (resultSet.next()) {
            	bid = new Bid();
                bid.setIdBid(resultSet.getInt("idBid"));
                bid.setBidValue(resultSet.getFloat("bidValue"));
                bid.setBidDateTime(resultSet.getTimestamp("bidDateTime"));
                bid.setUserMail(resultSet.getString("userMail"));
                bid.setIdAuction(resultSet.getInt("idAuction"));
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
        return bid;
    }

    public ArrayList<Bid> getBidsListInDescOrder() throws SQLException {
        ArrayList<Bid> orderedBids = new ArrayList<Bid>();
        String query = "SELECT * FROM dbaste.bids ORDER BY bidValue DESC";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
                Bid bid = new Bid();
                bid.setIdBid(resultSet.getInt("idBid"));
                bid.setBidValue(resultSet.getFloat("bidValue"));
                bid.setBidDateTime(resultSet.getTimestamp("bidDateTime"));
                bid.setUserMail(resultSet.getString("userMail"));
                bid.setIdAuction(resultSet.getInt("idAuction"));
                orderedBids.add(bid);
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
        return orderedBids;
    }

}
