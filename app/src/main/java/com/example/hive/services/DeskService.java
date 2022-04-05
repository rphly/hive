package com.example.hive.services;

import android.annotation.SuppressLint;

import com.example.hive.models.Desk;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.util.HashMap;

public class DeskService extends BaseService {
    final static DatabaseReference collection = FirebaseDatabase.getInstance().getReference().child("Desks");

    public static void getDeskById(String id, Response handler) {
        getFromFirebase(collection.child(id), handler);
    }

    public static void getDeskAuthFromQR(String qrStr, Response handler) {
        getFromFirebase(FirebaseDatabase.getInstance().getReference().child("DeskAuth").child(qrStr), handler);
    }

    public static void signInToDesk(String id, String currentUserId, Response handler) {
        getDeskById(id, new Response() {
            @SuppressLint("NewApi")
            @Override
            public void onSuccess(Object data) {
                /**
                 * CHECK if current desk is occupied
                 * We may sign iff:
                 * - current user id is empty
                 * - if occupied, that it has been 8 hours since the last check in (86400 seconds)
                **/
                Desk desk = Desk.fromObject(data);
                long currentTime = Instant.now().getEpochSecond();
                if (desk.getCurrentUserId().isEmpty() || currentTime > desk.getTimeCheckedIn() + 86400) {
                    try {
                        HashMap toUpdate = new HashMap();
                        toUpdate.put("current_user", currentUserId);
                        toUpdate.put("time_checked_in", currentTime);
                        updateFirebase(collection.child(id), toUpdate, handler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Hello!");
                    handler.onFailure();
                };
            }

            @Override
            public void onFailure() {

            }
        });
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
