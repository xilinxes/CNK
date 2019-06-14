package com.example.cnk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    static final int GALLERY_REQUEST = 1;
    private static int MAX_MESSAGE_LENGTH = 150;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageButton btnAddPhoto;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    Button btnInput;
    EditText editMsg;
    LinkedList<String> messages = new LinkedList<>();
    RecyclerView recMsgs;
    SharedPreferences sPref;
    String name, userID, currentWithUserHashId, dlgnm, msg;
    String dialogName = "Диалог с ";
    SharedPreferences.Editor ed;
    private DataAdapterForMainMessages dataAdapter;
    private Uri selectedImage;
    private int lastReadedMsg = 0;
    private FirebaseAuth mAuth;
    private Boolean ifInput = false;
    private UUID uuidPhoto;
    File localFile = null;
    Handler h = new Handler();
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        load();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        getSupportActionBar().setTitle(dialogName);
        startService(new Intent(this, MessageService.class));
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //btnClear = (Button) findViewById(R.id.btnClear);
        btnInput = (Button) findViewById(R.id.btnSndMsg);
        btnAddPhoto = (ImageButton) findViewById(R.id.btnAddPhoto);
        editMsg = (EditText) findViewById(R.id.editMsg);
        recMsgs = (RecyclerView) findViewById(R.id.recyclerMsg);
        recMsgs.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapterForMainMessages(this, messages);
        recMsgs.setAdapter(dataAdapter);
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        userID = String.valueOf(sPref.getInt("USER_ID", 1));
        Runnable run = new Runnable() {

            @Override
            public void run() {
                dataAdapter.notifyDataSetChanged();
                h.postDelayed(this, 3000);
            }
        };
        run.run();

        myRef.orderByChild("nickname").equalTo(dlgnm).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                currentWithUserHashId = dataSnapshot.getKey();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /*btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.removeValue();
                dataAdapter.notifyDataSetChanged();
                messages.clear();
            }
        });*/
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = editMsg.getText().toString();
                if (msg.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Пустое сообщение", Toast.LENGTH_SHORT).show();
                    return;
                }
                msg = name + ": ";
                msg += String.valueOf(editMsg.getText());

                if (msg.length() > MAX_MESSAGE_LENGTH) {
                    Toast.makeText(getApplicationContext(), "Слишком много символов", Toast.LENGTH_SHORT).show();
                    return;
                }
                ifInput = true;
                myRef.child(currentWithUserHashId).child("dialogs").child(name).push().setValue(msg);
                myRef.child(userID).child("dialogs").child(dlgnm).push().setValue(msg);
                Log.d("Test", currentWithUserHashId + " " + name + " " + msg);
                Log.d("Test", userID + " " + dlgnm + " " + msg);
                editMsg.setText("");
                //Toast.makeText(getApplicationContext(),currentWithUserHashId.toString(),Toast.LENGTH_SHORT).show();
            }
        });


        myRef.child(userID).child("dialogs").child(dlgnm).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              /*  String key = dataSnapshot.getKey();
                final String check = key.substring(0, 1);
                final String messg = dataSnapshot.getValue(String.class);
                String sub = key.substring(key.length() - 5);
                if (sub.equals("image")) {
                    ImageView img = findViewById(R.id.Images1);
                    Log.d("Test3.0", "equals");
                    i++;
                    ref = storage.getReferenceFromUrl("gs://cnkfirebaseproject.appspot.com/" + messg);
                    try {
                        localFile = File.createTempFile("aaa" + String.valueOf(new Random().nextInt(33)), "jpg");
                        GlideApp.with(MainActivity.this)
                                .load(ref)
                                .into(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Test3.0", "file downloaded");
                            messages.add(localFile.getPath());
                            //   dataAdapter.notifyDataSetChanged();
                            Log.d("Test3.0", String.valueOf(i));
                        }
                    });
                } else if (sub.equals("ssage")) {
                    messages.add(messg);
                    lastReadedMsg = dataAdapter.getItemCount();
                    if (ifInput) {
                        myRef.child(currentWithUserHashId).child("dialogs_info").child("allCountMessages").child(name).setValue(messages.size());
                    }
                    myRef.child(userID).child("dialogs_info").child("allCountMessages").child(dlgnm).setValue(messages.size());
                    recMsgs.smoothScrollToPosition(messages.size());
                    // dataAdapter.notifyDataSetChanged();
                }*/


                final String messg = dataSnapshot.getValue(String.class);
                messages.add(messg);
                lastReadedMsg = dataAdapter.getItemCount();
                if (ifInput) {
                    myRef.child(currentWithUserHashId).child("dialogs_info").child("allCountMessages").child(name).setValue(messages.size());
                }
                myRef.child(userID).child("dialogs_info").child("allCountMessages").child(dlgnm).setValue(messages.size());
                recMsgs.smoothScrollToPosition(messages.size());
                dataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

    }

    public void load() {
        name = sPref.getString("Nickname", "r");
        dlgnm = sPref.getString("CurrentDialogName", "");
        dialogName += sPref.getString("CurrentDialogName", "");
        currentWithUserHashId = sPref.getString("CurrentWithUserHashId", "rrr");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;


        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    // ImageView imageView = (ImageView) findViewById(R.id.imgvPhoto);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   /* Drawable image = new BitmapDrawable(bitmap);
                    editMsg.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
                    imageView.setImageBitmap(bitmap);*/
                   /* SpannableString ss = new SpannableString(uuidPhoto.toString());
                    Drawable image = new BitmapDrawable(bitmap);
                    image.setBounds(0, 0, 300, 300);
                    ImageSpan span = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    editMsg.setText(ss);*/

                    uuidPhoto = UUID.randomUUID();
                    uploadImage();


                }
        }



    }


    private void uploadImage() {

        if (selectedImage != null) {
            StorageReference ref = storageReference.child("dialogs/" + userID + "/" + dlgnm + "/" + uuidPhoto);
            ref.putFile(selectedImage);
            myRef.child(userID).child("dialogs").child(dlgnm).push().setValue("dialogs/" + userID + "/" + dlgnm + "/" + uuidPhoto);
            ref = storageReference.child("dialogs/" + currentWithUserHashId + "/" + name + "/" + uuidPhoto);
            ref.putFile(selectedImage);
            myRef.child(currentWithUserHashId).child("dialogs").child(name).push().setValue("dialogs/" + currentWithUserHashId + "/" + name + "/" + uuidPhoto);
            selectedImage = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myRef.child(userID).child("dialogs_info").child("lastReadedMessage").child(dlgnm).setValue(lastReadedMsg);
            Intent intent = new Intent(this, DialogsWindow.class);
            finish();
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPostResume() {
        stopService(new Intent(getApplicationContext(), MessageNotifficationService.class));
        myRef.child(userID).child("dialogs_info").child("lastReadedMessage").child(dlgnm).setValue(lastReadedMsg);
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        startService(new Intent(getApplicationContext(), MessageNotifficationService.class));
        myRef.child(userID).child("dialogs_info").child("lastReadedMessage").child(dlgnm).setValue(lastReadedMsg);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        startService(new Intent(getApplicationContext(), MessageNotifficationService.class));
        myRef.child(userID).child("dialogs_info").child("lastReadedMessage").child(dlgnm).setValue(lastReadedMsg);
        super.onDestroy();
    }


}
