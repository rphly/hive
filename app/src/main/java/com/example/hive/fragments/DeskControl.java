package com.example.hive.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hive.R;
import com.example.hive.models.Desk;
import com.example.hive.models.Light;
import com.example.hive.services.DeskService;
import com.example.hive.services.Lifx;
import com.example.hive.services.Response;
import com.example.hive.services.UserService;
import com.example.hive.utils.Constants;
import com.example.hive.utils.Debouncer;
import com.github.nisrulz.sensey.FlipDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DeskControl extends Fragment {
    boolean isShaking = false;
    Lifx LifxService;
    Light light;
    Integer prevLightState;
    TextView lightStatus, currentBrightness, currentTemp, currentlyAssignedDesk;
    LottieAnimationView lightBulbAnimation;
    ProgressBar progress;
    LinearLayout controlPlane, noDesk;
    Desk currentDesk;
    Debouncer debouncer = new Debouncer();
    boolean isLoaded = false; // only applies for first load
    String userId;

    private Handler handler;
    Runnable runnable;


    public DeskControl() { }

    public static DeskControl newInstance() {
        DeskControl fragment = new DeskControl();
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        Sensey.getInstance().stop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the light at user's current desk
        DeskService.getAllDesks(new Response() {
            @Override
            public void onSuccess(Object data) {
                ArrayList<Desk> desks = Desk.fromObjects(data);
                desks.removeIf(d -> !d.getCurrentUserId().equals(userId));
                if (desks.size() > 0) {
                    currentDesk = desks.get(0);
                    currentlyAssignedDesk.setText(String.format("You are currently assigned Desk %s", currentDesk.getLabel()));
                    try {
                        LifxService = Lifx.getInstance(currentDesk.getApiKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    noDesk.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_desk_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lightStatus = view.findViewById(R.id.control_light_status);
        currentBrightness = view.findViewById(R.id.control_current_brightness);
        currentTemp = view.findViewById(R.id.control_current_temp);
        currentlyAssignedDesk= view.findViewById(R.id.control_current_desk);
        progress = view.findViewById(R.id.progressBar);
        controlPlane = view.findViewById(R.id.control_plane);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // setup gesture listeners
        setupGestureListeners();

        // set lights
        lightBulbAnimation = view.findViewById(R.id.control_light_bulb);
        noDesk = view.findViewById(R.id.control_no_desk);
        Button increaseBrightness = view.findViewById(R.id.control_increase_brightness);
        Button decreaseBrightness = view.findViewById(R.id.control_decrease_brightness);

        increaseBrightness.setOnClickListener(l -> {
            System.out.println("Increase brightness");
            LifxService.increaseBrightness();
        });

        decreaseBrightness.setOnClickListener(l -> {
            System.out.println("decrease brightness");
            LifxService.decreaseBrightness();
        });

        Button warmTempBtn = view.findViewById(R.id.control_warm);
        Button coldTempBtn = view.findViewById(R.id.control_cold);


        warmTempBtn.setOnClickListener(l -> {
            LifxService.setLightTempColdOrWarm(Constants.Temperature.WARM);
        });

        coldTempBtn.setOnClickListener(l -> {
            LifxService.setLightTempColdOrWarm(Constants.Temperature.COLD);
        });


        // setup update handler
        handler = new Handler();
        final int delayMs = 2000;
        runnable = new Runnable() {
            public void run() {
                updateLight();
                handler.postDelayed(this, delayMs);
            }
        };
        handler.postDelayed(runnable, delayMs);
    }

    private void updateLight() {
        if (LifxService != null) {
            System.out.println("Pinging for light...");
            LifxService.getMainLight(new Response() {
                @Override
                public void onSuccess(Object data) {
                    light = (Light) data;

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            currentBrightness.setText(String.format("Current brightness is set at: %s", light.getBrightness()));
                            currentTemp.setText(String.format("Current Temperature is set at: %sK", light.getKelvin()));
                            lightStatus.setText(light.getIsOn() ? "Your light is currently on." : "Your light is currently off.");
                            if ((prevLightState == null || prevLightState == 0) && light.getIsOn()) {
                                playOnAnimation();
                                prevLightState = 1;
                            } else if ((prevLightState == null || prevLightState == 1) && !light.getIsOn()) {
                                playOffAnimation();
                                prevLightState = 0;
                            }

                            if (!isLoaded) {
                                progress.setVisibility(View.INVISIBLE);
                                controlPlane.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

                @Override
                public void onFailure() { }
            });
        }
    }

    private void playOnAnimation() {
        lightBulbAnimation.setMinFrame(1);
        lightBulbAnimation.setMaxFrame(150);
        lightBulbAnimation.playAnimation();
    }

    private void playOffAnimation() {
        lightBulbAnimation.setMinFrame(150);
        lightBulbAnimation.setMaxFrame(301);
        lightBulbAnimation.playAnimation();
    }

    private void setupGestureListeners() {

            Vibrator v = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
            // init global instance
            Sensey.getInstance().init(getContext());

            // flip detector
            FlipDetector.FlipListener flipListener = new FlipDetector.FlipListener() {
                @Override public void onFaceUp() {
                    if (LifxService == null) {
                        return;
                    }
                    // Device Facing up
                    System.out.println("Quiet mode off");
                    LifxService.breathe();
                    UserService.setUserStatus(userId, Constants.Status.AVAILABLE, new Response() {
                        @Override
                        public void onSuccess(Object data) {
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                }

                @Override public void onFaceDown() {
                    if (LifxService == null) {
                        return;
                    }
                    // Device Facing down
                    System.out.println("Quiet mode on");
                    LifxService.breathe();
                    UserService.setUserStatus(userId, Constants.Status.DO_NOT_DISTURB, new Response() {
                        @Override
                        public void onSuccess(Object data) {
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                    v.vibrate(500);
                }
            };

            // shake detector
            ShakeDetector.ShakeListener shakeListener = new ShakeDetector.ShakeListener() {
                @Override public void onShakeDetected() {
                    if (LifxService == null) {
                        return;
                    }
                    // Shake detected, do something
                    if (light != null) {
                        if (isShaking == false) {
                            LifxService.toggleLight();
                            v.vibrate(1000);

                            if (getActivity() != null) {
                                // async is a betch
                                Toast.makeText(getActivity(), "Toggling light...", Toast.LENGTH_LONG).show();
                            }
                            isShaking = true;
                        }
                    }
                }

                @Override public void onShakeStopped() {
                    if (LifxService == null) {
                        return;
                    }
                    if (isShaking == true) {
                        System.out.println("Stopped shaking");
                        isShaking = false;
                    }
                }

            };

            Sensey.getInstance().startFlipDetection(flipListener);
            Sensey.getInstance().startShakeDetection(7.0F, 1000L,shakeListener);
    }
}