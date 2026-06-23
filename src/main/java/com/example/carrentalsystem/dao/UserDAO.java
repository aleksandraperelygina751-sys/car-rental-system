package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Role;
import com.example.carrentalsystem.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.*, r.name as role_name, r.description as role_description " +
                "FROM users u JOIN roles r ON u.id_role = r.id_role";

        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Role role = new Role(
                        rs.getInt("id_role"),
                        rs.getString("role_name"),
                        rs.getString("role_description")
                );

                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        rs.getString("email"),
                        role
                );
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserByLogin(String login) {
        String query = "SELECT u.*, r.name as role_name, r.description as role_description " +
                "FROM users u JOIN roles r ON u.id_role = r.id_role " +
                "WHERE u.login = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Role role = new Role(
                        rs.getInt("id_role"),
                        rs.getString("role_name"),
                        rs.getString("role_description")
                );

                return new User(
                        rs.getInt("id_user"),
                        rs.getString("login"),
                        rs.getString("password_hash"),
                        rs.getString("email"),
                        role
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}