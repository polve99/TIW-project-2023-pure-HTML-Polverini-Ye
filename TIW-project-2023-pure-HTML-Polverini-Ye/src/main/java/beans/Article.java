package beans;

public class Article {

    private int articleCode;
    private String articleName;
    private String articleDescription;
    private String image; 
    private float articlePrice;
    private int idAuction;
    private String userMail;

    public Article() {
    }

    public Article(int articleCode, String articleName, String articleDescription, String image, float articlePrice, String userMail) {
        this.articleCode = articleCode;
        this.articleName = articleName;
        this.articleDescription = articleDescription;
        this.image = image;
        this.articlePrice = articlePrice;
        //this.idAuction = idAuction;
        this.userMail = userMail;
    }

    public int getArticleCode() {
        return articleCode;
    }
    public void setArticleCode(int articleCode) {
        this.articleCode = articleCode;
    }

    public String getArticleName() {
        return articleName;
    }
    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleDescription() {
        return articleDescription;
    }
    public void setArticleDescription(String articleDescription) {
        this.articleDescription = articleDescription;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public float getArticlePrice() {
        return articlePrice;
    }
    public void setArticlePrice(float articlePrice) {
        this.articlePrice = articlePrice;
    }

    public int getIdAuction() {
        return idAuction;
    }
    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }
    public String getUserMail() {
        return userMail;
    }
    public void setUserMail(String userMail) {
    	this.userMail = userMail;
    }

}
