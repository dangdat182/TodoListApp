package com.example.todolistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class signup_activity extends AppCompatActivity {
    private EditText editTextusername , editTextpassword, editTextcomfirmps;
    private Button buttonsignup, buttonsigningg;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    int RC_SIGNIN = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        editTextusername = findViewById(R.id.editTextusername1);
        editTextpassword = findViewById(R.id.editTextpassword1);
        editTextcomfirmps = findViewById(R.id.editTextcomfirmpassword);
        buttonsignup = findViewById(R.id.buttonsignup);
        buttonsigningg = findViewById(R.id.buttonggsignin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclientid))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        buttonsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextusername.getText().toString();
                String password = editTextpassword.getText().toString();
                String cfpassword = editTextcomfirmps.getText().toString();
                if (username.isEmpty() || password.isEmpty() || cfpassword.isEmpty()){
                    Toast.makeText(signup_activity.this, "Can't be left blank ", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(cfpassword))
                    {
                        Map<String, Object> taskMap1 = new HashMap<>();
                        taskMap1.put("username", username);
                        taskMap1.put("password", password);
                        firestore.collection("UserID").add(taskMap1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(signup_activity.this, "Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(signup_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(signup_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent();
                        intent.setClass(signup_activity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(signup_activity.this, " Comfirm password must be as same as password", Toast.LENGTH_SHORT).show();
                    }
                }

            }

    });
        buttonsigningg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            googleSignin();
        }
    });
}

    private void googleSignin() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, RC_SIGNIN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGNIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseauth(account.getIdToken());
                Profileactivity();

            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseauth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Map<String, Object> userid = new HashMap<>();
                            userid.put("username", user.getDisplayName());
                            userid.put("id", user.getUid());
                            userid.put("email", user.getEmail());
                            userid.put("profilephoto", user.getPhotoUrl().toString());
                            firestore.collection("UserIDGG").add(userid);
                            //Intent intent = new Intent();
                            //intent.setClass(signup_activity.this, ProfileActivity.class);
                            //startActivity(intent);
                        } else {
                            Toast.makeText(signup_activity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void Profileactivity() {
        finish();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}