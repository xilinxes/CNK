package com.example.cnk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {
    ArrayList<String> messages;
    ArrayList<String> countUnreadedMsgs;
    ArrayList<String> avatarki;
    ArrayList<Integer> ADAPTER_STATE;
    LayoutInflater inflater;
    FirebaseStorage storage;
    Uri url;
    private OnNoteListener onNoteListener;


    public DataAdapter(Context context, ArrayList<String> messages, ArrayList<String> countUnreadedMsgs, ArrayList<String> avatarki, ArrayList<Integer> ADAPTER_STATE, OnNoteListener onNoteListener) {
        this.messages = messages;
        this.countUnreadedMsgs = countUnreadedMsgs;
        this.avatarki = avatarki;
        this.inflater = LayoutInflater.from(context);
        this.onNoteListener = onNoteListener;
        this.storage = FirebaseStorage.getInstance();
        this.ADAPTER_STATE = ADAPTER_STATE;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_for_diaologs, viewGroup, false);
        return new ViewHolder(view, onNoteListener);
    }


    @Override
     public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        try {
            String msg = messages.get(i);
            String countMsgs = countUnreadedMsgs.get(i);
            if (ADAPTER_STATE.get(i) == 0) {
                if (!avatarki.get(i).equals("emptyPhoto")&&avatarki.get(i).substring(0,5).equals("users")) {
                    StorageReference ref = storage.getReferenceFromUrl("gs://cnkfirebaseproject.appspot.com/" + avatarki.get(i));
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                         public void onSuccess(Uri uri) {
                            url = uri;
                            RequestOptions options = new RequestOptions()
                                    .centerCrop().circleCrop().priority(Priority.HIGH);
                            new GlideImageLoader(viewHolder.avatarka, viewHolder.prBar).load(String.valueOf(url), options);

                          //  if (viewHolder.avatarka.getDrawable() != null) ;
                        }
                    });
                } else {
                    RequestOptions options = new RequestOptions()
                            .centerCrop().circleCrop().priority(Priority.HIGH);
                    new GlideImageLoader(viewHolder.avatarka, viewHolder.prBar).load("https://np.edu/_resources/images/person-silhouette.png", options);
                }
            }
            viewHolder.countMsgs.setText(countMsgs);
            viewHolder.msgg.setText(msg);
        } catch (IndexOutOfBoundsException e) {

        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }

}
