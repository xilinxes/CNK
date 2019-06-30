package com.example.cnk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DialogsWindow extends AppCompatActivity implements DataAdapter.OnNoteListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    BottomSheetBehavior mBottomSheetBehavior;
    RecyclerView recMsgs;
    AutoCompleteTextView name;
    DatabaseReference myRef = database.getReference("Users");
    SharedPreferences sPref;
    String userID;
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> countUnreadedMsgs = new ArrayList<>();
    ArrayList<String> baseOfNicks = new ArrayList<>();
    ArrayList<String> lastReadedMessage = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Button dialog, save;
    String currentUsernickname, currentWithUserHashId, allCountMessages;
    SharedPreferences.Editor ed;
    int i = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs_window);

        try {
            startService(new Intent(getApplicationContext(), MessageNotifficationService.class));
            sPref = getSharedPreferences("Saves", MODE_PRIVATE);
            takeUserNick();
            loadText();
            View bottomSheet = findViewById(R.id.bottomSht);
            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            name = findViewById(R.id.name);
            save = findViewById(R.id.save);
            dialog = findViewById(R.id.addDialog);
            recMsgs = (RecyclerView) findViewById(R.id.dialogs);
            recMsgs.setLayoutManager(new LinearLayoutManager(this));
            final DataAdapter dataAdapter = new DataAdapter(this, messages, countUnreadedMsgs, this);
            recMsgs.setAdapter(dataAdapter);
            adapter = new ArrayAdapter<>(this, R.layout.drop_down_spinner, baseOfNicks);
            name.setAdapter(adapter);


            myRef.child(userID).child("dialogs").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String x = String.valueOf(dataSnapshot.getKey());
                    messages.add(x);
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            allCountMessages = String.valueOf(dataSnapshot.child(userID).child("dialogs_info").child("allCountMessages").child(messages.get(i)).getValue());
                            lastReadedMessage.add(String.valueOf(dataSnapshot.child(userID).child("dialogs_info").child("lastReadedMessage").child(messages.get(i)).getValue()));
                            int res;
                            try {
                                res = Integer.parseInt(allCountMessages) - Integer.parseInt(lastReadedMessage.get(i));
                            } catch (NumberFormatException e) {
                                res = 0;
                                Log.d("check3.0", "---");
                            }
                            if (res == 0) {
                                countUnreadedMsgs.add("");
                            } else {
                                countUnreadedMsgs.add(String.valueOf(res));
                            }

                            if (i < messages.size() - 1) {
                                i++;
                            }
                            dataAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    myRef.addValueEventListener(new ValueEventListener() {
                        int check = 0;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (check = 0; check < messages.size(); check++) {
                                allCountMessages = String.valueOf(dataSnapshot.child(userID).child("dialogs_info").child("allCountMessages").child(messages.get(check)).getValue());
                                lastReadedMessage.set(check, String.valueOf(dataSnapshot.child(userID).child("dialogs_info").child("lastReadedMessage").child(messages.get(check)).getValue()));
                                int res = 0;
                                try {
                                    res = Integer.parseInt(allCountMessages) - Integer.parseInt(lastReadedMessage.get(check));
                                } catch (NumberFormatException e) {
                                    res = 0;
                                    Log.d("check3.0", "---");
                                } finally {
                                    if (res == 0) {
                                        countUnreadedMsgs.add("");
                                    } else {
                                        countUnreadedMsgs.set(check, String.valueOf(res));
                                    }
                                }
                            }
                            dataAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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


            myRef.child("nicknames").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String nick = dataSnapshot.getValue(String.class);
                    baseOfNicks.add(nick);
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

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDialog();
                }
            });
            dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        addDialog();
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        addDialog();
                    }
                }

            });
        } catch (Exception e) {
            Log.d("12345", e.toString());
        }
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
                try {
                    ed.putInt("countReadedMsgs", Integer.parseInt(lastReadedMessage.get(pos)));
                } catch (NumberFormatException e) {
                    e.getStackTrace();
                }
                //   ed.putString("Nickname",currentUsernickname);
                ed.commit();
                stopService(new Intent(getApplicationContext(), MessageNotifficationService.class));
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_down_up_close_enter,R.anim.activity_down_up_close_exit);
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

   /* public void startCheck(final DataAdapter dataAdapter) {
        messages.clear();
        countUnreadedMsgs.clear();
        try {

        } catch (Exception e) {
            Log.d("12345", e.toString());
        }
    }*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Profile.class);
        ed = sPref.edit();
        ed.putString("CurrentWithUserHashId", currentWithUserHashId);
        ed.commit();
        startActivity(intent);
        overridePendingTransition(R.anim.invert_left_in,R.anim.invert_right_out);
    }

    public void addDialog() {
        myRef.orderByChild("nickname").equalTo(name.getText().toString()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (int i = 0; i < messages.size(); i++) {
                    if ((messages.get(i)).equals(name.getText().toString())) {
                        Toast.makeText(DialogsWindow.this, "Диалог уже существует", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                currentWithUserHashId = dataSnapshot.getKey();
                Log.d("Test", currentUsernickname);
                String nick = name.getText().toString();
                myRef.child(userID).child("dialogs").child(name.getText().toString()).setValue(nick);
                myRef.child(currentWithUserHashId).child("dialogs").child(currentUsernickname).setValue(currentUsernickname);
                myRef.child(userID).child("dialogs_info").child("lastReadedMessage").child(nick).setValue("0");
                myRef.child(userID).child("dialogs_info").child("allCountMessages").child(nick).setValue("0");
                myRef.child(currentWithUserHashId).child("dialogs_info").child("allCountMessages").child(currentUsernickname).setValue("0");
                myRef.child(currentWithUserHashId).child("dialogs_info").child("lastReadedMessage").child(currentUsernickname).setValue("0");
                //pr1 = true;
                ed = sPref.edit();
                ed.putString("CurrentDialogName", name.getText().toString());
                ed.putString("CurrentWithUserHashId", dataSnapshot.getKey());
                ed.putInt("countReadedMsgs", 0);
                ed.commit();
                stopService(new Intent(getApplicationContext(), MessageNotifficationService.class));
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_down_up_close_enter,R.anim.activity_down_up_close_exit);
                name.setText("");
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
}
