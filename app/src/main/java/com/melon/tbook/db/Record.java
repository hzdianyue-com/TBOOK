package com.melon.tbook.db;

import java.util.Date;

public class Record {
    private int id;
    private String type;
    private double amount;
    private String category;
    private String remark;
    private Date date;
    private String account; // 新增子账户字段

    public Record() {
    }

    public Record(String type, double amount, String category, String remark, Date date, String account) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.remark = remark;
        this.date = date;
        this.account = account;
    }

    public Record(int id, String type, double amount, String category, String remark, Date date, String account) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.remark = remark;
        this.date = date;
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}