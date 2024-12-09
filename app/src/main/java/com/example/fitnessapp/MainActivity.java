package com.example.fitnessapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button btnWorkoutLog;
    private Button btnCalorieTracker;
    private Button btnProgressReport;
    private TextView txtCurrentDate;
    private TextView txtTodayWorkouts;
    private TextView txtTodayCalories;
    private FitnessDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        databaseHelper = new FitnessDatabaseHelper(this);

        // Initialize UI components
        btnWorkoutLog = findViewById(R.id.btnWorkoutLog);
        btnCalorieTracker = findViewById(R.id.btnCalorieTracker);
        btnProgressReport = findViewById(R.id.btnProgressReport);
        txtCurrentDate = findViewById(R.id.txtCurrentDate);
        txtTodayWorkouts = findViewById(R.id.txtTodayWorkouts);
        txtTodayCalories = findViewById(R.id.txtTodayCalories);

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        txtCurrentDate.setText(dateFormat.format(new Date()));

        // Set click listeners
        setupClickListeners();

        // Start Exercise Reminder Service
        startService(new Intent(this, ExerciseReminderService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTodaySummary();
    }

    private void setupClickListeners() {
        btnWorkoutLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WorkoutLogActivity.class));
            }
        });

        btnCalorieTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalorieTrackerActivity.class));
            }
        });

        btnProgressReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProgressReportActivity.class));
            }
        });
    }

    private void updateTodaySummary() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());

        // Get today's workouts
        StringBuilder workoutSummary = new StringBuilder("Today's Workouts:\n");
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query workouts
        String workoutQuery = "SELECT " + FitnessDatabaseHelper.COLUMN_WORKOUT_TYPE +
                ", " + FitnessDatabaseHelper.COLUMN_WORKOUT_DURATION +
                " FROM " + FitnessDatabaseHelper.TABLE_WORKOUTS +
                " WHERE " + FitnessDatabaseHelper.COLUMN_DATE + " = ?";

        try (Cursor workoutCursor = db.rawQuery(workoutQuery, new String[]{today})) {
            if (workoutCursor != null && workoutCursor.moveToFirst()) {
                do {
                    String type = workoutCursor.getString(workoutCursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_WORKOUT_TYPE));
                    int duration = workoutCursor.getInt(workoutCursor.getColumnIndexOrThrow(FitnessDatabaseHelper.COLUMN_WORKOUT_DURATION));
                    workoutSummary.append("• ").append(type).append(" (").append(duration).append(" mins)\n");
                } while (workoutCursor.moveToNext());
            } else {
                workoutSummary.append("No workouts logged today");
            }
        }

        // Query calories
        String calorieQuery = "SELECT SUM(" + FitnessDatabaseHelper.COLUMN_CALORIES_CONSUMED +
                ") as total FROM " + FitnessDatabaseHelper.TABLE_CALORIES +
                " WHERE " + FitnessDatabaseHelper.COLUMN_DATE + " = ?";

        try (Cursor calorieCursor = db.rawQuery(calorieQuery, new String[]{today})) {
            String calorieSummary;
            if (calorieCursor != null && calorieCursor.moveToFirst() && !calorieCursor.isNull(0)) {
                int totalCalories = calorieCursor.getInt(0);
                calorieSummary = "Total Calories Today: " + totalCalories;
            } else {
                calorieSummary = "No calories logged today";
            }
            txtTodayCalories.setText(calorieSummary);
        }

        txtTodayWorkouts.setText(workoutSummary.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}