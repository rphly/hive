package com.example.hive.activities;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.R;
import com.github.nisrulz.sensey.FlipDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.github.nisrulz.sensey.TouchTypeDetector;

public class GestureRemote extends AppCompatActivity {
    private int TOGGLE_LIGHT_STATE = 0;

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

        // shake listener
        ShakeDetector.ShakeListener shakeListener =new ShakeDetector.ShakeListener() {
            @Override public void onShakeDetected() {
                // Shake detected, do something
                if (TOGGLE_LIGHT_STATE == 0) {
                    System.out.println("Toggle light");
                    TOGGLE_LIGHT_STATE = 1;
                }
            }

            @Override public void onShakeStopped() {
                if (TOGGLE_LIGHT_STATE == 1) {
                    System.out.println("Stopped shaking");
                    TOGGLE_LIGHT_STATE = 0;
                }
            }

        };

        // handle touch events
        TouchTypeDetector.TouchTypListener touchListener = new TouchTypeDetector.TouchTypListener() {
            @Override
            public void onDoubleTap() {
                // change temperature
                System.out.println("Toggle warmth");
            }

            @Override
            public void onScroll(int scrollDirection) {
                switch (scrollDirection) {
                    case TouchTypeDetector.SCROLL_DIR_UP:
                        // scroll up
                        System.out.println("Increase brightness");
                        break;
                    case TouchTypeDetector.SCROLL_DIR_DOWN:
                        // scroll down
                        System.out.println("Decrease brightness");
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
        Sensey.getInstance().startShakeDetection(shakeListener);
        Sensey.getInstance().startTouchTypeDetection(this, touchListener);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}