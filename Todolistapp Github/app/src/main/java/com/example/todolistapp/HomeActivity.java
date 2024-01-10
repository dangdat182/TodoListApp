package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.todolistapp.Adapter.ToDoAdapter;
import com.example.todolistapp.Model.ToDoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.button.MaterialButton;

import java.security.PrivateKey;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements OnDialogCloseListener {
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firestore;
    private ToDoAdapter toDoAdapter;
    private List<ToDoModel> mylist;
    private SearchView searchView;
    private Query query;
    SharedPreferences sharedPreferences;
    public static String CurrentUID;
    private static final String SHARED_PREF_NAME = "MyPref";
    private static final String KEY_UID = "123";
    private ListenerRegistration listenerRegistration;
    private ImageButton buttonsort, viewProfileButton;
    private static final String CHANNEL_ID = "ToDoApp_CID";
    private static final String CHANNEL_NAME = "ToDoApp_CNAME";
    private static final String CHANNEL_DESC = "ToDoApp_DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_home);
        //actionBar.setTitle("Todo list");
        recyclerView = findViewById(R.id.recyclerview);
        floatingActionButton = findViewById(R.id.buttonaddnewtask);
        buttonsort = findViewById(R.id.buttonsort);
        searchView = findViewById(R.id.buttonsearch);
        firestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        viewProfileButton = findViewById(R.id.buttonViewProfile);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        CurrentUID = sharedPreferences.getString(KEY_UID,null);
        Log.d("Test","Home Activity: "+ CurrentUID);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
        mylist = new ArrayList<>();
        toDoAdapter = new ToDoAdapter(HomeActivity.this, mylist);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(toDoAdapter);
        displayNotification();
    }

    @Override
    protected void onStart() {
        //displayNotification();
        super.onStart();
    }


    private void filterTasks(String query) {
        List<ToDoModel> filteredList = new ArrayList<>();

        for (ToDoModel task : mylist) {
            if (task.getTask().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(task);
            }
        }
        toDoAdapter.filterList(filteredList);
    }

    private void showData() {
        query = firestore.collection("task")
                .orderBy("time", Query.Direction.DESCENDING);
                //.whereEqualTo("UserID",CurrentUID);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        if (documentChange.getDocument().getString("UserID").equals(CurrentUID)) {
                            String id = documentChange.getDocument().getId();
                            ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                            mylist.add(toDoModel);
                            toDoAdapter.notifyDataSetChanged();
                        }
                    }
                }
                listenerRegistration.remove();

            }
        });
    }

    private int getNotificationId(){
        return (int) new Date().getTime();
    }
    private String getCurrentDate(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault());
            String dateString = currentDateTime.format(formatter);
            return dateString;
        }
        return null;
    }
    private void displayNotification() {
        firestore.collection("task")
                .whereEqualTo("due",getCurrentDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if(documentSnapshot.getString("UserID").equals(CurrentUID)) {
                                    String taskName = documentSnapshot.getString("task");
                                    showNotificationExpiresTask(taskName);
                                }
                            }
                    }
                });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault());
        }
        firestore.collection("task")
                .whereNotEqualTo("due",getCurrentDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if(documentSnapshot.getString("UserID").equals(CurrentUID)) {
                                    String taskDate = documentSnapshot.getString("due");
                                    String localDate = getCurrentDate();
                                    if (CheckDate(taskDate, localDate) == false) {
                                        String taskName = documentSnapshot.getString("task");
                                        showNotificationExpiredTask(taskName);
                                    }
                                }
                            }
                        }
                    }
                });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private boolean CheckDate(String dateString1 , String dateString2){
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        try {
            // Chuyển đổi chuỗi thành kiểu Date
            Date date1 = dateFormat.parse(dateString1);
            Date date2 = dateFormat.parse(dateString2);
            if(date1.after(date2)){
                return true;
            }
            if(date1.before(date2)){
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showNotificationExpiresTask (String taskName){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("EXPIRING TASK: ")
                .setContentText(taskName + " expires today !")
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(getNotificationId(), mBuilder.build());
    }
    private void showNotificationExpiredTask (String taskName){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("EXPIRED TASK: ")
                .setContentText(taskName + "  has expired!")
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(getNotificationId(), mBuilder.build());
    }
    @Override
    public void onDialogCLose(DialogInterface dialogInterface) {
        mylist.clear();
        showData();
        toDoAdapter.notifyDataSetChanged();
    }
}