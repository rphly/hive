package com.example.hive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private void onNavigateToMapActivity() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set to_qr button
        Button btnMap = (Button)findViewById(R.id.go_to_map);
        btnMap.setText("See Map");
        btnMap.setOnClickListener((View v) -> {
            onNavigateToMapActivity();
        });
    }
}