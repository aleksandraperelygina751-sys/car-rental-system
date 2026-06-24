package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    public List<Discount> getAllDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discounts";

        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Discount discount = new Discount(
                        rs.getInt("id_discount"),
                        rs.getString("name"),
                        rs.getBigDecimal("percent_size"),
                        rs.getInt("min_rentals")
                );
                discounts.add(discount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    public Discount getDiscountById(int id) {
        String query = "SELECT * FROM discounts WHERE id_discount = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Discount(
                        rs.getInt("id_discount"),
                        rs.getString("name"),
                        rs.getBigDecimal("percent_size"),
                        rs.getInt("min_rentals")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}