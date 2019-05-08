package com.example.qrfiletransferandroid;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    final Fragment send = new SendFragment().withColor(R.color.white);
    final Fragment receive = new SendFragment().withColor(R.color.dark_pink);
    final Fragment history = new SendFragment().withColor(R.color.dark_brown);
    final FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fm.beginTransaction().add(R.id.main_container, history, "3").hide(history).commit();
        fm.beginTransaction().add(R.id.main_container, receive, "2").hide(receive).commit();
        fm.beginTransaction().add(R.id.main_container, send, "1").commit();

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.item_send:
                        fm.beginTransaction().hide(activeFragment).show(send).commit();
                        activeFragment = send;
                        return true;
                    case R.id.item_receive:
                        fm.beginTransaction().hide(activeFragment).show(receive).commit();
                        activeFragment = receive;
                        return true;
                    case R.id.item_history:
                        fm.beginTransaction().hide(activeFragment).show(history).commit();
                        activeFragment = history;
                        return true;
                }
                return false;
            }
        });
        nav.setSelectedItemId(R.id.item_send);
    }
}
