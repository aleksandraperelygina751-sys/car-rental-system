package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Client;
import com.example.carrentalsystem.model.Contract;
import com.example.carrentalsystem.model.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.*, cl.full_name, cl.phone, cl.address, d.name as discount_name, d.percent_size " +
                "FROM contracts c " +
                "JOIN clients cl ON c.id_client = cl.id_client " +
                "LEFT JOIN discounts d ON c.id_discount = d.id_discount";

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

                Discount discount = null;
                if (rs.getObject("id_discount") != null) {
                    discount = new Discount(
                            rs.getInt("id_discount"),
                            rs.getString("discount_name"),
                            rs.getBigDecimal("percent_size"),
                            0
                    );
                }

                Contract contract = new Contract(
                        rs.getInt("id_contract"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("return_date").toLocalDate(),
                        rs.getBigDecimal("total_amount"),
                        client
                );
                contract.setDiscount(discount);
                contracts.add(contract);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public List<Contract> getContractsByClientId(int clientId) {
        List<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.*, cl.full_name, cl.phone, cl.address, d.name as discount_name, d.percent_size " +
                "FROM contracts c " +
                "JOIN clients cl ON c.id_client = cl.id_client " +
                "LEFT JOIN discounts d ON c.id_discount = d.id_discount " +
                "WHERE c.id_client = ?";

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

                Discount discount = null;
                if (rs.getObject("id_discount") != null) {
                    discount = new Discount(
                            rs.getInt("id_discount"),
                            rs.getString("discount_name"),
                            rs.getBigDecimal("percent_size"),
                            0
                    );
                }

                Contract contract = new Contract(
                        rs.getInt("id_contract"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getDate("return_date").toLocalDate(),
                        rs.getBigDecimal("total_amount"),
                        client
                );
                contract.setDiscount(discount);
                contracts.add(contract);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public boolean addContract(Contract contract) {
        String query = "INSERT INTO contracts (issue_date, return_date, total_amount, id_client, id_discount) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(contract.getIssueDate()));
            stmt.setDate(2, Date.valueOf(contract.getReturnDate()));
            stmt.setBigDecimal(3, contract.getTotalAmount());
            stmt.setInt(4, contract.getClient().getId());

            if (contract.getDiscount() != null) {
                stmt.setInt(5, contract.getDiscount().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

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