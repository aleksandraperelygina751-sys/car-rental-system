package com.example.carrentalsystem.model;

import java.math.BigDecimal;

public class Fine {
    private int id;
    private String reason;
    private BigDecimal amount;
    private boolean paid;
    private Contract contract;

    public Fine() {}

    public Fine(int id, String reason, BigDecimal amount, boolean paid, Contract contract) {
        this.id = id;
        this.reason = reason;
        this.amount = amount;
        this.paid = paid;
        this.contract = contract;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    @Override
    public String toString() {
        return reason + " (" + amount + " руб.)";
    }
}