package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Client;
import com.example.carrentalsystem.model.Contract;
import com.example.carrentalsystem.model.Fine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {

    public List<Fine> getAllFines() {
        List<Fine> fines = new ArrayList<>();
        String query = "SELECT f.*, " +
                "c.id_contract, c.issue_date, c.return_date, c.total_amount, " +
                "cl.id_client, cl.full_name, cl.phone, cl.address " +
                "FROM fines f " +
                "JOIN contracts c ON f.id_contract = c.id_contract " +
                "JOIN clients cl ON c.id_client = cl.id_client";

        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id_client"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        null
                );

                Contract contract = new Contract(
                        rs.getInt("id_contract"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("return_date").toLocalDate(),
                        rs.getBigDecimal("total_amount"),
                        client
                );

                Fine fine = new Fine(
                        rs.getInt("id_fine"),
                        rs.getString("reason"),
                        rs.getBigDecimal("amount"),
                        rs.getBoolean("paid"),
                        contract
                );
                fines.add(fine);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    public List<Fine> getFinesByClientId(int clientId) {
        List<Fine> fines = new ArrayList<>();
        String query = "SELECT f.*, " +
                "c.id_contract, c.issue_date, c.return_date, c.total_amount, " +
                "cl.id_client, cl.full_name, cl.phone, cl.address " +
                "FROM fines f " +
                "JOIN contracts c ON f.id_contract = c.id_contract " +
                "JOIN clients cl ON c.id_client = cl.id_client " +
                "WHERE cl.id_client = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id_client"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        null
                );

                Contract contract = new Contract(
                        rs.getInt("id_contract"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("return_date").toLocalDate(),
                        rs.getBigDecimal("total_amount"),
                        client
                );

                Fine fine = new Fine(
                        rs.getInt("id_fine"),
                        rs.getString("reason"),
                        rs.getBigDecimal("amount"),
                        rs.getBoolean("paid"),
                        contract
                );
                fines.add(fine);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    public boolean addFine(Fine fine) {
        String query = "INSERT INTO fines (reason, amount, paid, id_contract) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, fine.getReason());
            stmt.setBigDecimal(2, fine.getAmount());
            stmt.setBoolean(3, fine.isPaid());
            stmt.setInt(4, fine.getContract().getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFine(Fine fine) {
        String query = "UPDATE fines SET reason = ?, amount = ?, paid = ?, id_contract = ? WHERE id_fine = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, fine.getReason());
            stmt.setBigDecimal(2, fine.getAmount());
            stmt.setBoolean(3, fine.isPaid());
            stmt.setInt(4, fine.getContract().getId());
            stmt.setInt(5, fine.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFine(int id) {
        String query = "DELETE FROM fines WHERE id_fine = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}