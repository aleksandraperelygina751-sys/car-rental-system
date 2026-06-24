package com.example.carrentalsystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/car_rental?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Подключение к БД успешно!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL драйвер не найден!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к БД!");
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}