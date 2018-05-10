package com.apptronics.matrix.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String contact;
    public String profilePicURL;
    public String email;
    public String uid;
    public boolean selected=false;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String profilePicURL, String phone) {
        this.name = name;
        this.email = email;
        this.profilePicURL = profilePicURL;
        this.contact = phone;
    }

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

}