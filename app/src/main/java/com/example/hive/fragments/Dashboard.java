package com.example.hive.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.hive.MainActivity;
import com.example.hive.R;
import com.example.hive.activities.GestureRemote;
import com.example.hive.activities.QR.QRCodeScanner;
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
    private String currentUserId;
    private TextView welcomeMsg, currentDeskMsg;
    private Button dashboardButton, deskSignOutButton;

    public Dashboard() {
        isLoadingDesks = true;
        isLoadingUser = true;
        firebase = FirebaseAuth.getInstance();
    }

    public static Dashboard newInstance() {
        Dashboard fragment = new Dashboard();
        return fragment;
    }

    public void getUserById() {
        UserService.getUserById(currentUserId, new Response() {
            @Override
            public void onSuccess(Object data) {
                currentUser = User.fromObject(data);
                isLoadingDesks = false;

                if (welcomeMsg != null) {
                    welcomeMsg.setText(String.format("Welcome, %s", currentUser.getFirstName()));
                }
            }

            @Override
            public void onFailure() {
                System.out.println("Failed to get user");
                isLoadingDesks = false;
            }
        });
    }

    public void getDeskByUserId() {
        DeskService.getAllDesks(new Response() {
            @Override
            public void onSuccess(Object data) {
                ArrayList<Desk> desks = Desk.fromObjects(data);
                setDeskDashboardUI(desks);
                isLoadingDesks = false;
            }

            @Override
            public void onFailure() {
                System.out.println("Failwhale");
                isLoadingDesks = false;
            }
        });
    }

    public void setDeskDashboardUI(ArrayList<Desk> desks) {
        desks.removeIf(d -> !d.getCurrentUserId().equals(currentUserId));
        if (desks.size() > 0) {
            currentDesk = desks.get(0);
            if (currentDeskMsg != null) {
                currentDeskMsg.setText(String.format("You are currently assigned to Desk %s.", currentDesk.getLabel()));
            }
            dashboardButton.setText("Gesture Remote");
            dashboardButton.setOnClickListener(l -> {
                startActivity(new Intent(getActivity(), GestureRemote.class));
            });

            if (deskSignOutButton != null) {
                deskSignOutButton.setOnClickListener(l -> {
                    deskSignOutButton.setEnabled(false);
                    currentDesk.signOut(new Response() {
                        @Override
                        public void onSuccess(Object data) {
                            deskSignOutButton.setEnabled(true);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        }

                        @Override
                        public void onFailure() {
                            deskSignOutButton.setEnabled(true);
                        }
                    });
                });
                deskSignOutButton.setVisibility(View.VISIBLE);
            }
        } else {
            dashboardButton.setText("Scan a Desk QR");
            dashboardButton.setOnClickListener(l -> {
                startActivity(new Intent(getActivity(), QRCodeScanner.class));
            });
        }
        dashboardButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = firebase.getCurrentUser().getUid();

        // get current user
        getUserById();

        // check if user has a desk
        getDeskByUserId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        welcomeMsg = view.findViewById(R.id.welcome_message);
        currentDeskMsg = view.findViewById(R.id.current_desk_message);
        dashboardButton = view.findViewById(R.id.dashboardCTA);
        deskSignOutButton = view.findViewById(R.id.desk_sign_out);

        dashboardButton.setVisibility(View.INVISIBLE);
        deskSignOutButton.setVisibility(View.INVISIBLE);


    }
}