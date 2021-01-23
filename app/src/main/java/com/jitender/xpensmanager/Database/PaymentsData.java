package com.jitender.xpensmanager.Database;

public class PaymentsData {
    private int id;
    private String mode;
    private double limit;
    private double totalmodeSpend;

    public PaymentsData(int id, String mode, double limit, double totalmodeSpend) {
        this.id = id;
        this.mode = mode;
        this.limit = limit;
        this.totalmodeSpend = totalmodeSpend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getTotalmodeSpend() {
        return totalmodeSpend;
    }

    public void setTotalmodeSpend(double totalmodeSpend) {
        this.totalmodeSpend = totalmodeSpend;
    }

    @Override
    public String toString() {
        return "PaymentsData{" +
                "id=" + id +
                ", mode='" + mode + '\'' +
                ", limit=" + limit +
                ", totalmodeSpend=" + totalmodeSpend +
                '}';
    }
}
