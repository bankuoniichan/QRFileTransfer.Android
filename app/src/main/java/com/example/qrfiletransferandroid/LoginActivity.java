package com.example.qrfiletransferandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "SSODemo";

//    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        txtView=findViewById(R.id.textViewInfo);
    }

    public void onLoginClick(View view) {
//        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//        startActivityForResult(intent, ProfileActivity.LOGIN);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void onGuestClick(View view) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProfileActivity.LOGIN && resultCode == Activity.RESULT_OK) {
            // Ticket can be reuse until expired.
            String ticket = data.getStringExtra("ticket");
            Log.i(TAG,"ticket:"+ticket);
            HelperTask helperTask=new HelperTask();
            helperTask.execute(ticket);
        }
    }

    class HelperTask extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String ticket=strings[0];
            String json = ChulaSSOHelper.serviceValidation(ticket);
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            try {
                JSONObject user = new JSONObject(json);
//                txtView.setText("Name: "+user.getString("gecos")+"\n"
//                        +"ID: "+user.getString("ouid")+"\n"
//                        +"Email: "+user.getString("email")
//                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
