package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private void onNavigateToQRScanner() {
        startActivity(new Intent(this, QRCodeScanner.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set to_qr button
        Button btn = (Button)findViewById(R.id.go_to_qr);
        btn.setText("Scan table");
        btn.setOnClickListener((View v) -> {
                onNavigateToQRScanner();
        });
    }
}