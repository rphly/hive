package com.example.hive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.MainActivity;
import com.example.hive.R;
import com.example.hive.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private EditText EditTextFirstName, EditTextLastName, EditTextemail, EditTextpassword;
    private ProgressBar progressBar;
    private TextView registerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        EditTextFirstName = (EditText) findViewById(R.id.firstName);
        EditTextLastName = (EditText) findViewById(R.id.lastName);
        EditTextemail = (EditText) findViewById(R.id.email);
        EditTextpassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = EditTextemail.getText().toString().trim();
        String firstName = EditTextFirstName.getText().toString().trim();
        String lastName = EditTextLastName.getText().toString().trim();
        String password = EditTextpassword.getText().toString().trim();
        if(firstName.isEmpty()){
            EditTextFirstName.setError("Please enter your first name");
            EditTextFirstName.requestFocus();
            return;
        }
        if(lastName.isEmpty()){
            EditTextLastName.setError("Please enter your last name");
            EditTextLastName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            EditTextemail.setError("Please enter your email");
            EditTextemail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EditTextemail.setError("Please provide vaild email");
            EditTextemail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            EditTextpassword.setError("Please provide a password");
            EditTextpassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            EditTextpassword.setError("Please provide a password longer than 6 characters");
            EditTextpassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            task.getException().printStackTrace();
                            Toast.makeText(RegisterUser.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(id, firstName, lastName, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this,"User has been registered successfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
    }
}