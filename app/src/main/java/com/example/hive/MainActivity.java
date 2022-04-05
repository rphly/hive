package com.example.hive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.hive.fragments.DeskControl;
import com.example.hive.fragments.Home;
import com.example.hive.fragments.Me;
import com.example.hive.utils.AuthenticatedActivity;
import com.github.nisrulz.sensey.Sensey;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AuthenticatedActivity  {
    @SuppressLint("SetTextI18n")

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Home home = new Home();
        Me me = new Me();
        DeskControl desk = new DeskControl();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();

        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                                Sensey.getInstance().stop();
                                return true;
                            case R.id.nav_profile:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, me).commit();
                                Sensey.getInstance().stop();
                                return true;
                            case R.id.nav_desk:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, desk).commit();
                                return true;
                            default:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                                Sensey.getInstance().stop();
                                return true;
                        }
                    }
                }
        );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}