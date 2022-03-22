package com.example.hive.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeskService extends BaseService {
    final static DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("Desks");

    public static void getDeskById(String id, Response handler) {
        getFromFirebase(collection.child(id), handler);
    }

    public static void getAllDesks(Response handler) {
        getFromFirebase(collection, handler);
    }
}
