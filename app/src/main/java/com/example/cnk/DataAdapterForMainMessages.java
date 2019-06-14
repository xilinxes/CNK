package com.example.cnk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;

public class DataAdapterForMainMessages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_TEXT = 1, TYPE_IMAGE = 2;
    LinkedList<String> messages;
    // LayoutInflater inflater;
    private Context context;
    FirebaseStorage storage;
    StorageReference ref;


    public DataAdapterForMainMessages(Context context, LinkedList<String> messages) {
        this.context = context;
        this.messages = messages;
        storage = FirebaseStorage.getInstance();
        // this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        String msg = messages.get(position);
        String check = "";
        try {
            check = msg.substring(0, 8);
        } catch (IndexOutOfBoundsException e) {
            e.getStackTrace();
        }
        if (check.equals("dialogs/")) {
            return TYPE_IMAGE;
        } else {
            return TYPE_TEXT;
        }
        // return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        /*View view = inflater.inflate(R.layout.item_msg, viewGroup, false);
        return new ViewHLDER(view);*/
        int layout = 0;
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_TEXT:
                layout = R.layout.item_msg;
                View textView = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(layout, viewGroup, false);
                viewHolder = new ViewHLDER(textView);
                break;
            case TYPE_IMAGE:
                layout = R.layout.item_mage;
                View imageView = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(layout, viewGroup, false);
                viewHolder = new ViewHlderForImages(imageView);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        String msg = messages.get(position);
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case TYPE_TEXT:
                ((ViewHLDER) viewHolder).showText(msg);
                break;
            case TYPE_IMAGE:
                ref = storage.getReferenceFromUrl("gs://cnkfirebaseproject.appspot.com/" + msg);
                ((ViewHlderForImages) viewHolder).showImage(ref);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();

    }

    public class ViewHLDER extends RecyclerView.ViewHolder {
        private TextView msgg;

        public ViewHLDER(View itemView) {
            super(itemView);
            msgg = itemView.findViewById(R.id.messageItem);
        }

        public void showText(String text) {
            msgg.setText(text);
        }

    }


    public class ViewHlderForImages extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHlderForImages(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
        }

        public void showImage(StorageReference ref) {
            GlideApp.with(context)
                    .load(ref)
                    .into(image);
        }

    }


}
