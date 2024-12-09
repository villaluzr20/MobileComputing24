package com.example.fitnessapp;

public class CalorieEntry {
    private int id;
    private String date;
    private int caloriesConsumed;

    public CalorieEntry(int id, String date, int caloriesConsumed) {
        this.id = id;
        this.date = date;
        this.caloriesConsumed = caloriesConsumed;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getCaloriesConsumed() {
        return caloriesConsumed;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCaloriesConsumed(int caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }
}