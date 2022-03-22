package com.example.hive.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hive.R;
import com.example.hive.models.Desk;
import com.example.hive.models.User;
import com.example.hive.services.DeskService;
import com.example.hive.services.Response;
import com.example.hive.services.UserService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment {
    private boolean isLoadingDesks;
    private boolean isLoadingUser;
    private FirebaseAuth firebase;
    private User currentUser;
    private Desk currentDesk;

    public Dashboard() {
        isLoadingDesks = true;
        isLoadingUser = true;
        firebase = FirebaseAuth.getInstance();
    }

    public static Dashboard newInstance() {
        Dashboard fragment = new Dashboard();
        return fragment;
    }

    public void getUserById(String currentUserId) {
        UserService.getUserById(currentUserId, new Response() {
            @Override
            public void onSuccess(Object data) {
                currentUser = (User.fromObject(data));
                isLoadingDesks = false;
            }

            @Override
            public void onFailure() {
                System.out.println("Failed to get user");
                isLoadingDesks = false;
            }
        });
    }

    public void getDeskByUser(String currentUserId) {
        DeskService.getAllDesks(new Response() {
            @Override
            public void onSuccess(Object data) {
                ArrayList<Desk> desks = Desk.fromObjects(data);
                desks.removeIf(d -> d.getCurrentUserId() != currentUserId);
                if (desks.size() > 0) {
                    currentDesk = desks.get(0);
                }

                isLoadingDesks = false;
            }

            @Override
            public void onFailure() {
                System.out.println("Failwhale");
                isLoadingDesks = false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String currentUserId = firebase.getCurrentUser().getUid();

        // get current user
        getUserById(currentUserId);

        // check if user has a desk
        getDeskByUser(currentUserId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
}