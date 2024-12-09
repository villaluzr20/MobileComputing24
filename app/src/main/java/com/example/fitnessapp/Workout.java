package com.example.fitnessapp;

public class Workout {
    private int id;
    private String date;
    private String type;
    private int duration;

    public Workout(int id, String date, String type, int duration) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
