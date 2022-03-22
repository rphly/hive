package com.example.hive.services;

public interface Response<T> {
    void onSuccess(T data);
    void onFailure();
}
