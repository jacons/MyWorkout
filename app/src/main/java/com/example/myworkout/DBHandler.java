package com.example.myworkout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {


    // Creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, "gymexercises_db", null, Integer.parseInt("4"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Build a database tables and relations

        // Used to keep into account the different workouts
        String workouts = " CREATE TABLE workouts (" +
                "id_program INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(50) NOT NULL ," +
                "date TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP);";

        // For each workout we have a set of exercises
        String exercises = " CREATE TABLE exercises (" +
                "id_program INTEGER," +
                "id_exercise INTEGER," +
                "ordering INTEGER," +               // Ordering
                "name VARCHAR(100) NOT NULL ," +    // Name of exercise
                "day INTEGER NOT NULL," +           // Day 1-6 Lun-Sat
                "time TEXT NOT NULL ," +            // Time among the series
                "notes TEXT NOT NULL ," +           // Notes just in case
                "PRIMARY KEY (id_program, id_exercise));";

        // For each exercise we have a sequence of week with different series,reps and load
        String microcycles = "CREATE TABLE mcycles(" +
                "id_program INTEGER," +
                "id_exercise INTEGER," +
                "week INTEGER," +
                "series INTEGER NOT NULL ," +       // Number of series
                "reps TEXT NOT NULL ," +            // Number of repetitions
                "load TEXT NOT NULL ," +            // Load typically in Kg
                "PRIMARY KEY (id_program, id_exercise, week));";


        db.execSQL(workouts);
        db.execSQL(exercises);
        db.execSQL(microcycles);

    }

    /**
     * Check if a given workout name is already present, return true if present
     * false otherwise
     */
    public boolean checkWorkoutExist(String workout_name) throws SQLiteException {

        if (workout_name.equals("")) return false;

        boolean flag;
        String sql_query = "SELECT COUNT(*) FROM workouts WHERE name == ?;";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql_query, new String[]{workout_name});

        flag = false;
        if (c.moveToFirst()) do {
            flag = c.getInt(0) > 0;
        }
        while (c.moveToNext());

        db.close();
        c.close();

        return flag;
    }

    /**
     * Add new workout
     */
    public void addNewWorkout(Workout<Exercise_form> w) throws SQLiteException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", w.name);
        // It adds new workout and retrieves the id, used in the next queries
        long id_workout = db.insert("workouts", null, values);

        int id_exercise = 0, id_cycle;

        for (Exercise_form e : w.exercises) {

            // Adding exercise
            values.clear();
            values.put("id_program", id_workout);
            values.put("id_exercise", id_exercise);
            values.put("ordering", id_exercise);
            values.put("name", e.getName());
            values.put("day", e.getDay());
            values.put("time", e.getTime());
            values.put("notes", e.getNotes());
            db.insert("exercises", null, values);

            // For each exercise adding a cycle
            id_cycle = 0;
            for (String[] cycle : e.getMCycles()) {
                values.clear();
                values.put("id_program", id_workout);
                values.put("id_exercise", id_exercise);
                values.put("week", id_cycle++);
                values.put("series", cycle[0]);
                values.put("reps", cycle[1]);
                values.put("load", "");
                db.insert("mcycles", null, values);
            }
            id_exercise++;
        }

        db.close();
    }

    /**
     * Get the last workout
     */
    public Workout<wrapper_exercise> get_last_workout() throws SQLiteException {

        SQLiteDatabase db = this.getReadableDatabase();

        String id_program = "", name = "", date = "", time, notes, series, reps, load;
        int day, id_exercise;

        String sql_query = "SELECT id_program,name,date FROM workouts ORDER BY id_program DESC LIMIT 1";
        Cursor c = db.rawQuery(sql_query, null);

        if (c.moveToFirst()) do {
            id_program = String.valueOf(c.getInt(0));
            name = c.getString(1);
            date = c.getString(2);

        } while (c.moveToNext());
        c.close();

        // Check values
        if (name.equals("") | date.equals("") | id_program.equals(""))
            throw new SQLiteException();

        Workout<wrapper_exercise> workout = new Workout<>(name, date, id_program);

        sql_query = "SELECT name, day, time, notes FROM exercises WHERE id_program == ? ORDER BY id_exercise ASC";
        c = db.rawQuery(sql_query, new String[]{id_program});

        if (c.moveToFirst()) do {

            name = c.getString(0);
            day = c.getInt(1);
            time = c.getString(2);
            notes = c.getString(3);
            workout.add(new wrapper_exercise(name, notes, day, time));

        } while (c.moveToNext());
        c.close();

        sql_query = "SELECT id_exercise, series, reps, load FROM mcycles WHERE id_program == ? ORDER BY id_exercise, week ASC";
        c = db.rawQuery(sql_query, new String[]{id_program});

        if (c.moveToFirst()) do {

            id_exercise = c.getInt(0);
            series = c.getString(1);
            reps = c.getString(2);
            load = c.getString(3);

            workout.exercises.get(id_exercise).add_cycle(series, reps, load);

        } while (c.moveToNext());

        c.close();
        db.close();

        return workout;
    }

    public void update_Mcycles(String id_program, String id_exercise, Adapter.MCycle[] cycles)
            throws SQLiteException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        int i = 0, row_affected = 0;
        for (Adapter.MCycle m : cycles) {
            values.clear();
            values.put("load", m.load.getText().toString().trim());

            row_affected += db.update("mcycles", values, "id_program=? AND id_exercise=? AND week=?",
                    new String[]{id_program, id_exercise, String.valueOf(i++)});
        }
        if (row_affected != i) throw new SQLiteException();


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS workouts");
        db.execSQL("DROP TABLE IF EXISTS exercises");
        db.execSQL("DROP TABLE IF EXISTS mcycles");
        onCreate(db);
    }
}