package ai.syntax.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ai.syntax.db.DbUtil;
import ai.syntax.model.User;

public class UserDAO {

    
    public void registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, profile_image_url) VALUES (?, ?, ?, ?)";

        
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword_hash());
            ps.setString(4, user.getProfile_image_url());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[UserDAO ERROR] Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = null;
        String sql = "SELECT id, username, email, password_hash, created_at, profile_image_url FROM users WHERE username = ? OR email = ?";

        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usernameOrEmail); 
            statement.setString(2, usernameOrEmail); 
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword_hash(resultSet.getString("password_hash")); 
                    user.setCreated_at(resultSet.getString("created_at"));      
                    user.setProfile_image_url(resultSet.getString("profile_image_url")); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username/email: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }}