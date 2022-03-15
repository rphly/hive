package com.example.hive.models;

public class User {
    private String fullName, email;

    public User(){};

    public User(String userId){};

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    // TODO fill in these methods
    public String getUserStatus() {return "Available";}
    public String getUserFullName() {return this.fullName;}
    public void getUserCurrentDesk() {
        // get id from firebase

    }
}
