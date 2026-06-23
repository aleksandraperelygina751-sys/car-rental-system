package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Client;
import com.example.carrentalsystem.model.Contract;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.*, cl.full_name, cl.phone, cl.address " +
                "FROM contracts c JOIN clients cl ON c.id_client = cl.id_client";

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
                contracts.add(contract);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public boolean addContract(Contract contract) {
        String query = "INSERT INTO contracts (issue_date, return_date, total_amount, id_client) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(contract.getIssueDate()));
            stmt.setDate(2, Date.valueOf(contract.getReturnDate()));
            stmt.setBigDecimal(3, contract.getTotalAmount());
            stmt.setInt(4, contract.getClient().getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    contract.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteContract(int id) {
        String query = "DELETE FROM contracts WHERE id_contract = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}