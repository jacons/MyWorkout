package com.example.myworkout;


import android.widget.EditText;
import android.widget.RadioGroup;


public class Exercise_form {

    private final EditText name, notes, time;
    private final EditText[][] m_cycles;
    private final RadioGroup radioDays;
    private String[][] cache = null;

    public Exercise_form(EditText name, EditText[][] m_cycles, RadioGroup radioDays, EditText time, EditText notes) {
        this.name = name;
        this.m_cycles = m_cycles;
        this.radioDays = radioDays;
        this.notes = notes;
        this.time = time;
    }

    public String[][] getMCycles() {

        if (this.cache == null) {

            String[][] result = new String[this.m_cycles.length][];

            int i = 0;
            for (EditText[] cycle : this.m_cycles)
                result[i++] = new String[]{
                        cycle[0].getText().toString().trim(),
                        cycle[1].getText().toString().trim()};

            this.cache = result;
        }

        return this.cache;
    }

    public String getName() {
        return name.getText().toString().trim();
    }

    public String getNotes() {
        return notes.getText().toString().trim();
    }

    public String getDay() {
        return String.valueOf(radioDays.getCheckedRadioButtonId());
    }

    public String getTime() {
        return time.getText().toString().trim();
    }


}
