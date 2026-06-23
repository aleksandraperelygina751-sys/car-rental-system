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
}