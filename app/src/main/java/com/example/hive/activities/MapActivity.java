package com.example.hive.activities;

import android.os.Bundle;

import com.example.hive.R;
import com.example.hive.utils.AuthenticatedActivity;

public class MapActivity extends AuthenticatedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

    }
}
