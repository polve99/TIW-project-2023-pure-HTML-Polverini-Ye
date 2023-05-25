package dao;

import beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User createUser(String userMail, String password, String name, String surname, String telephone, String address) throws SQLException {
        int rows = 0;
        User user = null;
        String query = "INSERT INTO dbaste.users (userMail, password, name, surname, telephone, address) VALUES (?, ?, ?, ?, ?, ?)";
        //es su mySQL: INSERT INTO dbaste.users (usermail, password, name, surname, telephone, address) VALUES ('gy', 'ok', 'giu','ye',null, 'mi');
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            pStatement.setString(2, password);
            pStatement.setString(3, name);
            pStatement.setString(4, surname);
            pStatement.setString(5, telephone);
            pStatement.setString(6, address);
            /*rows = */pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
        }
        return new User(userMail, password, name, surname, telephone, address);
    }

    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE dbaste.users SET password = ?, name = ?, surname = ?, telephone = ?, address = ? WHERE mail = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, user.getPassword());
            pStatement.setString(2, user.getName());
            pStatement.setString(3, user.getSurname());
            pStatement.setString(4, user.getTelephone());
            pStatement.setString(5, user.getAddress());
            pStatement.setString(6, user.getUserMail());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return true;
    }

    public boolean deleteUser(String mail) throws SQLException {
        String query = "DELETE FROM dbaste.users WHERE mail = ?";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, mail);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return true;
    }

    public User findUserByUserMail(String userMail) throws SQLException{
        User user = null;
        String query = "SELECT * FROM dbaste.users WHERE userMail = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            resultSet = pStatement.executeQuery();

            if(resultSet.next()){
                user = new User();
                user.setUserMail(resultSet.getString("userMail"));
                user.setPassword(resultSet.getString("password"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setSubscription_date(resultSet.getDate("subscription_date"));
                user.setTelephone(resultSet.getString("telephone"));
                user.setAddress(resultSet.getString("address"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
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
        return user;
    }

    public User getUserAfterAuthentication(String userMail, String password) throws SQLException{
        User user = null;
        String query = "SELECT * FROM dbaste.users WHERE userMail = ? AND password = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            pStatement.setString(2, password);
            resultSet = pStatement.executeQuery();

            if(resultSet.next()){
                user = new User();
                user.setUserMail(resultSet.getString("userMail"));
                user.setPassword(resultSet.getString("password"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setSubscription_date(resultSet.getDate("subscription_date"));
                user.setTelephone(resultSet.getString("telephone"));
                user.setAddress(resultSet.getString("address"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
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
        return user;
    }

    public boolean isUserMailInDB(String userMail) throws SQLException{
        String query = "SELECT * FROM dbaste.users WHERE userMail = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1, userMail);
            resultSet = pStatement.executeQuery();

            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
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
        return false;
    }

}