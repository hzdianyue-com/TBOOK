package com.melon.tbook.db;

import java.util.Date;

public class Borrow {
    private int id;
    private String type;
    private double amount;
    private String borrower;
    private Date borrowDate;

    public Borrow(){

    }
    public Borrow(String type, double amount, String borrower, Date borrowDate) {
        this.type = type;
        this.amount = amount;
        this.borrower = borrower;
        this.borrowDate = borrowDate;
    }

    public Borrow(int id, String type, double amount, String borrower, Date borrowDate) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.borrower = borrower;
        this.borrowDate = borrowDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }
}