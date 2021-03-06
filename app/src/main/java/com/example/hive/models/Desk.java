package com.example.hive.models;

import com.example.hive.services.DeskService;
import com.example.hive.services.Response;

import java.util.ArrayList;
import java.util.Map;

public class Desk {
    private String id, currentUserId, lightToken, label, apiKey;
    private Location location;
    private int timeCheckedIn;

    public Desk(String id, String currentUserId, String lightToken, String label, float x, float y, int timeCheckedIn, String apiKey) {
        this.id = id;
        this.currentUserId = currentUserId;
        this.lightToken = lightToken;
        this.label = label;
        this.location = new Location(x, y);
        this.timeCheckedIn = timeCheckedIn;
        this.apiKey = apiKey;
    };

    public boolean isAvailable() {
        return currentUserId == null;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getCurrentUserId() {
        return this.currentUserId;
    }
    public int getTimeCheckedIn() { return this.timeCheckedIn; }
    public Location getLocation() {return location;}
    public String getLabel() {return this.label;}
    public void signIn(User user, Response handler) {
        DeskService.signInToDesk(this.id, user.getId(), handler);
    }
    public void signOut(Response handler) {
        DeskService.signOutFromDesk(this.id, handler);
    }
    public String getLightToken() {return this.lightToken;}

    public static Desk fromObject(Object obj) {
        Map data = (Map) obj;
        String lightToken = String.valueOf(data.get("light_token"));
        String label = (String) data.get("label");
        Float x = Float.valueOf(String.valueOf(data.get("location_x")));
        Float y = Float.valueOf(String.valueOf(data.get("location_y")));
        String currentUserId = (String) data.get("current_user");
        String id = String.valueOf(data.get("id"));
        int timeCheckedIn = Integer.valueOf(String.valueOf(data.get("time_checked_in")));;
        String apiKey = String.valueOf(data.get("apiKey"));
        Desk desk = new Desk(id, currentUserId, lightToken, label, x, y, timeCheckedIn, apiKey);
        return desk;
    }

    public static ArrayList<Desk> fromObjects(Object obj) {
        ArrayList res = (ArrayList) obj;
        ArrayList data = new ArrayList<Desk>();
        for (Object o: res) {
            if (o != null) {
                data.add(Desk.fromObject(o));
            }
        }
        return data;
    }
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