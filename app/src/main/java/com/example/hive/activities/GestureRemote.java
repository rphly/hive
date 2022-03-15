package com.example.hive.activities;

import android.os.Bundle;
import android.view.MotionEvent;

import com.example.hive.BuildConfig;
import com.example.hive.R;
import com.example.hive.services.Lifx;
import com.example.hive.utils.AuthenticatedActivity;
import com.example.hive.utils.Constants;
import com.example.hive.utils.Debouncer;
import com.github.nisrulz.sensey.FlipDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.github.nisrulz.sensey.TouchTypeDetector;

import java.util.concurrent.TimeUnit;

public class GestureRemote extends AuthenticatedActivity {
    private int IS_SHAKING = 0;
    final Debouncer debouncer;
    final Lifx LifxService;
    private Constants.Temperature currentTemp = Constants.Temperature.WARM;

    public GestureRemote() throws Exception {
         debouncer = new Debouncer();
         LifxService = Lifx.getInstance(BuildConfig.LIFX_API_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup gesture listeners
        setupGestureListeners();
        setContentView(R.layout.activity_gesture_remote);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stop();
    }

    private void setupGestureListeners() {
        // init global instance
        Sensey.getInstance().init(this);

        // flip detector
        FlipDetector.FlipListener flipListener = new FlipDetector.FlipListener() {
            @Override public void onFaceUp() {
                // Device Facing up
                System.out.println("Quiet mode off");
            }

            @Override public void onFaceDown() {
                // Device Facing down
                System.out.println("Quiet mode on");
            }
        };

        // shake detector
        ShakeDetector.ShakeListener shakeListener =new ShakeDetector.ShakeListener() {
            @Override public void onShakeDetected() {
                // Shake detected, do something
                if (IS_SHAKING == 0) {
                    LifxService.toggleLight();
                    System.out.println("Toggle light");
                    IS_SHAKING = 1;
                }
            }

            @Override public void onShakeStopped() {
                if (IS_SHAKING == 1) {
                    System.out.println("Stopped shaking");
                    IS_SHAKING = 0;
                }
            }

        };

        // handle touch events
        TouchTypeDetector.TouchTypListener touchListener = new TouchTypeDetector.TouchTypListener() {
            @Override
            public void onDoubleTap() {
                // change temperature
                System.out.println("Toggle warmth");
                LifxService.setLightTempColdOrWarm(currentTemp);
                currentTemp = (currentTemp == Constants.Temperature.COLD) ? Constants.Temperature.WARM : Constants.Temperature.COLD;
            }

            @Override
            public void onScroll(int scrollDirection) {
                switch (scrollDirection) {
                    case TouchTypeDetector.SCROLL_DIR_UP:
                        // scroll up
                        debouncer.debounce(Void.class, new Runnable() {
                            @Override public void run() {
                                System.out.println("Increase brightness");
                                LifxService.increaseBrightness();

                            }
                        }, 500, TimeUnit.MILLISECONDS);

                        break;
                    case TouchTypeDetector.SCROLL_DIR_DOWN:
                        // scroll down
                        debouncer.debounce(Void.class, new Runnable() {
                            @Override public void run() {
                                System.out.println("Decrease brightness");
                                LifxService.decreaseBrightness();

                            }
                        }, 500, TimeUnit.MILLISECONDS);
                        break;
                    default:
                        break;
                }
            }

            @Override public void onTwoFingerSingleTap() {
                // Two fingers single tap
            }

            @Override public void onThreeFingerSingleTap() {
                // Three fingers single tap
            }

            @Override public void onSingleTap() {
                // Single tap
            }

            @Override public void onSwipe(int swipeDirection) {

            }

            @Override public void onLongPress() {
                // Long press
            }
        };

        Sensey.getInstance().startFlipDetection(flipListener);
        Sensey.getInstance().startShakeDetection(3.0F, 1000L,shakeListener);
        Sensey.getInstance().startTouchTypeDetection(this, touchListener);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}