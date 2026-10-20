package com.dayflow.model;

/** Safe public projection used by friend search, requests and friend lists. */
public class Friend {
    private long userId;
    private long requestId;
    private String fullName;
    private String username;
    private String relationshipState;

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRelationshipState() { return relationshipState; }
    public void setRelationshipState(String relationshipState) { this.relationshipState = relationshipState; }
}
