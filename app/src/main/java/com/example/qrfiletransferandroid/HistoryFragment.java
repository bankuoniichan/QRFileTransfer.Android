package com.example.qrfiletransferandroid;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);
        recyclerView = view.findViewById(R.id.historyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        MyAppDatabase myAppDatabase = HomeActivity.myAppDatabase;
        List<History> history = myAppDatabase.myDao().getHistory();
        Log.e("DATABASE HAVE ",history.size()+" ROWS");
        Log.e("**************","*****************");
        for (History his : history){
            Log.e("ID: ",his.getId()+"");
            Log.e("Type: ",his.getType()+"");
            Log.e("FileName: ",his.getFileName()+"");
            Log.e("Date: ",his.getDate()+"");
            Log.e("Time: ",his.getTime()+"");
            Log.e("-----","-----");
        }
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(history);
//        adapter.setItemClickListener(new MyRecyclerAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Log.d("Name", "Date : " + position);
//            }
//        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}

