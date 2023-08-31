package com.example.myworkout;

import java.util.ArrayList;
import java.util.Map;

public class wrapper_exercise {

    public static Map<Integer, String> id2day = Map.of(
            0, "Mon", 1, "Tue",
            2, "Wed", 3, "Thu",
            4, "Fri", 5, "Sat");
    private final String name, notes, time;
    private final ArrayList<String[]> m_cycle;
    private final int day;

    public wrapper_exercise(String name, String notes, int day, String time) {
        this.name = name;
        this.notes = notes;
        this.day = day;
        this.time = time;
        this.m_cycle = new ArrayList<>();
    }

    public void add_cycle(String series, String reps, String load) {
        m_cycle.add(new String[]{series, reps, load});
    }

    public String getName() {
        return this.name;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getDay() {
        return id2day.get(this.day);
    }

    public String getTime() {
        return this.time;
    }

    public String getSeries(int idx) {
        return m_cycle.get(idx)[0];
    }

    public String getReps(int idx) {
        return m_cycle.get(idx)[1];
    }

    public String getLoad(int idx) { return m_cycle.get(idx)[2]; }

    public int getNCycle() {
        return m_cycle.size();
    }
}
