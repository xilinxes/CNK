package com.example.cnk;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView msgg,countMsgs;
    ImageView avatarka;
    ProgressBar prBar;
    DataAdapter.OnNoteListener onNoteListener;


    public ViewHolder(View itemView, DataAdapter.OnNoteListener onNoteListener) {
        super(itemView);
        prBar = itemView.findViewById(R.id.prBarItemForDialogs);
        avatarka = itemView.findViewById(R.id.imgAvatarForItemDialogs);
        msgg = itemView.findViewById(R.id.messageItem);
        countMsgs = itemView.findViewById(R.id.tvCountUnrMessages);
        this.onNoteListener = onNoteListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onNoteListener.onNoteClick(getLayoutPosition());
    }

}
