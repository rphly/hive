package com.example.hive.services;

import java.util.Map;

public interface Response {
    void onSuccess(Map data);
    void onFailure();
}
