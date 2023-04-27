package beans;

public class Bid {

    private int bidId;
    private float bidValue;
    private String bidDateTime;
    private String userMail;
    private int idAuction;

    public Bid(int bidId, float bidValue, String bidDateTime, String userMail, int idAuction) {
        this.bidId = bidId;
        this.bidValue = bidValue;
        this.bidDateTime = bidDateTime;
        this.userMail = userMail;
        this.idAuction = idAuction;
    }

    public int getBidId() {
        return bidId;
    }

    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    public float getBidValue() {
        return bidValue;
    }

    public void setBidValue(float bidValue) {
        this.bidValue = bidValue;
    }

    public String getBidDateTime() {
        return bidDateTime;
    }

    public void setBidDateTime(String bidDateTime) {
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
