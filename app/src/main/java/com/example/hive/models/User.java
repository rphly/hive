package com.example.hive.models;

import com.example.hive.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String id;
    private final Constants.Status status;

    public User(String id, String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.status = Constants.Status.AVAILABLE;
    }

    public Constants.Status getStatus() {return this.status;}
    public String getEmail() {return this.email;}
    public String getFirstName() {return this.firstName;}
    public String getLastName() {return this.lastName;}
    public String getFullName() {return this.firstName + " " + this.lastName;}
    public String getId() {return this.id;}
    public void getCurrentDesk() {
        // get id from firebase
    }

    public static User fromObject(Object obj) {
        Map data = (Map) obj;
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("email");
        String id = String.valueOf(data.get("id"));

        return new User(id, firstName, lastName, email);
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
