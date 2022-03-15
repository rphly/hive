package com.example.hive.utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.activities.SignIn;
import com.google.firebase.auth.FirebaseAuth;

public class AuthenticatedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIfSignedIn()) {
            System.out.println("Not signed in");
            startActivity(new Intent(AuthenticatedActivity.this, SignIn.class));
            finish();
        }
    }

    private boolean checkIfSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}