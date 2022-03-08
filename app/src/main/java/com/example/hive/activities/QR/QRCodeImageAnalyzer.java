package com.example.hive.activities.QR;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QRCodeImageAnalyzer implements ImageAnalysis.Analyzer {
    private QRCodeFoundListener listener;

    public QRCodeImageAnalyzer(QRCodeFoundListener listener) {
        this.listener = listener;
    }
    @Override
    public void analyze(@NonNull ImageProxy image) {
        System.out.println(image.getImageInfo().toString());
        @SuppressLint("UnsafeOptInUsageError")
        InputImage im = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE)
                .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> result = scanner.process(im)
            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                  @Override
                  public void onSuccess(List<Barcode> barcodes) {
                      if (barcodes.size() > 0) {
                          listener.onQRCodeFound(barcodes.get(0).getRawValue());
                      } else {
                          listener.qrCodeNotFound();
                      }
                  }
              }
            )
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                    listener.qrCodeNotFound();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                @Override
                public void onComplete(@NonNull Task<List<Barcode>> task) {
                    image.close();
                }
            });
        };

    }
