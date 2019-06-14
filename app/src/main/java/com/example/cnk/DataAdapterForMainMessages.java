package com.example.cnk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapterForMainMessages extends RecyclerView.Adapter<DataAdapterForMainMessages.ViewHLDER> {
    ArrayList<String> messages;
    LayoutInflater inflater;

    public DataAdapterForMainMessages(Context context, ArrayList<String> messages) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ViewHLDER onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_msg, viewGroup, false);
        return new ViewHLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHLDER viewHolder, int i) {
            String msg = messages.get(i);
            Log.d("Test3.0", msg);
            String check = msg.substring(msg.length() - 3);
            if (check.equals("jpg")) {
                viewHolder.msgg.setText("");
                Bitmap bitmap = BitmapFactory.decodeFile(msg);
                Drawable image = new BitmapDrawable(bitmap);
                viewHolder.msgg.setBackground(image);
            } else {
                viewHolder.msgg.setText(msg);
            }

    }

    @Override
    public int getItemCount() {
        return messages.size();

    }

    public class ViewHLDER extends RecyclerView.ViewHolder {
        TextView msgg;

        public ViewHLDER(View itemView) {
            super(itemView);
            msgg = itemView.findViewById(R.id.messageItem);
        }

    }

}
