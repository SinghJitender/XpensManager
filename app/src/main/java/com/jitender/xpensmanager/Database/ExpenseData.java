package com.jitender.xpensmanager.Database;

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
    private String modeOfPayment;
    private double settledAmount;
    private String settled;

    public ExpenseData(){}

    public ExpenseData(int id, String date, String dayOfWeek, String textMonth, int month, int year, int day, double amount, String description,
                       String paidBy, String category, int deleted, double splitAmount, String group, String modeOfPayment, double settledAmount, String settled) {
        this.id = id;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.textMonth = textMonth;
        this.month = month;
        this.year = year;
        this.day = day;
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
        this.category = category;
        this.deleted = deleted;
        this.splitAmount = splitAmount;
        this.group = group;
        this.modeOfPayment = modeOfPayment;
        this.settledAmount = settledAmount;
        this.settled = settled;
    }

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

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public double getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(double settledAmount) {
        this.settledAmount = settledAmount;
    }

    public String getSettled() {
        return settled;
    }

    public void setSettled(String settled) {
        this.settled = settled;
    }

    @Override
    public String toString() {
        return "ExpenseData{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", textMonth='" + textMonth + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", day=" + day +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", paidBy='" + paidBy + '\'' +
                ", category='" + category + '\'' +
                ", deleted=" + deleted +
                ", splitAmount=" + splitAmount +
                ", group='" + group + '\'' +
                ", modeOfPayment='" + modeOfPayment + '\'' +
                ", settledAmount=" + settledAmount +
                ", settled=" + settled +
                '}';
    }
}
