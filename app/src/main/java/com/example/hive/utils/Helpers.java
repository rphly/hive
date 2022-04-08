package com.example.hive.utils;

import android.view.View;
import android.view.animation.TranslateAnimation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class Helpers {
    // define helper functions here

    // TODO: debounce

    // json -> map without using models
    public static Map<String, Object> convertJson(String json) {
        Map<String, Object> m = new Gson().fromJson(
                json, new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        return m;
    }

    public static void slideViewUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                view.getHeight(),
                0
        );
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setClickable(true);
        view.setFocusable(true);
    }

    public static void slideViewDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                view.getHeight()*2
        );
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setClickable(false);
        view.setFocusable(false);
    }

    public static CharSequence toLowerCase(CharSequence chars) {
        StringBuilder builder = new StringBuilder();
        char c;//  w ww . jav  a 2  s  .  com
        for (int i = 0; i < chars.length(); i++) {
            c = chars.charAt(i);
            if (Character.isUpperCase(c))
                c = Character.toLowerCase(c);
            builder.append(c);
        }
        return builder.toString();
    }
}
