package com.example.todolistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import com.example.todolistapp.Adapter.ToDoAdapter;
import com.example.todolistapp.Model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.button.MaterialButton;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnDialogCloseListener{
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firestore;
    private ToDoAdapter toDoAdapter;
    private List<ToDoModel> mylist;
    private Query query;
    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_home);
        //actionBar.setTitle("Todo list");
        recyclerView = findViewById(R.id.recyclerview);
        floatingActionButton = findViewById(R.id.buttonaddnewtask);
        firestore = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        MaterialButton viewProfileButton = findViewById(R.id.buttonViewProfile);


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
    private void showData() {
        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING );
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

    @Override
    public void onDialogCLose(DialogInterface dialogInterface) {
        mylist.clear();
        showData();
        toDoAdapter.notifyDataSetChanged();
    }



}