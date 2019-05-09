package com.example.qrfiletransferandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.historyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[][] data = {{"Top","20/05/2019"},{"Bank","15/03/2019"}};
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(data);
        adapter.setItemClickListener(new MyRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("Name", "Date : " + position);
            }
        });
        recyclerView.setAdapter(adapter);

    }
}
