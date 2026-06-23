package com.example.carrentalsystem.model;

import java.math.BigDecimal;

public class Discount {
    private int id;
    private String name;
    private BigDecimal percentSize;
    private int minRentals;

    public Discount() {}

    public Discount(int id, String name, BigDecimal percentSize, int minRentals) {
        this.id = id;
        this.name = name;
        this.percentSize = percentSize;
        this.minRentals = minRentals;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPercentSize() { return percentSize; }
    public void setPercentSize(BigDecimal percentSize) { this.percentSize = percentSize; }

    public int getMinRentals() { return minRentals; }
    public void setMinRentals(int minRentals) { this.minRentals = minRentals; }

    @Override
    public String toString() {
        return name + " (" + percentSize + "%)";
    }
}