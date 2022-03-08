package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.activities.QRCodeScanner;
import com.example.hive.activities.GestureRemote;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private void onNavigateToQRScanner() throws JSONException {
        startActivity(new Intent(this, QRCodeScanner.class));
    }
    private void onNavigateToGestureRemote() {
        startActivity(new Intent(this, GestureRemote.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set to_qr button
        Button scanTableBtn = (Button)findViewById(R.id.go_to_qr);
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
    }

}