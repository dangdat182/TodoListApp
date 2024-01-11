package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private Button buttonlogout;
    private TextView profileEmail, profilename, doneCount, ongoingCount, outofdateCount;
    private ImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.view_profile);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        assert extras != null;
        int done = extras.getInt("done");
        int ongoing = extras.getInt("ongoing");
        int outofdate = extras.getInt("outofdate");
        buttonlogout = findViewById(R.id.buttonlogout);
        profileEmail = findViewById(R.id.profileEmail);
        profilename = findViewById(R.id.profileName);
        profileImage = findViewById(R.id.profileImage);
        doneCount = findViewById(R.id.doneCount);
        ongoingCount = findViewById(R.id.ongoingCount);
        outofdateCount = findViewById(R.id.outOfDateCount);
        doneCount.setText(String.valueOf(done));
        ongoingCount.setText(String.valueOf(ongoing));
        outofdateCount.setText(String.valueOf(outofdate));
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null){
            String Name = account.getDisplayName();
            String Email = account.getEmail();
            Uri PhotoURL = account.getPhotoUrl();

            Picasso.get().load(PhotoURL).into(profileImage);
            profilename.setText(Name);
            profileEmail.setText(Email);
        }
        buttonlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

    }

    private void logOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finishAllActivities();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void finishAllActivities() {
        // list all activity and finish them
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();

        for (ActivityManager.AppTask appTask : appTasks) {
            appTask.finishAndRemoveTask();
        }
    }
}
