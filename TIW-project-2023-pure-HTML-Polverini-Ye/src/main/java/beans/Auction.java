package beans;

public class Auction {

    private int idAuction;
    private float initialPrice;
    private float minRise;
    private String expirationDateTime;
    private String userMail;

    public Auction() {
    }

    public Auction(int idAuction, float initialPrice, float minRise, String expirationDateTime, String userMail) {
        this.idAuction = idAuction;
        this.initialPrice = initialPrice;
        this.minRise = minRise;
        this.expirationDateTime = expirationDateTime;
        this.userMail = userMail;
    }

    public int getIdAuction() {
        return idAuction;
    }

    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }

    public float getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(float initialPrice) {
        this.initialPrice = initialPrice;
    }

    public float getMinRise() {
        return minRise;
    }

    public void setMinRise(float minRise) {
        this.minRise = minRise;
    }

    public String getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

}
