package com.example.qrfiletransferandroid;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendFragment extends Fragment {

    class FileListItemAdapter extends RecyclerView.Adapter<FileListItemAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            public ViewHolder(View v){
                super(v);
                text = v.findViewById(R.id.text);
            }
        }

        ArrayList<String> files;

        public FileListItemAdapter(ArrayList<String> files){
            this.files = files;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = (View) LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.filelist_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            String file = files.get(i);
            if(file.length() > 26){
                file = file.substring(0, 24) + "...";
            }
            viewHolder.text.setText(file);
        }

        @Override
        public int getItemCount() {
            return files.size();
        }
    }

    int color;
    RecyclerView fileList;
    ArrayList<String> files = new ArrayList<>();

    public SendFragment() {

    }

    public SendFragment withColor(int color) {
        this.color = color;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);
        view.findViewById(R.id.layout).setBackgroundColor(getResources().getColor(color));

        fileList = view.findViewById(R.id.fileList);
        fileList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        fileList.setHasFixedSize(true);
        FileListItemAdapter adapter = new FileListItemAdapter(files);
        fileList.setAdapter(adapter);
        getFiles();

        return view;
    }

    private void getFiles() {
        String extPath = Environment.getExternalStorageDirectory().toString();
        File dir = new File(extPath, "Download");
        File[] files = dir.listFiles();
        for(File file: files){
            this.files.add(file.getName());
        }
    }
}
