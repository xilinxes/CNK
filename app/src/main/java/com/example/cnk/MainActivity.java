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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static int MAX_MESSAGE_LENGTH = 150;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    Button btnInput, signout;
    EditText editMsg;
    ArrayList<String> messages = new ArrayList<>();
    RecyclerView recMsgs;
    SharedPreferences sPref;
    String name, userID,currentWithUserHashId,dlgnm, msg;
    String dialogName = "Диалог с ";
    SharedPreferences.Editor ed;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        load();
        Log.d("aaa",name);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        getSupportActionBar().setTitle(dialogName);
        startService(new Intent(this, MessageService.class));
        mAuth = FirebaseAuth.getInstance();
        //btnClear = (Button) findViewById(R.id.btnClear);
        btnInput = (Button) findViewById(R.id.btnSndMsg);
        signout = findViewById(R.id.btnSignOut);
        editMsg = (EditText) findViewById(R.id.editMsg);
        recMsgs = (RecyclerView) findViewById(R.id.recyclerMsg);
        recMsgs.setLayoutManager(new LinearLayoutManager(this));
        final DataAdapterForMainMessages dataAdapter = new DataAdapterForMainMessages(this, messages);
        recMsgs.setAdapter(dataAdapter);
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        userID = String.valueOf(sPref.getInt("USER_ID", 1));

        myRef.orderByChild("nickname").equalTo(dlgnm.toString()).addChildEventListener(new ChildEventListener() {
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

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed = sPref.edit();
                ed.putString("Nickname", "");
                ed.putString("Name", "");
                ed.putString("Surname", "");
                ed.putBoolean("check", false);
                ed.commit();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this, Authorization.class));
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
                msg=editMsg.getText().toString();
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

                myRef.child(currentWithUserHashId).child("dialogs").child(name).push().setValue(msg);
                myRef.child(userID).child("dialogs").child(dlgnm).push().setValue(msg);
                Log.d("Test",currentWithUserHashId+" "+name+" "+msg);
                Log.d("Test",userID+" "+dlgnm+" "+msg);
                editMsg.setText("");
                //Toast.makeText(getApplicationContext(),currentWithUserHashId.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        myRef.child(userID).child("dialogs").child(dlgnm).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String messg = dataSnapshot.getValue(String.class);
                messages.add(messg);
                dataAdapter.notifyDataSetChanged();
                recMsgs.smoothScrollToPosition(messages.size());
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

    }
    public void load(){
        name = sPref.getString("Nickname", "r");
        dlgnm = sPref.getString("CurrentDialogName", "");
        dialogName += sPref.getString("CurrentDialogName", "");
        currentWithUserHashId = sPref.getString("CurrentWithUserHashId","rrr");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, DialogsWindow.class);
            finish();
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
