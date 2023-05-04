package dao;

import beans.Bid;
import beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

public class BidDAO {
	
	public Connection connection;
	
	public BidDAO(Connection connection) {
        this.connection = connection;
    }

	public void createBid(String userMail, int bidValue, Timestamp bidDateTime, int idAuction) throws SQLException {
		String query = "INSERT INTO bids (bidValue, bidDateTime, userMail, idAuction) VALUES (?,?,?,?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, bidValue);
			pstatement.setTimestamp(2, new Timestamp (System.currentTimeMillis()));
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
	
	public Bid findMaxBid() throws SQLException {
		Bid bid = null;
		String query = "SELECT * FROM bids WHERE bidValue = {SELECT MAX(bidValue) FROM bids}";
		PreparedStatement pstatement = null;
		ResultSet res = null;
		try {
			pstatement = connection.prepareStatement(query);
			res = pstatement.executeQuery();
			if (!res.next()) return null;
			int id = res.getInt("bidId");
			int value = res.getInt("bidValue");
			Timestamp creationDateTime = res.getTimestamp("bidDateTime");
			String userMail = res.getString("userMail");
			int idAuction = res.getInt("idAuction");
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
		List<Bid> 
	}
}
