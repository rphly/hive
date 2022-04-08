package com.example.hive.services;

import com.example.hive.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserService extends BaseService {
    final static DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("Users");

    public static void getUserById(String id, Response handler) {
        getFromFirebase(collection.child(id), handler);
    }
    public static void getAllUsers(Response handler) {
        getFromFirebase(collection, handler);
    }

    public static void setUserStatus(String id, Constants.Status status, Response handler) {
        HashMap toUpdate = new HashMap();
        toUpdate.put("status", status);
        try {
            updateFirebase(collection.child(id), toUpdate, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
