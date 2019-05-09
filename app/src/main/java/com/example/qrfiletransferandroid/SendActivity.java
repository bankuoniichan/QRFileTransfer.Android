package com.example.qrfiletransferandroid;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.provider.DocumentsContract;
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
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendActivity extends AppCompatActivity {

    public static MyAppDatabase myAppDatabase;
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
        myAppDatabase = MyAppDatabase.getAppDatabase(getApplicationContext());

        // bind variable with object
        filename = (TextView) findViewById(R.id.filename_textView);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.status_textView);
        startButton  = (Button) findViewById(R.id.startButton);
        pauseButton  = (Button) findViewById(R.id.pauseButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        // test
        imageView = (ImageView) findViewById(R.id.test_imageView);
        // receive intent
        Intent intent = getIntent();

        // calculate things
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

        // set initial text
        filename.setText("filename: " + intent.getStringExtra("filename"));
        status.setText("0%");

        // set on xml instead
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generate QR code
                bitmap = qrGenerator("www.google.co.th/");

                Log.e("tag","set surface size to 0, 0");
                // hide surface view by set both its sizes to 0
                surfaceView.getHolder().setFixedSize(0,0);

                // render QR code to image view and set its size to
                imageView.setImageBitmap(bitmap);
//                imageView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                imageView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                params.weight = 1.0f;
                imageView.setLayoutParams(params);

                String type = "Send";
                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("dd/MM/yyyy");
                String date = simpleDateFormat_date.format(new Date());
                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("hh:mm:ss");
                String time = simpleDateFormat_time.format(new Date());
                String fileName = "TestFile";
                Log.e("Type: ",type);
                Log.e("FileName: ",fileName);
                Log.e("Date: ",date);
                Log.e("Time: ",time);

                History history = new History();
                history.setType(type);
                history.setDate(date);
                history.setTime(time);
                history.setFileName(fileName);
                myAppDatabase.myDao().addHistory(history);
                Toast.makeText(getApplicationContext(),"History Added Successfully",Toast.LENGTH_SHORT).show();
//                SurfaceHolder holder = displayView.getHolder();
//                Canvas canvas = null;
//                try {
//                    canvas = holder.lockCanvas(null);
//                    synchronized (holder) {
//                        draw(canvas, bitmap);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    Log.e("tag", "b4 if");
//                    if (canvas != null) {
//                        Log.e("tag", "canvas != null");
//                        displayView.getHolder().unlockCanvasAndPost(canvas);
//                    } else {
//                        Log.e("tag", "canvas == null");
//                    }
//                }

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
            BitMatrix bitMatrix = multiFormatWriter.encode(rawData, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

//    private void draw(Canvas canvas, Bitmap bitmap) {
//        canvas.drawColor(Color.BLACK);
//        canvas.drawBitmap(bitmap, 0, 0 , null);
//    }

}
