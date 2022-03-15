package com.example.hive.models;

public class Desk {
    private String id;
    private int location;
    private String userId;
    private String lightToken;
    private String label;
    private float x;
    private float y;

    public Desk() {};

    // TODO: get desk from firebase by id
    public Desk(String id) {

    };
    public boolean getAvailability() {return true;}
    public User getCurrentUser() {return new User(this.userId);}
    public Location getLocation() {return new Location(this.x, this.y);}
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

    public String getLocationString() {
        return String.format("%f, %f", this.x, this.y);
    }

    public void printLocation() {
        System.out.println(getLocationString());
    }

}