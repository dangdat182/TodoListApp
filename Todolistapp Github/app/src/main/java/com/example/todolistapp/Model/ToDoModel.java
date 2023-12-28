package com.example.todolistapp.Model;

import java.sql.Timestamp;

public class ToDoModel extends TaskID{
    private String task, due, dueTime;
    private int status;


    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }
    public String getDueTime() {
        return dueTime;
    }

}
