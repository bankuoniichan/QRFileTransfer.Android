package com.example.qrfiletransferandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SendActivity extends AppCompatActivity {

    private TextView filename;
    private ImageView currentQR;
    private ProgressBar progressBar;
    private TextView status;
    private ToggleButton pauseButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // bind variable with object
        filename = (TextView) findViewById(R.id.filename_textView);
        currentQR = (ImageView) findViewById(R.id.qr_imageView);
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.status_textView);
        pauseButton  = (ToggleButton) findViewById(R.id.toggleButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        // receive intent
        Intent intent = getIntent();

        // calculate things
        Bitmap bm = test();
        currentQR.setImageBitmap(bm);

        // set initial text
        filename.setText("filename: " + intent.getStringExtra("filename"));
        status.setText("0%");

        // set button onlick
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
        // this is a small sample use of the QRCodeEncoder class from zxing
        try {
            // generate a 150x150 QR code
//            Bitmap bm = encodeAsBitmap(barcode_content, BarcodeFormat.QR_CODE, 150, 150);
            Bitmap bm = null;
            if(bm != null) {
                // image_view.setImageBitmap(bm);
                return bm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
