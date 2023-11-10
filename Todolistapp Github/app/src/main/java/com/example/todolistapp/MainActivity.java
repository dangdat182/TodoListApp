package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class MainActivity extends AppCompatActivity {
    private Button button, buttonsigningg;
    private FirebaseFirestore firestore;
    private EditText editTextpw;
    private EditText editTextUsername;
    private TextView buttontextsignup;
    private FirebaseAuth auth;
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    int RC_SIGNIN = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
        editTextpw = findViewById(R.id.editTextpassword);
        editTextUsername = findViewById(R.id.editTextusername);
        button = findViewById(R.id.buttonlogin);
        buttontextsignup = findViewById(R.id.buttontextsignup);
        buttonsigningg = findViewById(R.id.buttonsigningg);
        auth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclientid))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextpw.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username or password cannot be blank", Toast.LENGTH_SHORT).show();
                } else {


                    firestore.collection("UserID")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {

                                    String authpassword = queryDocumentSnapshots.getDocuments().get(0).getString("password");

                                    if (password.equals(authpassword)) {
                                        Toast.makeText(MainActivity.this, "Sucesssful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);

                                    } else {Toast.makeText(MainActivity.this, "Wrong password or username", Toast.LENGTH_SHORT).show();
                                    }}
                                else{
                                    // Tên người dùng không tồn tại
                                    Toast.makeText(MainActivity.this, "Wrong password or username", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Lỗi truy cập Firestore
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
        buttontextsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, signup_activity.class);
                startActivity(intent);
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
                            Toast.makeText(MainActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
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

