package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Client;
import com.example.carrentalsystem.model.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT c.*, d.name as discount_name, d.percent_size, d.min_rentals " +
                "FROM clients c LEFT JOIN discounts d ON c.id_discount = d.id_discount";

        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Discount discount = null;
                if (rs.getObject("id_discount") != null) {
                    discount = new Discount(
                            rs.getInt("id_discount"),
                            rs.getString("discount_name"),
                            rs.getBigDecimal("percent_size"),
                            rs.getInt("min_rentals")
                    );
                }

                Client client = new Client(
                        rs.getInt("id_client"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        discount
                );
                clients.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public boolean addClient(Client client) {
        String query = "INSERT INTO clients (full_name, phone, address, id_discount, id_user) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, client.getFullName());
            stmt.setString(2, client.getPhone());
            stmt.setString(3, client.getAddress());

            if (client.getDiscount() != null) {
                stmt.setInt(4, client.getDiscount().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (client.getIdUser() > 0) {
                stmt.setInt(5, client.getIdUser());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateClient(Client client) {
        String query = "UPDATE clients SET full_name = ?, phone = ?, address = ?, id_discount = ? WHERE id_client = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, client.getFullName());
            stmt.setString(2, client.getPhone());
            stmt.setString(3, client.getAddress());

            if (client.getDiscount() != null) {
                stmt.setInt(4, client.getDiscount().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, client.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteClient(int id) {
        String query = "DELETE FROM clients WHERE id_client = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateClientDiscount(int clientId) {
        String countQuery = "SELECT COUNT(*) FROM contracts WHERE id_client = ?";
        String discountQuery = "SELECT id_discount FROM discounts WHERE min_rentals <= ? ORDER BY min_rentals DESC LIMIT 1";

        try (PreparedStatement countStmt = DBConnection.getInstance().getConnection().prepareStatement(countQuery)) {
            countStmt.setInt(1, clientId);
            ResultSet countRs = countStmt.executeQuery();
            int rentals = 0;
            if (countRs.next()) {
                rentals = countRs.getInt(1);
            }

            try (PreparedStatement discountStmt = DBConnection.getInstance().getConnection().prepareStatement(discountQuery)) {
                discountStmt.setInt(1, rentals);
                ResultSet discountRs = discountStmt.executeQuery();
                if (discountRs.next()) {
                    int discountId = discountRs.getInt("id_discount");

                    String updateQuery = "UPDATE clients SET id_discount = ? WHERE id_client = ?";
                    try (PreparedStatement updateStmt = DBConnection.getInstance().getConnection().prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, discountId);
                        updateStmt.setInt(2, clientId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getClientIdByUserId(int userId) {
        String query = "SELECT id_client FROM clients WHERE id_user = ?";
        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_client");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}