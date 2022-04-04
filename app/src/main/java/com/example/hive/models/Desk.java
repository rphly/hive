package com.example.hive.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.hive.services.DeskService;
import com.example.hive.services.Response;

import java.util.ArrayList;
import java.util.Map;

public class Desk {
    private final String id;
    private final String currentUserId;
    private final String lightToken;
    private final String label;
    private final Location location;

    public Desk(String id, String currentUserId, String lightToken, String label, float x, float y) {
        this.id = id;
        this.currentUserId = currentUserId;
        this.lightToken = lightToken;
        this.label = label;
        this.location = new Location(x, y);
    }

    public boolean isAvailable() {
        return currentUserId == null;
    }
    public String getCurrentUserId() {
        return this.currentUserId;
    }
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
        float x = Float.parseFloat(String.valueOf(data.get("location_x")));
        float y = Float.parseFloat(String.valueOf(data.get("location_y")));
        String currentUserId = (String) data.get("current_user");
        String id = String.valueOf(data.get("id"));

        return new Desk(id, currentUserId, lightToken, label, x, y);
    }

    public static ArrayList<Desk> fromObjects(Object obj) {
        ArrayList res = (ArrayList) obj;
        ArrayList<Desk> data = new ArrayList<>();
        for (Object o: res) {
            if (o != null) {
                data.add(Desk.fromObject(o));
            }
        }
        return data;
    }
}

class Location {
    private final float x;
    private final float y;

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("%f, %f", this.x, this.y);
    }

}