package com.example.hive.services;

import static com.example.hive.Constants.LIFX_BASE_URL;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Lifx {
    /* Interact with LIFX smart LED bulbs */
    private OkHttpClient client = null;
    private String mainLightId = null;
    Gson g = new Gson();

    public Lifx(String apiKey) throws Exception {
        if (apiKey == null) {
            throw new Exception("API_KEY cannot be null");
        }

        // create Request instance to intercept and attach api key
        this.client = new OkHttpClient.Builder().addInterceptor(new AuthorizationInterceptor(apiKey)).build();
        // get main light
        this.getMainLight();
    }

    private void getMainLight() throws IOException, JSONException {
        String url = String.format(LIFX_BASE_URL, "all");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONArray jsonArray = new JSONArray(response.body().toString());

                if (jsonArray.length() > 0) {
                    JSONObject j = (JSONObject) jsonArray.get(0);
                    this.mainLightId = (String) j.get("id");
                }
            }
        }
    }

    public void toggleLight() {}
    public void increaseBrightness() {}
    public void decreaseBrightness() {}
    public void toggleWarmOrColdLighting() {}
}

class AuthorizationInterceptor implements Interceptor {
    private String apiKey;
    public AuthorizationInterceptor(String apiKey) {
        this.apiKey = apiKey;
    };
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.header("Authorization") == null) {
            originalRequest.newBuilder()
                .header("Authorization", apiKey)
                .build();
        }
        return chain.proceed(originalRequest);
    };
}