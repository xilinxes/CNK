package com.example.cnk;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    Button ok;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private EditText email, pass;
    private TextView vxod;
    private DatabaseReference users = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        vxod = findViewById(R.id.edtRegistration);
        email = findViewById(R.id.edtEmail2);
        pass = findViewById(R.id.edtPass2);
        mAuth = FirebaseAuth.getInstance();
        ok = findViewById(R.id.btnREG);
        vxod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, Authorization.class));
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //  currentUser.updatePassword()
    }

    private void createUser() {
        final String mail, password;
        mail = email.getText().toString();
        password = pass.getText().toString();
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            users.child(String.valueOf(mail.hashCode())).setValue(mail.hashCode());
                            users.child(String.valueOf(mail.hashCode())).child("mail").setValue(mail);
                            users.child(String.valueOf(mail.hashCode())).child("password").setValue(password);
                            Log.d("Tag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updateEmail(mail);
                            user.updatePassword(password);
                            Intent intent = new Intent(getApplicationContext(), Profile.class);
                            intent.putExtra("ID", String.valueOf(mail.hashCode()));
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}






