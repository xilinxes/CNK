package com.example.cnk;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;

public class DataAdapterForMainMessages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_LEFT_TEXT_MESSAGE = 1, TYPE_IMAGE_LEFT = 2, TYPE_RIGHT_TEXT_MESSAGE = 3, TYPE_IMAGE_RIGHT = 4;
    LinkedList<String> messages;
    FirebaseStorage storage;
    StorageReference ref;
    String equalname, userID;
    // LayoutInflater inflater;
    private Context context;
    CircularProgressDrawable circ;


    public DataAdapterForMainMessages(Context context, LinkedList<String> messages, String equalname, String userID) {
        this.context = context;
        this.messages = messages;
        this.equalname = equalname;
        this.userID = userID;
        storage = FirebaseStorage.getInstance();
        circ = new CircularProgressDrawable(context);
        circ.setStrokeWidth(15f);
        circ.setCenterRadius(50f);
        circ.start();
        // this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        String msg = messages.get(position);
        String checkForImageLeft = "", checkForImageRight = "";
        String checkForRightMessage = "";
        try {
            checkForRightMessage = msg.substring(0, equalname.length() + 2);

        } catch (IndexOutOfBoundsException e) {
            e.getStackTrace();
        }
        try {
            checkForImageLeft = msg.substring(0, 8);
        } catch (IndexOutOfBoundsException e) {
            e.getStackTrace();
        }
        try {
            checkForImageRight = msg.substring(0, 9 + userID.length());
        } catch (IndexOutOfBoundsException e) {
            e.getStackTrace();
        }
        if (checkForImageRight.equals("dialogs/" + userID + "/")) {
            return TYPE_IMAGE_RIGHT;
        } else if (checkForImageLeft.equals("dialogs/")) {
            return TYPE_IMAGE_LEFT;
        } else if (checkForRightMessage.equals(equalname + ": ")) {
            return TYPE_RIGHT_TEXT_MESSAGE;
        } else {
            return TYPE_LEFT_TEXT_MESSAGE;
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
            case TYPE_LEFT_TEXT_MESSAGE:
                layout = R.layout.item_msg;
                View textView = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(layout, viewGroup, false);
                viewHolder = new ViewHLDER(textView);
                break;
            case TYPE_IMAGE_LEFT:
                layout = R.layout.item_mage;
                View imageViewLeft = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(layout, viewGroup, false);
                viewHolder = new ViewHlderForImages(imageViewLeft);
                break;
            case TYPE_RIGHT_TEXT_MESSAGE:
                layout = R.layout.activity_item_right_message;
                View textviewForRightMsg = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(layout, viewGroup, false);
                viewHolder = new ViewHLDERForRightMessages(textviewForRightMsg);
                break;
            case TYPE_IMAGE_RIGHT:
                layout = R.layout.item_image_right;
                View imageViewRight = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(layout, viewGroup, false);
                viewHolder = new ViewHlderForImagesRight(imageViewRight);
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
            case TYPE_LEFT_TEXT_MESSAGE:
                ((ViewHLDER) viewHolder).showText(msg);
                break;
            case TYPE_IMAGE_LEFT:
                ref = storage.getReferenceFromUrl("gs://cnkfirebaseproject.appspot.com/" + msg);
                ((ViewHlderForImages) viewHolder).showImage(ref);
                break;
            case TYPE_RIGHT_TEXT_MESSAGE:
                ((ViewHLDERForRightMessages) viewHolder).showTextForRight(msg);
                break;
            case TYPE_IMAGE_RIGHT:
                ref = storage.getReferenceFromUrl("gs://cnkfirebaseproject.appspot.com/" + msg);
                ((ViewHlderForImagesRight) viewHolder).showImageRight(ref);
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
            int i = 0;
            int index = text.indexOf("Nfdjs33NJVjfdophkrgmvmDJfmgm039-=@!@#44,fdkSs");
            text = text.substring(index + 45);
            while ((text.charAt(i) == ' ' && i < text.length()) || (text.charAt(i) == '\n' && i < text.length())) {
                i++;
            }
            text = text.substring(i);
            msgg.setText(text);
        }

    }

    public class ViewHLDERForRightMessages extends RecyclerView.ViewHolder {
        private TextView msgg;

        public ViewHLDERForRightMessages(View itemView) {
            super(itemView);
            msgg = itemView.findViewById(R.id.messageItemForRight);
        }

        public void showTextForRight(String text) {
            int i = 0;
            text = text.substring(equalname.length() + 47);
            while ((text.charAt(i) == ' ' && i < text.length()) || (text.charAt(i) == '\n' && i < text.length())) {
                i++;
            }
            text = text.substring(i);
            msgg.setText(text);
        }

    }


    public class ViewHlderForImages extends RecyclerView.ViewHolder {
        private ImageView imageLeft;
        private ProgressBar progressBar;
        private Uri url;

        public ViewHlderForImages(View itemView) {
            super(itemView);
            imageLeft = itemView.findViewById(R.id.itemImageLeft);
            progressBar = itemView.findViewById(R.id.progressLeftBar);
        }

        public void showImage(StorageReference ref) {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    url = uri;
                    RequestOptions options = new RequestOptions()
                            .centerCrop().priority(Priority.HIGH);
                    new GlideImageLoader(imageLeft,progressBar).load(String.valueOf(url),options);
                }
            });


        }


    }

    public class ViewHlderForImagesRight extends RecyclerView.ViewHolder {
        private ImageView imageRight;
        private Uri url;
        private ProgressBar progressBar;
        public ViewHlderForImagesRight(View itemView) {
            super(itemView);
            imageRight = itemView.findViewById(R.id.itemImageRight);
            progressBar = itemView.findViewById(R.id.progressRightBar);
        }

        public void showImageRight(StorageReference ref) {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    url = uri;
                    RequestOptions options = new RequestOptions()
                            .centerCrop().priority(Priority.HIGH);
                    new GlideImageLoader(imageRight,progressBar).load(String.valueOf(url),options);
                }
            });
        }

    }


}
