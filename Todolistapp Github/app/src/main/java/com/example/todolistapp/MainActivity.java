package com.example.todolistapp;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private FirebaseFirestore firestore;
    private EditText editTextpw;
    private EditText editTextUsername;
    private TextView buttontextsignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
        editTextpw = findViewById(R.id.editTextpassword);
        editTextUsername = findViewById(R.id.editTextusername);
        button = findViewById(R.id.buttonlogin);
        buttontextsignup = findViewById(R.id.buttontextsignup);
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextpw.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Can't be left blank ", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> taskMap1 = new HashMap<>();
                    taskMap1.put("username", username);
                    taskMap1.put("password", password);
                    firestore.collection("UserID").add(taskMap1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }


            }


        })*/;
        buttontextsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, signup_activity.class);
                startActivity(intent);
            }
        });
    }
}