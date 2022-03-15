package com.example.hive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hive.activities.GestureRemote;
import com.example.hive.activities.MapActivity;
import com.example.hive.activities.QR.QRCodeScanner;
import com.example.hive.activities.SignIn;
import com.example.hive.services.DeskService;
import com.example.hive.utils.AuthenticatedActivity;
import com.example.hive.services.Response;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;

import java.util.Map;

public class MainActivity extends AuthenticatedActivity {

    private void onNavigateToQRScanner() throws JSONException {
        startActivity(new Intent(this, QRCodeScanner.class));
    }
    private void onNavigateToGestureRemote() {
        startActivity(new Intent(this, GestureRemote.class));
    }

    private void onNavigateToMapActivity() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, SignIn.class);
        this.finishAffinity();
        this.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set to_qr button
        Button scanTableBtn = (Button) findViewById(R.id.go_to_qr);
        scanTableBtn.setText("Scan table");
        scanTableBtn.setOnClickListener((View v) -> {
            try {
                onNavigateToQRScanner();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // set to_qr button
        Button gestureRemoteBtn = (Button)findViewById(R.id.gesture_remote);
        gestureRemoteBtn.setText("Gesture Remote");
        gestureRemoteBtn.setOnClickListener((View v) -> {
            onNavigateToGestureRemote();
        });

        // map button
        Button btnMap = findViewById(R.id.go_to_map);
        btnMap.setText("See Map");
        btnMap.setOnClickListener((View v) -> onNavigateToMapActivity());

        // signOut button
        Button signOutBtn = findViewById(R.id.sign_out);
        signOutBtn.setText("Sign Out");
        signOutBtn.setOnClickListener((View v) -> signOut());

        // test button
        Button testBtn = findViewById(R.id.testBtn);
        testBtn.setText("Test");
        testBtn.setOnClickListener((View v) -> {
            DeskService.getDeskById("1", new Response() {

                @Override
                public void onSuccess(Map data) {
                    System.out.println(data.toString());
                }

                @Override
                public void onFailure() {
                    System.out.println("Failwhale");
                }
            });
        });
    }

}