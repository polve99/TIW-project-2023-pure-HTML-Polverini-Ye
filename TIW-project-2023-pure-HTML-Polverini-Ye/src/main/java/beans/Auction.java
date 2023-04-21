package beans;

public class Auction {
	private int idAuction;
	private float initialPrice;
	private float minRise;
	private String expirationDateTime;
	
	
	
	public Auction(int idAuction, float initialPrice, float minRise, String expirationDateTime) {
		super();
		this.idAuction = idAuction;
		this.initialPrice = initialPrice;
		this.minRise = minRise;
		this.expirationDateTime = expirationDateTime;
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
}
