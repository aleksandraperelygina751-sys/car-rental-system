package com.example.carrentalsystem.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Contract {
    private int id;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private BigDecimal totalAmount;
    private Client client;

    public Contract() {}

    public Contract(int id, LocalDate issueDate, LocalDate returnDate, BigDecimal totalAmount, Client client) {
        this.id = id;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.totalAmount = totalAmount;
        this.client = client;
    }

    public Contract(LocalDate issueDate, LocalDate returnDate, BigDecimal totalAmount, Client client) {
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.totalAmount = totalAmount;
        this.client = client;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    @Override
    public String toString() {
        return "Договор #" + id + " (" + client.getFullName() + ")";
    }
}