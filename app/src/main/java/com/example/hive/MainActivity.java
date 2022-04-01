package com.example.hive;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.hive.fragments.Home;
import com.example.hive.fragments.Me;
import com.example.hive.utils.AuthenticatedActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AuthenticatedActivity  {
    @SuppressLint("SetTextI18n")

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        Home home = new Home();
        Me me= new Me();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();

        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                                return true;
                            case R.id.profile:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, me).commit();
                                return true;
                            default:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                                return true;
                        }

                    }
                }
        );
    }

}