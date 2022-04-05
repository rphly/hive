package com.example.hive.activities.QR;

import static com.example.hive.services.DeskService.getDeskAuthFromQR;
import static com.example.hive.services.DeskService.signInToDesk;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hive.MainActivity;
import com.example.hive.R;
import com.example.hive.services.Response;
import com.example.hive.utils.AuthenticatedActivity;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QRCodeScanner extends AuthenticatedActivity {
    private PreviewView previewView;
    private Button checkInBtn;
    private String qrStr;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    // define enums
    private static final int CAMERA_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner1);

        // set invisibility of check in btn
        checkInBtn = findViewById(R.id.checkInBtn);
        checkInBtn.setVisibility(View.INVISIBLE);
        checkInBtn.setOnClickListener((View v) -> {
            Log.i(MainActivity.class.getSimpleName(), "QR Code Found: " + qrStr);
            getDeskAuthFromQR(qrStr, new Response() {
                @Override
                public void onSuccess(Object obj) {
                    Map data = (Map) obj;
                    String apiKey = String.valueOf(data.get("apiKey"));
                    String deskId = String.valueOf(data.get("desk"));
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    signInToDesk(deskId, userId, new Response() {
                        @Override
                        public void onSuccess(Object data) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(QRCodeScanner.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("LIFX_API_KEY", apiKey); // unsafe, just for testing
                            editor.apply();

                            startActivity(new Intent(QRCodeScanner.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(getApplicationContext(), "Failed to check in to desk", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure() {

                }
            });
        });


        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        previewView = findViewById(R.id.cameraPreview);

        requestCameraPerms();
    }

    private void requestCameraPerms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // if permission has been granted
            startCamera();
        } else {
            // request for permission
            ActivityCompat.requestPermissions(QRCodeScanner.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider provider) {
        // create Camera preview
        Preview preview = new Preview.Builder().build();

        // set surface
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // conduct QR image analysis
        ImageAnalysis imgAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // keep only the latest image from stream
                .build();

        imgAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String qrCode) {
                qrStr = qrCode;
                checkInBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void qrCodeNotFound() {
                checkInBtn.setVisibility(View.INVISIBLE);
            }
        }));

        // bind camera preview to provider
        provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imgAnalysis);

    }

}
