package beans;

import java.util.Date;

public class User {
	private String mail;
	private String password;
	private String name;
	private String surname;
	private Date birth_date;
	private Date subscription_date;
	private String telephone;
	private String address;
	
	public User(String mail, String password, String name, String surname, Date birth_date, Date subscription_date, String telephone, String address) {
		this.mail = mail;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.birth_date = birth_date;
		this.subscription_date = subscription_date;
		this.telephone = telephone;
		this.address = address;
	}
	    
    public String getMail() {
        return mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSurname() {
        return surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public Date getBirth_date() {
        return birth_date;
    }
    
    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }
    
    public Date getSubscription_date() {
        return subscription_date;
    }
    
    public void setSubscription_date(Date subscription_date) {
        this.subscription_date = subscription_date;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }  
}