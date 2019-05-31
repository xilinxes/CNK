package com.example.cnk;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class DialogsWindow extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recMsgs;
    EditText name;
    DatabaseReference myRef = database.getReference("Users");
    SharedPreferences sPref;
    String userID;
    ArrayList<String> messages = new ArrayList<>();
    Button dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs_window);
        loadText();
        name = findViewById(R.id.name);
        dialog = findViewById(R.id.addDialog);
        recMsgs = (RecyclerView) findViewById(R.id.dialogs);
        recMsgs.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapter dataAdapter = new DataAdapter(this, messages);
        recMsgs.setAdapter(dataAdapter);


        myRef.child(userID).child("dialogs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String x = String.valueOf(dataSnapshot.getValue());
                Log.d("fff",x);
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
                myRef.child(userID).child("dialogs").child(name.getText().toString()).setValue(name.getText().toString());
                name.setText("");
            }
        });

    }
    void loadText() {
        sPref = getSharedPreferences("Saves",MODE_PRIVATE);
        this.userID = String.valueOf(sPref.getInt("USER_ID",1));
    }



}
