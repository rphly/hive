package com.example.hive.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeskService extends BaseService {
    final static DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("Desks");

    public static void getDeskById(String id, Response handler) {
        getFromFirebase(collection.child(id), handler);
    }

    public static void signInToDesk(String id, String currentUserId, Response handler) {
        try {
            writeToFirebase(collection.child(id).child("current_user"), currentUserId, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void signOutFromDesk(String id, Response handler) {
        try {
            // DO NOT PASS IN NULL
            writeToFirebase(collection.child(id).child("current_user"), "", handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAllDesks(Response handler) {
        getFromFirebase(collection, handler);
    }
}
