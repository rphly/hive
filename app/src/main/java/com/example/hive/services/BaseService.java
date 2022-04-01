package com.example.hive.services;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;

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

    public static void writeToFirebase(DatabaseReference collection, Object data, Response handler) throws Exception {
        if (data == null) {
            throw new Exception("ABORT: attempted to set null data");
        }
        collection.setValue(data).addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                System.out.println(String.format("Failed to set value: %s <- %s", collection.getKey(), data));
                handler.onFailure();
            }

            handler.onSuccess(true);
        });
    }

    public static void updateFirebase(DatabaseReference collection, Map data, Response handler) throws Exception {
        if (data == null) {
            throw new Exception("ABORT: attempted to set null data");
        }

        collection.updateChildren(data).addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                System.out.println(String.format("Failed to update value: %s <- %s", collection.getKey(), data));
                handler.onFailure();
            }

            handler.onSuccess(true);
        });
    }

    public static void deleteFromFirebase(DatabaseReference collection, Response handler) throws Exception {
        collection.setValue(null).addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                System.out.println(String.format("Failed to delete value: %s", collection.getKey()));
                handler.onFailure();
            }

            handler.onSuccess(true);
        });
    }
}