package com.example.hive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private void onNavigateToMapActivity() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set to_qr button
        Button btnMap = findViewById(R.id.go_to_map);
        btnMap.setText("See Map");
        btnMap.setOnClickListener((View v) -> onNavigateToMapActivity());
    }
}