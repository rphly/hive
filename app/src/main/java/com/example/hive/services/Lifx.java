package com.example.hive.services;

import static com.example.hive.utils.Constants.DEFAULT_LIGHT_COLD_KELVIN;
import static com.example.hive.utils.Constants.DEFAULT_LIGHT_WARM_KELVIN;
import static com.example.hive.utils.Constants.LIFX_BASE_URL;
import static com.example.hive.utils.Constants.LIFX_POST_STATE_DELTA_URL;
import static com.example.hive.utils.Constants.LIFX_POST_TOGGLE_POWER_URL;
import static com.example.hive.utils.Constants.LIFX_PUT_STATE_URL;
import static com.example.hive.utils.Constants.Temperature;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    private void getMainLight() throws JSONException {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleLight() throws JSONException {
        /* NOTE: Does not reset brightness of bulb by default */
        String url = String.format(LIFX_POST_TOGGLE_POWER_URL, "id:" + this.mainLightId);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), "{}");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Light successfully toggled.");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void increaseBrightness() {
        addBrightnessDelta(0.1);
    }

    public void decreaseBrightness() {
        addBrightnessDelta(-0.1);
    }

    private void addBrightnessDelta(double delta) {
        /*
            Brightness is clipped at [0,1]. No error is thrown if delta results in brightness beyond range.
            0 =< INITIAL_STATE + delta_1 + delta_2 + ... + delta_n <= 1
            NOTE: this operation is additive.
        */
        String url = String.format(LIFX_POST_STATE_DELTA_URL, "id:" + this.mainLightId);
        @SuppressLint("DefaultLocale")
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.format("{\"brightness\": %.1f}", delta));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Light brightness successfully adjusted.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLightTempColdOrWarm(Temperature temp) {
        String url = String.format(LIFX_PUT_STATE_URL, "id:" + this.mainLightId);
        int color = temp == Temperature.COLD ? DEFAULT_LIGHT_COLD_KELVIN : DEFAULT_LIGHT_WARM_KELVIN;
        @SuppressLint("DefaultLocale")
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), String.format("{\"color\": kelvin:%s}", color));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Light temp successfully toggled.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            originalRequest.newBuilder()
                .header("Authorization", apiKey)
                .build();
        }
        return chain.proceed(originalRequest);
    };
}