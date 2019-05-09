package com.example.qrfiletransferandroid;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SendActivity extends AppCompatActivity {

    private File file;
    private TextView filename;
    private SurfaceView surfaceView;
    private ProgressBar progressBar;
    private TextView status;
    private Button startButton;
    private Button pauseButton;
    private Button cancelButton;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private Bitmap bitmap;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // bind variable with object
        filename = (TextView) findViewById(R.id.filename_textView);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.status_textView);
        startButton  = (Button) findViewById(R.id.startButton);
        pauseButton  = (Button) findViewById(R.id.pauseButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        // receive intent
        Intent intent = getIntent();

        // set initial text
//        String pathname = intent.getStringArrayExtra("pathname").toString();
//        String[] splitPaths = pathname.split("/");
//        String filenameText = splitPaths[splitPaths.length - 1];
//
//        if (splitPaths.length > 0){
//            filename.setText("filename: " + filenameText);
//        } else {
//            // actually we need to do something more than just set text
//            filename.setText("Error: no such file or directory");
//        }

        // calculate initial QR code and prepare camera
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setFacing(1).setRequestedPreviewSize(640, 480).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
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
                    surfaceView.post(new Runnable() {
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

        // calculate about file
        //  // read and convert it to byte array
//        file = new File(pathname);
//        int fileLength = (int) file.length();
//        byte[] bytes = new byte[fileLength];
//
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            fileInputStream.read(bytes);
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // file segmentation
        // int part = Math.ceil(fileLength / 20);

        // set progress bar
        status.setText("0%");

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generate initial QR code
                bitmap = qrGenerator("www.google.co.th/");

                // hide surface view by set both its
                // height: match-parent, height: 0, weight: 0
                LinearLayout.LayoutParams surfaceViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                surfaceViewParams.weight = 0.0f;
                surfaceView.setLayoutParams(surfaceViewParams);

                // render QR code to image view and show by set its
                // height: match-parent, height: 0, weight: 1
                imageView.setImageBitmap(bitmap);
                LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                imageViewParams.weight = 1.0f;
                imageView.setLayoutParams(imageViewParams);

                // transfer the file below this (?)
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

    private Bitmap qrGenerator(String rawData) {
        Bitmap bitmap = null;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(rawData, BarcodeFormat.QR_CODE, 750, 750);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
