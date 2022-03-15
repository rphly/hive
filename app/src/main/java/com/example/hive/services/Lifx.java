package com.example.hive.services;

import static com.example.hive.utils.Constants.DEFAULT_LIGHT_BRIGHTNESS_INTERVAL;
import static com.example.hive.utils.Constants.DEFAULT_LIGHT_COLD_KELVIN;
import static com.example.hive.utils.Constants.DEFAULT_LIGHT_WARM_KELVIN;
import static com.example.hive.utils.Constants.LIFX_BASE_URL;
import static com.example.hive.utils.Constants.LIFX_POST_STATE_DELTA_URL;
import static com.example.hive.utils.Constants.LIFX_POST_TOGGLE_POWER_URL;
import static com.example.hive.utils.Constants.LIFX_PUT_STATE_URL;
import static com.example.hive.utils.Constants.Temperature;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Lifx {
    /* Interact with LIFX smart LED bulbs */
    private OkHttpClient client = null;
    private String mainLightId = null;

    public Lifx(String apiKey) throws Exception {
        if (apiKey == null) {
            throw new Exception("API_KEY cannot be null");
        }

        // create Request instance to intercept and attach api key
        this.client = new OkHttpClient.Builder().addInterceptor(new AuthorizationInterceptor(apiKey)).build();
        getMainLight();
    }

    private Call getMainLight(Callback callback) {
        String url = String.format(LIFX_BASE_URL, "all");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public void getMainLight() {
        getMainLight(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Main light succeeded");
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            JSONObject j = (JSONObject) jsonArray.get(0);
                            mainLightId = (String) j.get("id");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    };

    private Call toggleLight(Callback callback) {
        /* NOTE: Does not reset brightness of bulb by default */
        String url = String.format(LIFX_POST_TOGGLE_POWER_URL, "id:" + this.mainLightId);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), "{}");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public void toggleLight() {
        toggleLight(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Light successfully toggled.");
                    return;
                }
            }
        });
    }

    public void increaseBrightness() {
        addBrightnessDelta(DEFAULT_LIGHT_BRIGHTNESS_INTERVAL);
    }

    public void decreaseBrightness() {
        addBrightnessDelta(-DEFAULT_LIGHT_BRIGHTNESS_INTERVAL);
    }

    private Call addBrightnessDelta(double delta, Callback callback) {
        /*
            Brightness is clipped at [0,1]. No error is thrown if delta results in brightness beyond range.
            0 =< INITIAL_STATE + delta_1 + delta_2 + ... + delta_n <= 1
            NOTE: this operation is additive.
        */
        String url = String.format(LIFX_POST_STATE_DELTA_URL, "id:" + this.mainLightId);
        @SuppressLint("DefaultLocale")
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.format("{\"brightness\": %.1f, \"fast\": true, \"duration\": 0.4}", delta));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void addBrightnessDelta(double delta) {
        addBrightnessDelta(delta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Light brightness successfully adjusted.");
                }
            }
        });
    }

    public void setLightTempColdOrWarm(Temperature temp) {
        setLightTempColdOrWarm(temp, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.code());
                if (response.isSuccessful()) {
                    System.out.println("Light temp successfully toggled.");
                }
            }
        });
    }
    private Call setLightTempColdOrWarm(Temperature temp, Callback callback) {
        String url = String.format(LIFX_PUT_STATE_URL, "id:" + this.mainLightId);
        int color = temp == Temperature.COLD ? DEFAULT_LIGHT_COLD_KELVIN : DEFAULT_LIGHT_WARM_KELVIN;
        @SuppressLint("DefaultLocale")
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.format("{\"color\": \"kelvin:%s\"}", color));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
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
            Request authRequest = originalRequest.newBuilder()
                .header("Authorization", String.format("Bearer %s", apiKey))
                .build();
            return chain.proceed(authRequest);
        }
        return chain.proceed(originalRequest);
    };
}