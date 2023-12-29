package com.example.todolistapp;

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
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.todolistapp.Adapter.ToDoAdapter;
import com.example.todolistapp.Model.ToDoModel;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.button.MaterialButton;

import java.security.PrivateKey;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnDialogCloseListener {
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firestore;
    private ToDoAdapter toDoAdapter;
    private List<ToDoModel> mylist;
    private SearchView searchView;
    private Query query;
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
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);

                        mylist.add(toDoModel);
                        toDoAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();

            }
        });
    }

    private boolean isToday(Date date) {
        // Lấy ngày hiện tại
        Date today = new Date();
        // So sánh ngày của timestamp với ngày hiện tại
        return android.text.format.DateUtils.isToday(date.getTime());
    }

    // Hàm lấy timestamp của ngày hiện tại
    private Date getCurrentDateTimestamp() {
        return new Date();
    }
    private void displayNotification() {
        query = firestore.collection("task");
        CollectionReference collectionRef = firestore.collection("task");
        Query query = collectionRef.whereEqualTo("time", getCurrentDateTimestamp());

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()){
                    DocumentSnapshot document = dc.getDocument();
                    if (isToday(document.getDate("time"))) {
                        // In ra thông báo hoặc thực hiện hành động bạn mong muốn ở đây
                        // Ví dụ:
                        String data = document.getString("task");
                        // Hiển thị thông báo
                        showNotification(data);
                    }
                }
            }
        });
    }
    private void showNotification (String taskName){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Your task is about to expire")
                .setContentText(taskName + "is expire today !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mNotificationMgr.notify(1, mBuilder.build());
    }
    @Override
    public void onDialogCLose(DialogInterface dialogInterface) {
        mylist.clear();
        showData();
        toDoAdapter.notifyDataSetChanged();
    }
}