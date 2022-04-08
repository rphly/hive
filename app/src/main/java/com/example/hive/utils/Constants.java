package com.example.hive.utils;

import android.graphics.Color;

public class Constants {
    // focus mode
    public static final int DEFAULT_FOCUS_MODE = 0;

    // lights
    public static final double DEFAULT_LIGHT_BRIGHTNESS_INTERVAL = 0.3;
    public static final int DEFAULT_LIGHT_COLD_KELVIN = 5000;
    public static final int DEFAULT_LIGHT_WARM_KELVIN = 3500;

    // LIFX urls
    public static final String LIFX_BASE_URL = "https://api.lifx.com/v1/lights/%s/";
    public static final String LIFX_PUT_STATE_URL = LIFX_BASE_URL + "state";
    public static final String LIFX_POST_TOGGLE_POWER_URL = LIFX_BASE_URL + "toggle";
    public static final String LIFX_POST_STATE_DELTA_URL = LIFX_BASE_URL + "state/delta";
    public static final String LIFX_POST_EFFECT_BREATHE = LIFX_BASE_URL + "effects/breathe";

    public static enum Temperature {
        COLD,
        WARM
    }

    public static enum Status {
        AVAILABLE,
        DO_NOT_DISTURB
    }

    public static class Colors {
        public static final int white = Color.parseColor("#FFFFFF");
        public static final int red = Color.parseColor("#D2232A");
        public static final int yellow = Color.parseColor("#EA8000");
    }
}