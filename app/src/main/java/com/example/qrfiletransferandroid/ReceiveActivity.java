package com.example.qrfiletransferandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Hashtable;

public class ReceiveActivity extends AppCompatActivity {

    private File file;
    private TextView filename;
    private SurfaceView surfaceView;
    private ProgressBar progressBar;
    private TextView statusText;
    private Button startButton;
    private Button pauseButton;
    private Button cancelButton;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private byte[] bytes;
    private ByteBuffer byteBuffer;
    private Bitmap bitmap;

    private ImageView imageView;
    private int blockSize;
    private int blockNumber;
    private int qrCodeImageSize = 1200;

    private Boolean isReceiving = false;
    private String status = "unready";
    private int progress;
    public class Header {
        int blockNumber;
        int blockSize;
        String fileName;
        byte[] md5;

        public Header(int blockNumber,int blockSize,String fileName,byte[] md5) {
            this.blockNumber = blockNumber;
            this.blockSize = blockSize;
            this.fileName = fileName;
            this.md5 = md5;
        }
    };

    private Header header;
    private String stringResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // bind variable with object
        filename = (TextView) findViewById(R.id.filename_textView);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        statusText = (TextView) findViewById(R.id.status_textView);
        startButton  = (Button) findViewById(R.id.startButton);
        pauseButton  = (Button) findViewById(R.id.pauseButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        // receive intent
        Intent intent = getIntent();

        // calculate initial QR code and prepare camera
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setFacing(1).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build();
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
                            qrHandler(qrCodes);
                        }
                    });
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                statusText.setText("Started");
                status = "ready";
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                if (isReceiving) {
                    pause();
                } else {
                    resume();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(-1);
                finish();
            }
        });

    }

    private void vibrate(int time) {
        Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    private Bitmap qrEncoder(String rawData, int blockSize, String responseStatus) {
        Bitmap bitmap = null;
        // old version content is pure rawData > String content = rawData;
        // maybe attach something before rawData like
        String content = responseStatus + " " + progress + " " + rawData;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, blockSize, blockSize, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void setViewLayoutParams(View view, float f) {
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        viewParams.weight = f;
        view.setLayoutParams(viewParams);
    }

    private byte[] md5Calculator(byte[] bytes) {
        MessageDigest md;
        byte[] digest = null;

        try {
            md = MessageDigest.getInstance("MD5");
            digest = md.digest(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digest;
    }

    private void receive(String contentBlock) {
        progress++;

        // save content to byte buffer
        byte[] byteContentBlock = contentBlock.getBytes(StandardCharsets.UTF_8);
        byteBuffer.put(byteContentBlock);

        // receive response
        // create and show up acknowledge QR code
        Bitmap cbsBitmap = qrEncoder("", qrCodeImageSize, "BLOCK_OK");
        imageView.setImageBitmap(cbsBitmap);

        if (progress == blockNumber) {
            status = "complete";
            vibrate(3000);

            // write file from byte buffer
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(header.fileName);
                fileOutputStream.write(byteBuffer.array());
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            finish();
        }
    }

    private void pause() {
        // set receiving-flag and toggle pause button
        isReceiving = false;
        pauseButton.setText("continue");

        // create and show up pause-request QR code
        Bitmap cbsBitmap = qrEncoder("", qrCodeImageSize, "PAUSE_REQUEST");
        imageView.setImageBitmap(cbsBitmap);
    }

    private void resume() {
        // set receiving-flag and toggle pause button back
        isReceiving = true;
        pauseButton.setText("pause");

        // create and show up pause-request QR code
        Bitmap cbsBitmap = qrEncoder("", qrCodeImageSize, "RESUME_REQUEST");
        imageView.setImageBitmap(cbsBitmap);
    }

    private void setInitialProgressBar(int max) {
        progressBar.setMax(max);
        progressBar.setProgress(0);
        progress = 0;
    }

    private void updateProgressBar() {
        progressBar.setProgress(progress);
        statusText.setText(String.valueOf(progress/blockNumber).concat("%"));
    }

    private void qrHandler(SparseArray<Barcode> qrCodes) {
        // rawText is an header with data that is form "${receivedStatus} ${receivedProgress} ${contentBlock}"
        String rawText = qrCodes.valueAt(0).displayValue;
        String receivedStatus = rawText.split(" ")[0];
        int responseProgress = Integer.parseInt(rawText.split(" ")[1]);
        String contentBlock = rawText.split(" ")[2];

        Log.e("tag", rawText);

        if (status.equals("receive")) {
            // valid progress : is response's progress same as current one
            Boolean validProgress = responseProgress == progress;

            // wait until receive new acknowledge
            if (validProgress && receivedStatus.equals("BLOCK")) {
                receive(contentBlock);
                updateProgressBar();
            }
        } else if (status.equals("ready")) {
            // after send header, wait for acknowledge's header which progress value is -1
            Boolean validProgress = responseProgress == 0;

            if (validProgress && receivedStatus.equals("HEADER")) {
                status = "receive";
                isReceiving = true;

                // save header
                try {
                    Log.e("tag2", contentBlock);
                    header.fileName = contentBlock.split("_")[0];
                    header.blockNumber = Integer.parseInt(contentBlock.split("_")[1]);
                    header.blockSize = Integer.parseInt(contentBlock.split("_")[2]);
                    header.md5 = contentBlock.split("_")[3].getBytes();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // set initial progress bar
                setInitialProgressBar(header.blockNumber);
                updateProgressBar();

                // generate initial response QR code
                stringResponse = "";
                bitmap = qrEncoder(stringResponse, qrCodeImageSize, "HEADER_OK");

                // hide surface view && render QR code to image view then show it
                setViewLayoutParams(surfaceView, 0.0f);
                setViewLayoutParams(imageView, 1.0f);
                imageView.setImageBitmap(bitmap);

            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
