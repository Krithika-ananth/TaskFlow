package com.dayflow.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductivityStats {
    private int total;
    private int completed;
    private int pending;
    private int today;
    private int upcoming;
    private int overdue;
    private int important;
    private int completionRate;
    private int currentStreak;
    private int longestStreak;
    private boolean hasMonthlyHistory;
    private Map<String, Integer> categories = new LinkedHashMap<>();
    private Map<String, Integer> priorities = new LinkedHashMap<>();
    private List<ProductivityBucket> weekly = List.of();
    private List<ProductivityBucket> monthly = List.of();

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getCompleted() { return completed; }
    public void setCompleted(int completed) { this.completed = completed; }
    public int getPending() { return pending; }
    public void setPending(int pending) { this.pending = pending; }
    public int getToday() { return today; }
    public void setToday(int today) { this.today = today; }
    public int getUpcoming() { return upcoming; }
    public void setUpcoming(int upcoming) { this.upcoming = upcoming; }
    public int getOverdue() { return overdue; }
    public void setOverdue(int overdue) { this.overdue = overdue; }
    public int getImportant() { return important; }
    public void setImportant(int important) { this.important = important; }
    public int getCompletionRate() { return completionRate; }
    public void setCompletionRate(int completionRate) { this.completionRate = completionRate; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public boolean isHasMonthlyHistory() { return hasMonthlyHistory; }
    public void setHasMonthlyHistory(boolean hasMonthlyHistory) { this.hasMonthlyHistory = hasMonthlyHistory; }
    public Map<String, Integer> getCategories() { return categories; }
    public void setCategories(Map<String, Integer> categories) { this.categories = categories; }
    public Map<String, Integer> getPriorities() { return priorities; }
    public void setPriorities(Map<String, Integer> priorities) { this.priorities = priorities; }
    public List<ProductivityBucket> getWeekly() { return weekly; }
    public void setWeekly(List<ProductivityBucket> weekly) { this.weekly = weekly; }
    public List<ProductivityBucket> getMonthly() { return monthly; }
    public void setMonthly(List<ProductivityBucket> monthly) { this.monthly = monthly; }
}
