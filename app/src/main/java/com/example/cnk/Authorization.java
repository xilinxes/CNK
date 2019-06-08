package com.example.cnk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class Authorization extends AppCompatActivity implements Serializable {
    //fixme local small test
    TextView reg;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences sPref;
    DatabaseReference myRef = database.getReference("Users");
    String currentUsername;
    private EditText edtEmail, edtPass;
    private Button btnOK;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar prBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        prBar = findViewById(R.id.Bar_auth);
        mAuth = FirebaseAuth.getInstance();
        reg = findViewById(R.id.edtRegistration);
        edtEmail = findViewById(R.id.edtEmail2);
        edtPass = findViewById(R.id.edtPass2);
        btnOK = findViewById(R.id.btnREG);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prBar.setVisibility(ProgressBar.VISIBLE);
                signIn();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    saveText();
                    finish();
                    startActivity(intent);
                }
            }
        };
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Authorization.this, Registration.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void signIn() {
        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(Authorization.this, "Fields are empty", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                               @Override
                                               public void onComplete(@NonNull Task<AuthResult> task) {

                                                   if (!task.isSuccessful()) {
                                                       prBar.setVisibility(ProgressBar.INVISIBLE);
                                                       Toast.makeText(Authorization.this, "Not correct pass or email", Toast.LENGTH_LONG).show();
                                                   }
                                               }
                                           }
                    );
        }

    }

    void saveText() {
        sPref = getSharedPreferences("Saves", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("USER_ID", edtEmail.getText().toString().hashCode());
        ed.commit();
    }


}
