package com.example.cnk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    TextView nicknameTv;
    EditText name, surname, nickname;
    Button save, dialogs, signout;
    String userID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    SharedPreferences sPref;
    ProgressBar prBar;
    SharedPreferences.Editor ed;
    Boolean readyToFinish = false, checkFOrProfile = false;
    private FirebaseAuth mAuth;
    private DatabaseReference users = database.getReference("Users");
    private double x1, x2, y1, y2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prBar = findViewById(R.id.Bar);
        prBar.setVisibility(ProgressBar.VISIBLE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.backForDialogsWindowItem)));
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        signout = findViewById(R.id.btnSignOut);
        nicknameTv = findViewById(R.id.nicknameTv);
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
                if(dataSnapshot.child("profileComplete").getValue(String.class).equals("true")){
                    nicknameTv.setText("Никнейм: "+dataSnapshot.child("nickname").getValue(String.class));
                    nickname.setVisibility(View.INVISIBLE);
                    nicknameTv.setVisibility(View.VISIBLE);
                    checkFOrProfile = true;
                }
                else {
                    nicknameTv.setVisibility(View.INVISIBLE);
                    nickname.setVisibility(View.VISIBLE);
                }
                nickname.setText(dataSnapshot.child("nickname").getValue(String.class));
                name.setText(dataSnapshot.child("name").getValue(String.class));
                surname.setText(dataSnapshot.child("surname").getValue(String.class));
                ed = sPref.edit();
                ed.putString("Nickname", dataSnapshot.child("nickname").getValue(String.class));
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
                if(!checkFOrProfile){
                    Toast.makeText(getApplicationContext(), "Закончите настройку профиля", Toast.LENGTH_LONG).show();
                }
                if (readyToFinish&&checkFOrProfile) {
                    Intent intent = new Intent(getApplicationContext(), DialogsWindow.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nickname.getText().toString().isEmpty()){
                    Toast.makeText(Profile.this, "введите никнейм", Toast.LENGTH_SHORT).show();
                    return;
                }
                myRef.orderByChild("nickname").equalTo(nickname.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (userID != dataSnapshot.getKey()&&nickname.getVisibility()==View.VISIBLE ) {
                            Toast.makeText(getApplicationContext(), "никнейм уже занят", Toast.LENGTH_LONG).show();
                            nickname.setText("");
                            users.child("nicknames").child(userID).setValue(nickname.getText().toString());
                            users.child(userID).child("nickname").setValue(nickname.getText().toString());
                            ed.putString("Nickname", nickname.getText().toString());
                            users.child(userID).child("profileComplete").setValue("false");
                            ed.commit();
                        }
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
                users.child("nicknames").child(userID).setValue(nickname.getText().toString());
                users.child(userID).child("profileComplete").setValue("true");
                users.child(userID).child("nickname").setValue(nickname.getText().toString());
                users.child(userID).child("name").setValue(name.getText().toString());
                users.child(userID).child("surname").setValue(surname.getText().toString());
                ed.putString("Nickname", nickname.getText().toString());
                ed.putString("Name", name.getText().toString());
                ed.putString("Surname", surname.getText().toString());
                ed.commit();

                // Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_LONG).show();

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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                x2 = event.getX();
                y2 = event.getY();
                if ((x2 - x1) < -300 && Math.abs(y2 - y1) < 200 && readyToFinish&&checkFOrProfile) {
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

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.CustomAlertDialog));
        quitDialog.setTitle("Выйти?");

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                finish();
            }
        });

        quitDialog.show();
    }
}
