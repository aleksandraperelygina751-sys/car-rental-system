package com.example.carrentalsystem.model;

public class Car {
    private int id;
    private String brand;
    private int productionYear;
    private String type;

    public Car() {}

    public Car(int id, String brand, int productionYear, String type) {
        this.id = id;
        this.brand = brand;
        this.productionYear = productionYear;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getProductionYear() { return productionYear; }
    public void setProductionYear(int productionYear) { this.productionYear = productionYear; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return brand + " (" + productionYear + ")";
    }
}