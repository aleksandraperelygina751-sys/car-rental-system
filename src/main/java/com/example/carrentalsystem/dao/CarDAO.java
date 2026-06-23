package com.example.carrentalsystem.dao;

import com.example.carrentalsystem.model.Car;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars";

        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id_car"),
                        rs.getString("brand"),
                        rs.getInt("production_year"),
                        rs.getString("type")
                );
                cars.add(car);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public boolean addCar(Car car) {
        String query = "INSERT INTO cars (brand, production_year, type) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, car.getBrand());
            stmt.setInt(2, car.getProductionYear());
            stmt.setString(3, car.getType());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCar(Car car) {
        String query = "UPDATE cars SET brand = ?, production_year = ?, type = ? WHERE id_car = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, car.getBrand());
            stmt.setInt(2, car.getProductionYear());
            stmt.setString(3, car.getType());
            stmt.setInt(4, car.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCar(int id) {
        String query = "DELETE FROM cars WHERE id_car = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}