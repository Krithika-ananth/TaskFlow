package com.dayflow.model;

public class Task {
    private long taskId;
    private long userId;
    private Long categoryId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private java.time.LocalDate dueDate;
    private String dueTime;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime completedAt;
    private String repeatType;
    private String categoryName;
    private String ownerName;
    private boolean sharedWithMe;
    private boolean important;

    public long getTaskId() { return taskId; }
    public void setTaskId(long taskId) { this.taskId = taskId; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.time.LocalDate getDueDate() { return dueDate; }
    public void setDueDate(java.time.LocalDate dueDate) { this.dueDate = dueDate; }
    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(java.time.LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getRepeatType() { return repeatType; }
    public void setRepeatType(String repeatType) { this.repeatType = repeatType; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public boolean isSharedWithMe() { return sharedWithMe; }
    public void setSharedWithMe(boolean sharedWithMe) { this.sharedWithMe = sharedWithMe; }
    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }
}
