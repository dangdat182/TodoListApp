package com.example.todolistapp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.AddNewTask;
import com.example.todolistapp.R;
import com.example.todolistapp.HomeActivity;
import com.example.todolistapp.Model.ToDoModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> todolist;
    private HomeActivity activity;
    private FirebaseFirestore firestore;
    private List<ToDoModel> originalList;
    public ToDoAdapter(HomeActivity homeActivity, List<ToDoModel> todolist){
        this.todolist = todolist;
        this.originalList = new ArrayList<>(todolist);
        activity = homeActivity;
    }
    public void filterList(List<ToDoModel> filteredList) {
        todolist = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.activity_task, parent, false);
        firestore = FirebaseFirestore.getInstance();

        return new MyViewHolder(view);
    }
    public void deleteTask(int position){
        ToDoModel toDoModel = todolist.get(position);
        firestore.collection("task").document(toDoModel.TaskId).delete();
        todolist.remove(position);
        notifyItemRemoved(position);
    }
    public Context getContext() {
        return activity;
    }
    public void editTask(int position){
        ToDoModel toDoModel = todolist.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("task" , toDoModel.getTask());
        bundle.putString("due" , toDoModel.getDue());
        bundle.putString("dueTime",toDoModel.getDueTime());
        bundle.putString("id" , toDoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDoModel toDoModel = todolist.get(position);
        holder.checkbox.setText(toDoModel.getTask());
        holder.DueDate.setText("Due on: " + toDoModel.getDue());
        holder.DueTime.setText("On: " + toDoModel.getDueTime()); // Đặt văn bản giờ đáo hạn
        holder.checkbox.setChecked(toBoolean(toDoModel.getStatus()));

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 1);
                }
                else{
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 0);
                }
            }
        });
    }
    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {

        return todolist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView DueDate, DueTime;
        CheckBox checkbox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            DueDate = itemView.findViewById(R.id.textviewdate);
            checkbox = itemView.findViewById(R.id.checkbox);
            DueTime = itemView.findViewById(R.id.textviewtime);
        }
    }
}