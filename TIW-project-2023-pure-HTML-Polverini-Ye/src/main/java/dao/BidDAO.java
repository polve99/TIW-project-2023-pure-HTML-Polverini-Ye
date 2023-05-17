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

    public void createBid(int idBid, float bidValue, Timestamp bidDateTime, String userMail, int idAuction) throws SQLException {
        String query = "INSERT INTO bids (idBid, bidValue, bidDateTime, userMail, idAuction) VALUES (?,?,?,?,?)";
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, idBid);
            pstatement.setFloat(2, bidValue);
            pstatement.setTimestamp(3, bidDateTime);
            pstatement.setString(4, userMail);
            pstatement.setInt(5, idAuction);
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
        Bid bid = new Bid();
        String query = "SELECT * FROM bids WHERE idBid = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idBid);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
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
        ArrayList<Bid> bids = new ArrayList<>();
        String query = "SELECT * FROM bids WHERE userMail = ?";
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
        ArrayList<Bid> bids = new ArrayList<>();
        String query = "SELECT * FROM bids WHERE idAuction = ?";
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
        Bid bid = new Bid();
        String query = "SELECT * FROM bids WHERE idAuction = ? AND bidValue = {SELECT MAX(bidValue) FROM bids WHERE idAuction = ?}";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setInt(1, idAuction);
            pStatement.setInt(2, idAuction);
            resultSet = pStatement.executeQuery();
            while (resultSet.next()) {
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
        ArrayList<Bid> orderedBids = new ArrayList<>();
        String query = "SELECT * FROM bids ORDER BY bidValue DESC";
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

    /* lascio nel caso fossero da riguardare ma le ho riscritte sopra (credo bene)

    public Bid findBidByUser(String userMail) throws SQLException {
        Bid bid = null;
        String query  = "SELECT * FROM bids WHERE userMail = ?";
        PreparedStatement pstatement = null;
        ResultSet res = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, userMail);
            res = pstatement.executeQuery();
            if (!res.next()) return null;
            int id = res.getInt("bidId");
            int value = res.getInt("bidValue");
            Timestamp creationDateTime = res.getTimestamp("bidDateTime");
            int auctionId = res.getInt("idAuction");
            bid = new Bid(id, userMail, value, creationDateTime, auctionId);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return bid;
    }

    public Bid findBidByAuction(int idAuction) throws SQLException {
        Bid bid = null;
        String query  = "SELECT * FROM bids WHERE idAuction = ?";
        PreparedStatement pstatement = null;
        ResultSet res = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, idAuction);
            res = pstatement.executeQuery();
            if (!res.next()) return null;
            int id = res.getInt("bidId");
            int value = res.getInt("bidValue");
            Timestamp creationDateTime = res.getTimestamp("bidDateTime");
            String userMail = res.getString("userMail");
            bid = new Bid(id, userMail, value, creationDateTime, idAuction);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }

        return bid;
    }

    public Bid findMaxBid(int idAuction) throws SQLException {
        Bid bid = null;
        String query = "SELECT * FROM bids WHERE idAuction = ? AND bidValue = {SELECT MAX(bidValue) FROM bids WHERE idAuction = ?}";
        PreparedStatement pstatement = null;
        ResultSet res = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, idAuction);
            pstatement.setInt(2, idAuction);
            res = pstatement.executeQuery();
            if (!res.next()) return null;
            int id = res.getInt("bidId");
            float value = res.getFloat("bidValue");
            Timestamp creationDateTime = res.getTimestamp("bidDateTime");
            String userMail = res.getString("userMail");
            bid = new Bid(id, userMail, value, creationDateTime, idAuction);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }

        return bid;
    }

    public List<Bid> orderBidList() throws SQLException {
        List<Bid> bids = null;
        String query = "SELECT * FROM bids ORDER BY bidValue DESC()";
        PreparedStatement pstatement = null;
        ResultSet res = null;
        try {
            pstatement = connection.prepareStatement(query);
            res = pstatement.executeQuery();
            bids = new ArrayList<>();
            while (res.next()) {
                int id = res.getInt("bidId");
                float value = res.getFloat("bidValue");
                Timestamp creationDateTime = res.getTimestamp("bidDateTime");
                String userMail = res.getString("userMail");
                int idAuction = res.getInt("idAuction");
                bids.add(new Bid(id, userMail, value, creationDateTime, idAuction));
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return bids;
    }

     */
}
