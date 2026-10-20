package com.dayflow.model;

public class ProductivityBucket {
    private final String label;
    private final int count;
    private final int percent;

    public ProductivityBucket(String label, int count, int percent) {
        this.label = label;
        this.count = count;
        this.percent = percent;
    }

    public String getLabel() { return label; }
    public int getCount() { return count; }
    public int getPercent() { return percent; }
}
