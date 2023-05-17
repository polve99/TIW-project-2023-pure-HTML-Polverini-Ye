package beans;

import java.sql.Timestamp;

public class Bid {

    private int idBid;
    private float bidValue;
    private Timestamp bidDateTime;
    private String userMail;
    private int idAuction;

    public Bid() {
    }

    public Bid(int idBid, String userMail, float bidValue, Timestamp bidDateTime, int idAuction) {
        this.idBid = idBid;
        this.bidValue = bidValue;
        this.bidDateTime = bidDateTime;
        this.userMail = userMail;
        this.idAuction = idAuction;
    }

    public int getIdBid() {
        return idBid;
    }
    public void setIdBid(int idBid) {
        this.idBid = idBid;
    }

    public float getBidValue() {
        return bidValue;
    }
    public void setBidValue(float bidValue) {
        this.bidValue = bidValue;
    }

    public Timestamp getBidDateTime() {
        return bidDateTime;
    }
    public void setBidDateTime(Timestamp bidDateTime) {
        this.bidDateTime = bidDateTime;
    }

    public String getUserMail() {
        return userMail;
    }
    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public int getIdAuction() {
        return idAuction;
    }
    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }

}
