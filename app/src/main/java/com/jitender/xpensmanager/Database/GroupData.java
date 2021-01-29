package com.jitender.xpensmanager.Database;

public class GroupData {
    private int id;
    private String title;
    private int noOfPersons;
    private double maxLimit;
    private double netAmount;
    private double totalAmount;
    private double groupTotal;

    public GroupData() {
    }

    public GroupData(int id, String title, int noOfPersons, int maxLimit, double netAmount, double totalAmount) {

        this.id = id;
        this.title = title;
        this.noOfPersons = noOfPersons;
        this.maxLimit = maxLimit;
        this.netAmount = netAmount;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfPersons(int noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public double getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(double maxLimit) {
        this.maxLimit = maxLimit;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(double groupTotal) {
        this.groupTotal = groupTotal;
    }
}
