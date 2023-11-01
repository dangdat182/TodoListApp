package com.example.todolistapp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class TaskID {
    @Exclude
    public String TaskId;
    public <T extends TaskID> T withId(@NonNull final String id){
        this.TaskId = id;
        return (T) this;
    }
}

