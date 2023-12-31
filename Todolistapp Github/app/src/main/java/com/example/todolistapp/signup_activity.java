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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
public class signup_activity extends AppCompatActivity {
    private EditText editTextusername , editTextpassword, editTextcomfirmps , editTextFullname;
    private Button buttonsignup, buttonsigningg;
    private FirebaseFirestore firestore;
    private String collectionName = "UserID";
    private boolean usernameExists = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firestore = FirebaseFirestore.getInstance();

        editTextFullname = findViewById(R.id.editTextFullname);
        editTextusername = findViewById(R.id.editTextusername1);
        editTextpassword = findViewById(R.id.editTextpassword1);
        editTextcomfirmps = findViewById(R.id.editTextcomfirmpassword);
        buttonsignup = findViewById(R.id.buttonsignup);


        buttonsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextusername.getText().toString();
                String password = editTextpassword.getText().toString();
                String cfpassword = editTextcomfirmps.getText().toString();
                String Fullname = editTextFullname.getText().toString();
                if (Fullname.isEmpty() || username.isEmpty() || password.isEmpty() || cfpassword.isEmpty()){
                    Toast.makeText(signup_activity.this, "Can't be left blank ", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<5){
                    Toast.makeText(signup_activity.this, "Password at least 5 characters", Toast.LENGTH_SHORT).show();
                }
                /*else if (password.length() >= 5) {
                    CollectionReference collectionRef = firestore.collection(collectionName);
                    collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String returnUsername = document.get("username").toString();
                                    if (returnUsername != null && returnUsername.equalsIgnoreCase(username)) {
                                        usernameExists = true;
                                        break; // Kết thúc vòng lặp khi tìm thấy một tài liệu thỏa mãn điều kiện
                                    }
                                }

                                if (usernameExists == true) {
                                    Toast.makeText(getApplicationContext(), "Username exists", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(signup_activity.this, "Error: Can't get data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }*/

                else{
                    firestore.collection("UserID")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(signup_activity.this, "username already exists", Toast.LENGTH_SHORT).show();
                                }

                                else if (password.equals(cfpassword)) {
                                Map<String, Object> taskMap1 = new HashMap<>();
                                taskMap1.put("username", username);
                                taskMap1.put("password", password);
                                taskMap1.put("Fullname", Fullname);
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
                                intent.setClass(signup_activity.this, MainActivity.class);
                                startActivity(intent);
                                } else {
                                Toast.makeText(signup_activity.this, " Confirm password must be as same as password", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
    });
}}