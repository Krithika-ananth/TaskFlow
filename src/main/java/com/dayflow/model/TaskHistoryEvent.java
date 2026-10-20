package com.dayflow.model;

import java.time.LocalDateTime;

public class TaskHistoryEvent {
    private long taskId;
    private String taskTitle;
    private String eventType;
    private LocalDateTime occurredAt;
    private String dateGroup;
    private String timeLabel;

    public long getTaskId() { return taskId; }
    public void setTaskId(long taskId) { this.taskId = taskId; }
    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getDateGroup() { return dateGroup; }
    public void setDateGroup(String dateGroup) { this.dateGroup = dateGroup; }
    public String getTimeLabel() { return timeLabel; }
    public void setTimeLabel(String timeLabel) { this.timeLabel = timeLabel; }
}
