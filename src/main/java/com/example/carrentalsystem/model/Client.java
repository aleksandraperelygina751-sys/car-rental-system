package com.example.carrentalsystem.model;

public class Client {
    private int id;
    private String fullName;
    private String phone;
    private String address;
    private Discount discount;
    private int idUser;

    public Client() {}

    public Client(int id, String fullName, String phone, String address, Discount discount) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.discount = discount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Discount getDiscount() { return discount; }
    public void setDiscount(Discount discount) { this.discount = discount; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    @Override
    public String toString() {
        return fullName;
    }
}