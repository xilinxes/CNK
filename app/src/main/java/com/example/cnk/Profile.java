package com.example.cnk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Random;

public class Profile extends AppCompatActivity {
    EditText name, surname, nickname;
    Button save, dialogs;
    SharedPreferences sPref;
    String userID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private DatabaseReference users = database.getReference("Users");
    private boolean pb1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        name = findViewById(R.id.name);
        dialogs = findViewById(R.id.dialogs);
        surname = findViewById(R.id.surname);
        nickname = findViewById(R.id.nickname);
        save = findViewById(R.id.save);
        dialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DialogsWindow.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadText();
                users.child(userID).child("nickname").setValue(nickname.getText().toString());
                users.child(userID).child("name").setValue(name.getText().toString());
                users.child(userID).child("surname").setValue(surname.getText().toString());
                sPref = getSharedPreferences("Saves", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
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
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        this.userID = String.valueOf(sPref.getInt("USER_ID", 1));
    }
}
