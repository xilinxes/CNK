package com.example.cnk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DialogsWindow extends AppCompatActivity implements DataAdapter.OnNoteListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recMsgs;
    EditText name;
    DatabaseReference myRef = database.getReference("Users");
    SharedPreferences sPref;
    String userID;
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> countUnreadedMsgs = new ArrayList<>();
    Button dialog;
    String currentUsernickname, currentWithUserHashId;
    SharedPreferences.Editor ed;
    Boolean pr1, pr2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs_window);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        loadText();
        takeUserNick();
        name = findViewById(R.id.name);
        dialog = findViewById(R.id.addDialog);
        recMsgs = (RecyclerView) findViewById(R.id.dialogs);
        recMsgs.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapter dataAdapter = new DataAdapter(this, messages, this);
        recMsgs.setAdapter(dataAdapter);


        myRef.child(userID).child("dialogs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String x = String.valueOf(dataSnapshot.getKey());
                messages.add(x);
                dataAdapter.notifyDataSetChanged();
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
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.orderByChild("nickname").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        currentWithUserHashId = dataSnapshot.getKey();
                        Log.d("Test",currentUsernickname);
                        myRef.child(userID).child("dialogs").child(name.getText().toString()).setValue(name.getText().toString());
                        myRef.child(currentWithUserHashId).child("dialogs").child(currentUsernickname).setValue(currentUsernickname);
                        /*myRef.child(currentWithUserHashId).child("dialogs_info").child(currentUsernickname).child("allCountMessages").setValue(0);
                        myRef.child(userID).child("dialogs_info").child(name.getText().toString()).child("allCountMessages").setValue(0);
                        myRef.child(currentWithUserHashId).child("dialogs_info").child(currentUsernickname).child("lastReadedMessage").setValue(0);
                        myRef.child(userID).child("dialogs_info").child(name.getText().toString()).child("lastReadedMessage").setValue(0);*/
                        name.setText("");
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

                //Toast.makeText(getApplicationContext(), "Такого пользователя не существует", Toast.LENGTH_LONG).show();
            }


        });
    }

    void loadText() {
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        userID = String.valueOf(sPref.getInt("USER_ID", 1));
    }


    @Override
    public void onNoteClick(int position) {
        currwithusr(position);
    }

    public void takeUserNick() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUsernickname = (dataSnapshot.child(userID).child("nickname").getValue(String.class));
                Log.d("Test",currentUsernickname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void currwithusr(final int pos) {
        myRef.orderByChild("nickname").equalTo(messages.get(pos)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                currentWithUserHashId = dataSnapshot.getKey();
                ed = sPref.edit();
                ed.putString("CurrentDialogName", messages.get(pos).toString());
                ed.putString("CurrentWithUserHashId", currentWithUserHashId);
             //   ed.putString("Nickname",currentUsernickname);
                ed.commit();
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(this,Profile.class);
        ed = sPref.edit();
        ed.putString("CurrentWithUserHashId", currentWithUserHashId);
        ed.commit();
        finish();
        startActivity(intent);
        return super.onKeyDown(keyCode, event);
    }


}
