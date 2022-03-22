package com.example.hive.services;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

public class BaseService {

    /**
     * Helper function to get data from Firebase. Useful to standardize logging on all DB queries
     *
     * @param collection Firebase DB ref
     * @param handler callback handler containing onSuccess and onFailure
     * */
    public static void getFromFirebase(DatabaseReference collection, Response handler) {
        collection.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                task.getException().printStackTrace();
                handler.onFailure();
            }
            else {
                handler.onSuccess(task.getResult().getValue());
            }
        });
    };
}