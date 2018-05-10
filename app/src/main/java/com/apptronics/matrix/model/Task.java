package com.apptronics.matrix.model;

/**
 * Created by Maha Perriyava on 5/4/2018.
 */

public class Task {
    public String deadline;
    public String description;

    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(String description, String deadline) {
        this.description = description;
        this.deadline = deadline;
    }

}
