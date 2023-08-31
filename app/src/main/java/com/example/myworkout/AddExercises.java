package com.example.myworkout;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.myworkout.Utils.dpToPx;

import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddExercises extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Exercise_form> exercise_forms;
    private LinearLayout ll_exercises;
    private EditText m_cycles; // EditText to set the number of micro-cycles to have
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercises);

        this.ll_exercises = findViewById(R.id.list_exercises);
        this.m_cycles = findViewById(R.id.micro_cycles);

        Button addExercise = findViewById(R.id.add_exercise);
        addExercise.setOnClickListener(this);

        Button save_workout = findViewById(R.id.save_workout);
        save_workout.setOnClickListener(this);

        this.exercise_forms = new ArrayList<>();
        this.dbHandler = new DBHandler(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.add_exercise) {
            this.exercise_forms.add(add_form());
        }
        if (id == R.id.save_workout) {
            this.save_workout();
        }
    }

    public void save_workout() {

        String workout_name;

        // Retrieve a workout_name
        workout_name = ((EditText) findViewById(R.id.workout_name)).getText().toString().trim();

        try {
            // Check first of all if there is another workout with the same name
            if (dbHandler.checkWorkoutExist(workout_name)) {
                Toast.makeText(getApplicationContext(), workout_name + " already exist!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), "Error with the database! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // workout object used to db updates
        Workout<Exercise_form> workout = new Workout<>(workout_name);

        // We retrieve the fields and check if they are null
        for (Exercise_form w : this.exercise_forms) {

            if (w.getName().equals("") | w.getDay().equals("")) {
                Toast.makeText(getApplicationContext(), "You must complete all information! (Exercise_form)", Toast.LENGTH_SHORT).show();
                return;
            }

            for (String[] cycle : w.getMCycles()) {
                if (cycle[0].equals("") | cycle[1].equals("")) {
                    Toast.makeText(getApplicationContext(), "You must complete all information! (Micro-cycles)", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            workout.add(w);
        }
        if (workout.exercises.size() < 1) {
            Toast.makeText(getApplicationContext(), "Add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            dbHandler.addNewWorkout(workout);
            finish();
        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), "Error with the database! " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    public Exercise_form add_form() {

        String tmp = this.m_cycles.getText().toString();
        int n_cycles = tmp.equals("") ? 1 : Math.min(Integer.parseInt(tmp), 8);

        this.m_cycles.setEnabled(false);

        LinearLayout form = new LinearLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        form.setPadding(dpToPx(this, 5), dpToPx(this, 5), dpToPx(this, 5), dpToPx(this, 5));
        params.setMargins(0, dpToPx(this, 10), 0, 0);
        form.setLayoutParams(params);
        form.setBackgroundResource(R.drawable.form_background);
        form.setOrientation(LinearLayout.VERTICAL);

        LinearLayout name_title = new LinearLayout(this);
        name_title.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        name_title.setOrientation(LinearLayout.HORIZONTAL);

        EditText name_ex = new EditText(this);
        name_ex.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 3f));
        name_ex.setHint(R.string.name_of_exercise);
        name_ex.setTextSize(15);

        TextView cycles = new TextView(this);
        cycles.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2f));
        cycles.setGravity(Gravity.END);
        cycles.setText(R.string.micro_cycles_weeks);

        name_title.addView(name_ex);
        name_title.addView(cycles);
        form.addView(name_title);

        EditText[][] list_cycles = new EditText[n_cycles][];
        for (int i = 0; i < n_cycles; i++) {

            LinearLayout ll_SeriesReps = new LinearLayout(this);
            ll_SeriesReps.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            ll_SeriesReps.setOrientation(LinearLayout.HORIZONTAL);


            TextView num = new TextView(this);
            num.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
            num.setText(String.valueOf(i + 1));
            num.setTextColor(Color.BLACK);
            num.setGravity(Gravity.CENTER);

            EditText series = new EditText(this);
            params = new LinearLayout.LayoutParams(0, dpToPx(this, 36), 1f);
            series.setLayoutParams(params);
            series.setInputType(+InputType.TYPE_CLASS_NUMBER);
            series.setHint(R.string.series);
            series.setTextSize(15);
            series.setGravity(Gravity.CENTER);
            series.setText("2");

            EditText reps = new EditText(this);
            reps.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(this, 36), 1f));
            reps.setHint(R.string.reps);
            reps.setTextSize(15);
            reps.setGravity(Gravity.CENTER);
            reps.setText("10");

            ll_SeriesReps.addView(num);
            ll_SeriesReps.addView(series);
            ll_SeriesReps.addView(reps);
            form.addView(ll_SeriesReps);

            list_cycles[i] = new EditText[]{series, reps};
        }

        LinearLayout ll_times = new LinearLayout(this);
        ll_times.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ll_times.setOrientation(LinearLayout.HORIZONTAL);


        TextView time_tv = new TextView(this);
        time_tv.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        time_tv.setText(R.string.time);


        EditText time = new EditText(this);
        time.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(this, 36), 1f));
        time.setHint(R.string.time);
        time.setText("1-2''");
        time.setTextSize(15);
        time.setGravity(Gravity.CENTER);


        ll_times.addView(time_tv);
        ll_times.addView(time);

        form.addView(ll_times);


        RadioGroup radioDays = new RadioGroup(this);
        radioDays.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        radioDays.setOrientation(LinearLayout.HORIZONTAL);
        radioDays.setGravity(Gravity.CENTER);

        int idx = 0;
        for (String d : new String[]{"Mo", "Tu", "We", "Th", "Fr", "Sa"}) {
            RadioButton radio = new RadioButton(this);
            if (idx == 0) radio.toggle();
            radio.setId(idx++);
            radio.setText(d);
            radio.setTextSize(13);
            radioDays.addView(radio);

        }
        form.addView(radioDays);

        EditText notes = new EditText(this);
        notes.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, dpToPx(this, 36)));
        notes.setHint(R.string.notes);
        notes.setTextSize(15);
        form.addView(notes);

        ll_exercises.addView(form);
        return new Exercise_form(name_ex, list_cycles, radioDays, time, notes);
    }

}
