package com.example.hive.models;

public class User {
    private String firstName, lastName, email, id;

    public User(String id, String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
    }

    // TODO fill in these methods
    public String getStatus() {return "Available";}
    public String getFullName() {return this.firstName + " " + this.lastName;}
    public String getId() {return this.id;}
    public void getCurrentDesk() {
        // get id from firebase
    }
}
