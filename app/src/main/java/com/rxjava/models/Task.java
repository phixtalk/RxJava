package com.rxjava.models;

public class Task {

    private String description;
    private boolean isComplete;
    private int priority;

    public Task(String description, boolean isComplete, int priority) {
        this.description = description;
        this.isComplete = isComplete;
        this.priority = priority;
    }

    // getter and setters ...


    public String getDescription() {
        return description;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public int getPriority() {
        return priority;
    }
}