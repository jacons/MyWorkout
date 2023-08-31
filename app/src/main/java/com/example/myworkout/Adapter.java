package com.example.myworkout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public final Context context;
    public final int m_cycle; // Number of Micro-cycles
    public final String id_program; // Unique id workout
    public final DBHandler dbHandler;
    private final ArrayList<wrapper_exercise> list_exercises;

    Adapter(Context context, Workout<wrapper_exercise> workout) {

        this.context = context;
        this.list_exercises = workout.exercises;
        this.id_program = workout.id_program;
        this.m_cycle = workout.exercises.get(0).getNCycle();

        this.dbHandler = new DBHandler(context);
    }

    MCycle add_cycle(Context c, int count, LinearLayout parser) {

        LinearLayout ll_cycle = new LinearLayout(c);
        ll_cycle.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ll_cycle.setOrientation(LinearLayout.HORIZONTAL);

        TextView num_week = new TextView(c);
        num_week.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        num_week.setText("Week " + (count + 1));
        num_week.setTextSize(15);
        num_week.setTextColor(Color.BLACK);
        num_week.setGravity(Gravity.CENTER);
        ll_cycle.addView(num_week);

        TextView series_ = new TextView(c);
        series_.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        series_.setTextSize(15);
        series_.setGravity(Gravity.CENTER);
        series_.setText(R.string.series);
        ll_cycle.addView(series_);

        TextView series_value = new TextView(c);
        series_value.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        series_.setTextSize(15);
        series_value.setTextColor(Color.BLACK);
        series_value.setGravity(Gravity.CENTER);
        ll_cycle.addView(series_value);

        TextView reps_ = new TextView(c);
        reps_.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        reps_.setTextSize(15);
        reps_.setGravity(Gravity.CENTER);
        reps_.setText(R.string.reps);
        ll_cycle.addView(reps_);

        TextView reps_value = new TextView(c);
        reps_value.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        reps_.setTextSize(15);
        reps_value.setTextColor(Color.BLACK);
        reps_value.setGravity(Gravity.CENTER);
        ll_cycle.addView(reps_value);

        EditText load_value = new EditText(c);
        load_value.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f));
        load_value.setTextSize(12);
        load_value.setTextColor(Color.BLACK);
        load_value.setGravity(Gravity.CENTER);
        ll_cycle.addView(load_value);

        parser.addView(ll_cycle);
        return new MCycle(series_value, reps_value, load_value);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_exercise, parent, false);
        return new ViewHolder(v, dbHandler, id_program, m_cycle);
    }

    @Override
    public int getItemCount() {
        return list_exercises.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        wrapper_exercise wrapper = list_exercises.get(position);

        holder.name.setText(wrapper.getName());
        holder.notes.setText(wrapper.getNotes());
        holder.day.setText(wrapper.getDay());
        holder.time.setText(wrapper.getTime());
        holder.id_exercise = String.valueOf(position);

        int idx = 0;
        for (MCycle c : holder.list_m_cycle) {
            c.series.setText(wrapper.getSeries(idx));
            c.reps.setText(wrapper.getReps(idx));
            c.load.setText(wrapper.getLoad(idx));
            idx++;
        }
    }

    public static class MCycle {
        public final TextView series, reps;
        public final EditText load;

        MCycle(TextView series, TextView reps, EditText load) {
            this.series = series;
            this.reps = reps;
            this.load = load;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public final DBHandler dbHandler;
        public final String id_program;
        public String id_exercise;
        public TextView name, notes, day, time;
        public MCycle[] list_m_cycle;

        public ViewHolder(@NonNull View itemView, DBHandler dbHandler, String id_program, int m_cycle) {

            super(itemView);
            this.dbHandler = dbHandler;
            this.id_program = id_program;


            name = itemView.findViewById(R.id.card_name_ex);
            name.setOnLongClickListener(this);
            notes = itemView.findViewById(R.id.card_notes);
            day = itemView.findViewById(R.id.card_day_of_week);
            time = itemView.findViewById(R.id.card_recovery_time);

            LinearLayout ll_m_cycles = itemView.findViewById(R.id.card_micro_cycles);
            list_m_cycle = new MCycle[m_cycle];
            for (int i = 0; i < m_cycle; i++) list_m_cycle[i] = add_cycle(context, i, ll_m_cycles);
        }

        @Override
        public boolean onLongClick(View view) {

            if (view.getId() == name.getId()) {
                try {
                    this.dbHandler.update_Mcycles(id_program, id_exercise, list_m_cycle);

                } catch (SQLiteException e) {
                    Toast.makeText(context, "Error during the updating! " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }
                Toast.makeText(context, "Information saved", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }
    }
}
