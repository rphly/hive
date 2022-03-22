package com.example.hive.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserService extends BaseService {
    final static DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("Users");

    public static void getUserById(String id, Response handler) {
        getFromFirebase(collection.child(id), handler);
    }

    public static void getAllUsers(Response handler) {
        getFromFirebase(collection, handler);
    }

}
