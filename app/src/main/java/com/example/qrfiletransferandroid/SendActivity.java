package com.example.qrfiletransferandroid;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
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
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendActivity extends AppCompatActivity {

    private File file;
    
    public static MyAppDatabase myAppDatabase;
    
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
    private Bitmap bitmap;

    private ImageView imageView;
    private int blockSize;
    private int blockNumber;
    private int qrCodeImageSize = 1200;

    private Boolean isSending = false;
    private String status = "propmt";
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
    private String stringHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        myAppDatabase = MyAppDatabase.getAppDatabase(getApplicationContext());

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

        // set initial text
        String pathname = intent.getStringExtra("pathname").toString();
        String[] splitPaths = pathname.split("/");
        String filenameText = splitPaths[splitPaths.length - 1];
        Log.e("Pathname", pathname);
        filename.setText("filename: " + filenameText);

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

        // calculate about file
        //  // read and convert it to byte array
        file = new File(pathname);
        int fileLength = (int) file.length();
        Log.e("file-length", String.valueOf(fileLength));
        bytes = new byte[fileLength];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // prepare data for header
        // // blockSize from QR code size
        // hard code for unnamed.jpd > 7886 length / 512 = 16 block
        int maxBlockSize = 512;
        blockSize = 512;

        // // blockNumber can obtain from 'fileLength / blockSize'
        blockNumber = 16;
        
        // create header
        header = new Header(blockNumber, blockSize, filenameText, md5Calculator(bytes));
        stringHeader = header.fileName + " " + header.blockNumber + " " + header.blockSize + " " + header.md5;

        // set progress bar
        setInitialProgressBar(blockNumber);
        updateProgressBar();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generate initial QR code
                bitmap = qrEncoder(stringHeader, qrCodeImageSize, "HEADER");

                // hide surface view && render QR code to image view then show it
                setViewLayoutParams(surfaceView, 0.0f);
                setViewLayoutParams(imageView, 1.0f);
                imageView.setImageBitmap(bitmap);
                
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

                // transfer the file below this (?)
                status = "w8 ack h8";
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                if (isSending) {
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

    private Bitmap qrEncoder(String rawData, int blockSize, String headerType) {
        Bitmap bitmap = null;
        // old version content is pure rawData > String content = rawData;
        // maybe attach something before rawData like
        String content = headerType + " " + progress + " " + rawData;

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

    private void send() {
        // send
        progress++;
        // ex. blockSize = 5 so we send 0 > 4 then 5 > 9 then 10 > 14
        String currentByteSet = new String(Arrays.copyOfRange(bytes, (progress - 1) * blockSize,progress * blockSize - 1));
        Bitmap cbsBitmap = qrEncoder(currentByteSet, qrCodeImageSize, "BLOCK");
        imageView.setImageBitmap(cbsBitmap);

        if (progress == blockNumber) {
            status = "complete";
            vibrate(3000);
        }
    }

    private void pause() {
        // set sending-flag and toggle pause button
        isSending = false;
        pauseButton.setText("continue");
    }

    private void resume() {
        // set sending-flag and toggle pause button back
        isSending = true;
        pauseButton.setText("pause");
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
        // rawText is an acknowledge that is form "${responseStatus} ${responseProgress}"
        String rawText = qrCodes.valueAt(0).displayValue;
        String responseStatus = rawText.split(" ")[0];
        int responseProgress = Integer.parseInt(rawText.split(" ")[1]);

        if (responseStatus == "PAUSE_REQUEST") { pause(); }
        else if (responseStatus == "RESUME_REQUEST") { resume(); }
        else if (status == "send") {
            // valid progress : is response's progress same as current one
            Boolean validProgress = responseProgress == progress;

            // wait until receive new acknowledge
            if (validProgress) {
                if (responseStatus == "BLOCK_OK") {
                    send();
                    updateProgressBar();
                }
            }
        } else if (status == "w8 ack h8") {
            // after send header, wait for acknowledge's header which progress value is -1
            Boolean validProgress = responseProgress == -1;

            if (validProgress && responseStatus == "HEADER_OK") {
                status = "send";
                isSending = true;
                send();
                updateProgressBar();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
