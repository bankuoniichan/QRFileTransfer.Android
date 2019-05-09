package com.example.qrfiletransferandroid;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{

    private String[][] mDataSet;
    ItemClickListener mListener;

    public MyRecyclerAdapter(String[][] dataSet){
        mDataSet = dataSet;
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            date = itemView.findViewById(R.id.text_date);
            itemView.setOnClickListener(this);
        }

        public void setItem(int pos) {
            name.setText(mDataSet[pos][0]);
            date.setText("date: " + mDataSet[pos][1]);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) mListener.onItemClick(getAdapterPosition());
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        mListener = listener;
    }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_history_item,viewGroup,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.setItem(i);
        }

        @Override
        public int getItemCount() {
            return mDataSet.length;
        }
    }


