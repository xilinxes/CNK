package com.example.cnk;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<ViewHolder>  {
    ArrayList<String> messages;
    ArrayList<String> countUnreadedMsgs;
    LayoutInflater inflater;
    private OnNoteListener onNoteListener;


    public DataAdapter(Context context, ArrayList<String> messages, ArrayList<String> countUnreadedMsgs, OnNoteListener onNoteListener) {
        this.messages = messages;
        this.countUnreadedMsgs = countUnreadedMsgs;
        this.inflater = LayoutInflater.from(context);
        this.onNoteListener = onNoteListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_for_diaologs, viewGroup, false);
        return new ViewHolder(view, onNoteListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String msg = messages.get(i);
        String countMsgs = countUnreadedMsgs.get(i);
        viewHolder.countMsgs.setText(countMsgs);
        viewHolder.msgg.setText(msg);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

}
