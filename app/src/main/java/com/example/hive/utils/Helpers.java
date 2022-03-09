package com.example.hive.utils;

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
}

