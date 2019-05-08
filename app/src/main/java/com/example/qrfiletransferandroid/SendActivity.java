package com.example.qrfiletransferandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class SendActivity extends AppCompatActivity {

    private TextView filename;
    private SurfaceView displayView;
    private ProgressBar progressBar;
    private TextView status;
    private Button startButton;
    private Button pauseButton;
    private Button cancelButton;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // bind variable with object
        filename = (TextView) findViewById(R.id.filename_textView);
        displayView = (SurfaceView) findViewById(R.id.surfaceView);
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.status_textView);
        startButton  = (Button) findViewById(R.id.startButton);
        pauseButton  = (Button) findViewById(R.id.pauseButton);
        cancelButton = (Button) findViewById(R.id.startButton);

        // receive intent
        Intent intent = getIntent();

        // calculate things
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(150, 150).build();

        displayView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size() != 0) {
                    displayView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            filename.setText(qrCodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        // set initial text
        filename.setText("filename: " + intent.getStringExtra("filename"));
        status.setText("0%");

        // set button onClick
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do another thing
            }
        });

    }

    private Bitmap test() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
