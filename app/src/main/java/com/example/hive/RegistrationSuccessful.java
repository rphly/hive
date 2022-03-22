package com.example.hive.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.hive.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrationSuccessful extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_successful);

        Button signInPageBtn = (Button)findViewById(R.id.go_to_sign_in);
        signInPageBtn.setText("Return to sign in page");
        signInPageBtn.setOnClickListener((View v) -> onNavigateToSignInActivity());
    }

    private void onNavigateToSignInActivity() {
        startActivity(new Intent(this, SignIn.class));
    }
}