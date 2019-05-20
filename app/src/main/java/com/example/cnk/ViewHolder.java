package com.example.cnk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView msgg;
    public ViewHolder(View itemView) {
        super(itemView);
        msgg = itemView.findViewById(R.id.messageItem);
    }
}
