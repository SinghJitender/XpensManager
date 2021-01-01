package com.example.xpensmanager.Database;

public class CategoryData {
    private int id;
    private String category;
    private double limit;
    private double totalCategorySpend;

    public CategoryData(int id, String category, double limit, double totalCategorySpend) {
        this.id = id;
        this.category = category;
        this.limit = limit;
        this.totalCategorySpend = totalCategorySpend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getTotalCategorySpend() {
        return totalCategorySpend;
    }

    public void setTotalCategorySpend(double totalCategorySpend) {
        this.totalCategorySpend = totalCategorySpend;
    }
}
