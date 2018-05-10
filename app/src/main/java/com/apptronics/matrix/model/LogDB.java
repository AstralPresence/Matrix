package com.apptronics.matrix.model;

/**
 * Created by Maha Perriyava on 5/5/2018.
 */

public class LogDB {
    public String time;
    public String description;

    public LogDB() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public LogDB(String description, String time) {
        this.description = description;
        this.time = time;
    }



}