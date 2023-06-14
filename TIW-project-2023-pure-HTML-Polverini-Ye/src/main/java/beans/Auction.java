package beans;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Auction {

    private int idAuction;
    private float initialPrice;
    private float minRise;
    private Timestamp expirationDateTime;
    private String userMail;
    private boolean isOpen;

    private ArrayList<Article> articles;

    public Auction() {
        super();
    }

    public Auction(int idAuction, float initialPrice, float minRise, String expirationDateTime, String userMail) {
        this.idAuction = idAuction;
        this.initialPrice = initialPrice;
        this.minRise = minRise;
        this.expirationDateTime = Timestamp.valueOf(expirationDateTime);
        this.userMail = userMail;
        this.isOpen = true;

        this.articles = new ArrayList<>();
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

    public Timestamp getExpirationDateTime() {
        return expirationDateTime;
    }
    public void setExpirationDateTime(Timestamp expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public String getUserMail() {
        return userMail;
    }
    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }
    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }
}
