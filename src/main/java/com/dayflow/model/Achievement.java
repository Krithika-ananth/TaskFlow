package com.dayflow.model;

public class Achievement {
    private long achievementId;
    private String name;
    private String description;
    private int requiredValue;
    private boolean unlocked;
    private int progress;
    private int progressPercent;

    public long getAchievementId() { return achievementId; }
    public void setAchievementId(long achievementId) { this.achievementId = achievementId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getRequiredValue() { return requiredValue; }
    public void setRequiredValue(int requiredValue) { this.requiredValue = requiredValue; }
    public boolean getUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
}
