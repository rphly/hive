package com.example.hive;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.hive.utils.AuthenticatedActivity;

public class MainActivity extends AuthenticatedActivity {


    @SuppressLint("SetTextI18n")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





    }

}