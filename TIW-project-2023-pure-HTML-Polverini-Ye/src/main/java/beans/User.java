package beans;

import java.util.Date;

public class User {

    private String userMail;
    private String password;
    private String name;
    private String surname;
    private Date subscription_date;
    private String telephone;
    private String address;

    public User() {
    }

    public User(String userMail, String password, String name, String surname, Date subscription_date, String telephone, String address) {
        this.userMail = userMail;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.subscription_date = subscription_date;
        this.telephone = telephone;
        this.address = address;
    }

    public String getUserMail() {
        return userMail;
    }
    public void setUserMail(String userMail) {
        this.userMail = userMail;
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
