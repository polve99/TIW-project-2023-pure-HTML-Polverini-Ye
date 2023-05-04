package dao;


import beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.time.LocalDate;

public class UserDAO {

    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean findUser(String mail) throws SQLException{
        boolean found = false;
        String query = "SELECT mail FROM users WHERE mail = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1 , mail);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) found = true;
        }catch(SQLException e) {
            throw new SQLException(e);
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return found;
    }

    public User checkAuthentication(String mail, String password) throws SQLException{
        //User user = null;
        String query = "SELECT * FROM user WHERE mail = ? AND password = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1 , mail);
            pStatement.setString(2 , password);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                return new User(resultSet.getString("Mail"), resultSet.getString("Password"),
                        resultSet.getString("Name"), resultSet.getString("Surname"),
                        resultSet.getDate("Birth_date"), resultSet.getDate("Subscription_date"),
                        resultSet.getString("Telephone"), resultSet.getString("Address"));
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return null;
    }

    public boolean addUser(String mail, String password, String name, String surname, Date birth_date, String telephone, String address) throws SQLException{
        int addedRows = 0;
      
        if(findUser(mail))
            return false;

        String query = "INSERT into user (mail,Password,name,surname,birth_date,subscription_date,telephone,address) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1 , mail);
            pStatement.setString(2 , password);
            pStatement.setString(3 , name);
            pStatement.setString(4 , surname);
            pStatement.setDate(5 , (java.sql.Date) birth_date);
            pStatement.setDate(6 , /*java.sql.Date.valueOf(LocalDate.now()*/(java.sql.Date) new Date(System.currentTimeMillis())); //TODO RICORDARE DI VERIFICARE SE FUNZIONE
            pStatement.setString(7 , telephone);
            pStatement.setString(8 , address);

            addedRows = pStatement.executeUpdate();//code is the number of updated row in the DB
        }catch(SQLException e) {
            throw new SQLException(e);
        }finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
        }
        return (addedRows > 0);
    }

}