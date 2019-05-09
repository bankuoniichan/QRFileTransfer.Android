package com.example.qrfiletransferandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ClickableSpan;
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
            View view;
            TextView text;
            String path;
            public ViewHolder(View v){
                super(v);
                view = v;
                text = v.findViewById(R.id.text);
            }
        }

        File[] files;
        View active = null;
        String selectedPath = null;

        public FileListItemAdapter(File[] files){
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
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            File file = files[i];
            String filename = file.getName();
            if(filename.length() > 26){
                filename = filename.substring(0, 24) + "...";
            }
            viewHolder.path = file.getAbsolutePath();
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPath = viewHolder.path;
                    activate(viewHolder.view);
                }
            });
            viewHolder.text.setText(filename);
        }

        @Override
        public int getItemCount() {
            return files.length;
        }

        private void activate(View view) {
            if(active != null) {
                active.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                snackbar.show();
            }
            if(active != view) {
                view.setBackgroundColor(getResources().getColor(R.color.light_gray));
                active = view;
            } else {
                active = null;
                snackbar.dismiss();
            }
        }

        public void deactivateItem() {
            active.setBackgroundColor(getResources().getColor(R.color.white));
            active = null;
        }

        public String getSelectedFilePath() {
            return selectedPath;
        }

    }

    int color;
    RecyclerView fileList;
    File[] files;
    Snackbar snackbar;

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
        getFiles();
        final FileListItemAdapter adapter = new FileListItemAdapter(files);
        fileList.setAdapter(adapter);

        snackbar = Snackbar
                .make(container.getRootView(), R.string.file_selected, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.send, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String filepath = adapter.getSelectedFilePath();
                        adapter.deactivateItem();
                        snackbar.dismiss();
                        send(filepath);
                    }
                });

        return view;
    }

    private void getFiles() {
        String extPath = Environment.getExternalStorageDirectory().toString();
        File dir = new File(extPath, "Download");
        files = dir.listFiles();
    }

    void send(String filepath) {
        Intent intent = new Intent(getContext(), SendActivity.class);
        intent.putExtra("pathname", filepath);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
