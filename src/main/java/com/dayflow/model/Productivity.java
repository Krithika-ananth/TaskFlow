package com.dayflow.model;

public class Productivity {
    private long productivityId;
    private long userId;
    private java.time.LocalDate date;
    private int completedTasks;
    private int focusMinutes;

    public long getProductivityId() { return productivityId; }
    public void setProductivityId(long productivityId) { this.productivityId = productivityId; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public java.time.LocalDate getDate() { return date; }
    public void setDate(java.time.LocalDate date) { this.date = date; }
    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    public int getFocusMinutes() { return focusMinutes; }
    public void setFocusMinutes(int focusMinutes) { this.focusMinutes = focusMinutes; }
}
