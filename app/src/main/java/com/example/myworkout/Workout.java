package com.example.myworkout;

import java.util.ArrayList;

public class Workout<T> {

    final String name;
    ArrayList<T> exercises;
    public String date;
    String id_program;

    public Workout(String name) {
        this.name = name;
        this.exercises = new ArrayList<>();
    }

    public Workout(String name, String date, String id_program) {
        this.name = name;
        this.date = date;
        this.id_program = id_program;
        this.exercises = new ArrayList<>();
    }

    public void add(T e) {
        this.exercises.add(e);
    }
}
