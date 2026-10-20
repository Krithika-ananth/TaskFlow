package com.dayflow.model;

public class Subtask {
    private long subtaskId;
    private long taskId;
    private String title;
    private String status;

    public long getSubtaskId() { return subtaskId; }
    public void setSubtaskId(long subtaskId) { this.subtaskId = subtaskId; }
    public long getTaskId() { return taskId; }
    public void setTaskId(long taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
