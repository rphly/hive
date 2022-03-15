package com.example.hive.services;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class DeskService {
    public static void getDeskById(String id, Response handler) {
        final DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("Desks");
        collection.child(id).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                handler.onFailure();
            }
            else {
                Map data = (Map) task.getResult().getValue();
                String lightToken = String.valueOf(data.get("light_token"));
                String label = (String) data.get("label");
                Float x = Float.valueOf(String.valueOf(data.get("location_x")));
                Float y = Float.valueOf(String.valueOf(data.get("location_y")));
                String currentUserId = (String) data.get("current_user");
                handler.onSuccess(data);
            }
        });
    }

}
