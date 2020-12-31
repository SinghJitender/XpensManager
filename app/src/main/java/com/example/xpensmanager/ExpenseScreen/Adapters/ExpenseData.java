package com.example.xpensmanager.ExpenseScreen.Adapters;

public class ExpenseData {
    private int id;
    private String date;
    private String dayOfWeek;
    private String textMonth;
    private int month;
    private int year;
    private int day;
    private double amount;
    private String description;
    private String paidBy;
    private String category;
    private int deleted;
    private double splitAmount;
    private String group;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTextMonth() {
        return textMonth;
    }

    public void setTextMonth(String textMonth) {
        this.textMonth = textMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public double getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(double splitAmount) {
        this.splitAmount = splitAmount;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "ExpenseData{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", textMonth='" + textMonth + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", amount='" + amount + '\'' +
                ", description='" + description + '\'' +
                ", paidBy='" + paidBy + '\'' +
                ", category='" + category + '\'' +
                ", deleted='" + deleted + '\'' +
                ", splitAmount='" + splitAmount + '\'' +
                '}';
    }
}
