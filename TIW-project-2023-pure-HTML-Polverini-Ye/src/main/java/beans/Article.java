package beans;

public class Article {

    private int articleCode;
    private String articleName;
    private String articleDescription;
    private String image; //image contiene il percorso di file dell'immagine associata all'articolo.
    // Quando si crea o si modifica un articolo, è possibile caricare un'immagine dal file system e salvare il percorso del file come valore dell'attributo "image".
    private float articlePrice;
    private int idAuction;

    public Article() {
    }

    public Article(int articleCode, String articleName, String articleDescription, String image, float articlePrice, int idAuction) {
        this.articleCode = articleCode;
        this.articleName = articleName;
        this.articleDescription = articleDescription;
        this.image = image;
        this.articlePrice = articlePrice;
        this.idAuction = idAuction;
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

}
