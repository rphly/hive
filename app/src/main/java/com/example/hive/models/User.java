package com.example.hive.models;

import com.example.hive.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    private String firstName, lastName, email, id, profilePic;
    private Constants.Status status;
    public User(String id, String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.status = Constants.Status.AVAILABLE;
    }

    public User(String id, String firstName, String lastName, String email, String profilePic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.status = Constants.Status.AVAILABLE;
        this.profilePic = profilePic;
    }

    public Constants.Status getStatus() {return this.status;}
    public String getEmail() {return this.email;}
    public String getFirstName() {return this.firstName;}
    public String getLastName() {return this.lastName;}
    public String getFullName() {return this.firstName + " " + this.lastName;}
    public String getId() {return this.id;}
    public String getProfilePicUri() {
        return this.profilePic;
    }
    public void getCurrentDesk() {
        // get id from firebase
    }

    public static User fromObject(Object obj) {
        Map data = (Map) obj;
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("email");
        String id = String.valueOf(data.get("id"));
        String status = String.valueOf(data.get("status"));
        String profilePic = (String) data.get("profilePic");

        User user = new User(id, firstName, lastName, email, profilePic);
        return user;
    }

    public static ArrayList<User> fromObjects(Object obj) {
        Map raw = (Map) obj;
        List<Object> list = new ArrayList<Object>(raw.values());
        ArrayList data = new ArrayList<User>();
        for (Object o: list) {
            if (o != null) {
                data.add(User.fromObject(o));
            }
        }
        return data;
    }
}
