package com.example.hive.models;

public class Light {
    private String id;
    private boolean isOn;
    private double brightness;
    private int kelvin;

    public Light(String id, boolean isOn, double brightness, int kelvin) {
        this.brightness = brightness;
        this.id = id;
        this.isOn = isOn;
        this.kelvin = kelvin;
    };

    public boolean getIsOn() {
        return isOn;
    }

    public double getBrightness() {
        return brightness;
    }

    public int getKelvin() {
        return kelvin;
    }

    public String getId() {
        return id;
    }

}
