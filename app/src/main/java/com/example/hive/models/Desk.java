package com.example.hive.models;

public class Desk {
    private String id;
    private String currentUserId;
    private String lightToken;
    private String label;
    private Location location;

    public Desk(String id, String currentUserId, String lightToken, String label, float x, float y) {
        this.id = id;
        this.currentUserId = currentUserId;
        this.lightToken = lightToken;
        this.label = label;
        this.location = new Location(x, y);
    };

    public boolean isAvailable() {
        return currentUserId == null;
    }
    public User getCurrentUser() {

        return new User(this.currentUserId);
    }
    public Location getLocation() {return location;}
    public String getLabel() {return this.label;}
    public boolean signIn() {return true;}
    public boolean signOut() {return true;}
    public String getLightToken() {return this.lightToken;}
}

class Location {
    private float x;
    private float y;

    public Location() {};

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    };

    @Override
    public String toString() {
        return String.format("%f, %f", this.x, this.y);
    }

    public void printLocation() {
        System.out.println(this.toString());
    }
}