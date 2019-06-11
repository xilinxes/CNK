package com.example.cnk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    EditText name, surname, nickname;
    Button save, dialogs, signout;
    String userID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    private FirebaseAuth mAuth;
    private DatabaseReference users = database.getReference("Users");
    private double x1, x2, y1, y2;
    SharedPreferences sPref;
    ProgressBar prBar;
    SharedPreferences.Editor ed;
    Boolean readyToFinish = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prBar = findViewById(R.id.Bar);
        prBar.setVisibility(ProgressBar.VISIBLE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        signout = findViewById(R.id.btnSignOut);
        loadText();
        name = findViewById(R.id.name);
        dialogs = findViewById(R.id.dialogs);
        surname = findViewById(R.id.surname);
        nickname = findViewById(R.id.nickname);
        save = findViewById(R.id.save);
        startService(new Intent(this, MessageNotifficationService.class));
        ed = sPref.edit();
        ed.putBoolean("check", true);
        ed.commit();



        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue(String.class));
                surname.setText(dataSnapshot.child("surname").getValue(String.class));
                nickname.setText(dataSnapshot.child("nickname").getValue(String.class));
                ed = sPref.edit();
                ed.putString("Nickname", nickname.getText().toString());
                ed.putString("Name", name.getText().toString());
                ed.putString("Surname", surname.getText().toString());
                ed.commit();
                prBar.setVisibility(ProgressBar.INVISIBLE);
                readyToFinish = true;
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
                startActivity(new Intent(getApplicationContext(), Authorization.class));
            }
        });

        dialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readyToFinish) {
                    Intent intent = new Intent(getApplicationContext(), DialogsWindow.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.child("nicknames").child(userID).setValue(nickname.getText().toString());
                users.child(userID).child("nickname").setValue(nickname.getText().toString());
                users.child(userID).child("name").setValue(name.getText().toString());
                users.child(userID).child("surname").setValue(surname.getText().toString());
                ed = sPref.edit();
                ed.putString("Nickname", nickname.getText().toString());
                ed.putString("Name", name.getText().toString());
                ed.putString("Surname", surname.getText().toString());
                ed.commit();
                Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_LONG).show();


            }
        });

        /*users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userID = arguments.get("ID").toString();
                info.setText(String.valueOf(dataSnapshot.child(userID).child("name").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    void loadText() {
        userID = String.valueOf(sPref.getInt("USER_ID", 1));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*Intent intent = new Intent(this, Profile.class);
            finish();
            startActivity(intent);*/
        }
        // return super.onKeyDown(keyCode, event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                x2 = event.getX();
                y2 = event.getY();
                if ((x2 - x1) < -300 && Math.abs(y2 - y1) < 200 && readyToFinish) {
                    Intent intent = new Intent(getApplicationContext(), DialogsWindow.class);
                    finish();
                    startActivity(intent);
                }
                break;
            }
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
