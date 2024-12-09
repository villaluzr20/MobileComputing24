package com.example.fitnessapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProgressReportActivity extends AppCompatActivity {
    private TextView txtTotalWorkouts;
    private TextView txtTotalCaloriesConsumed;
    private TextView txtAverageWorkoutDuration;
    private TextView txtAverageCaloriesPerDay;
    private TextView txtGoalProgress;
    private ProgressBar progressBarFitnessGoal;
    private FitnessDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_report);

        // Initialize database helper
        databaseHelper = new FitnessDatabaseHelper(this);

        // Initialize UI components
        initializeViews();

        // Generate report
        generateWeeklyProgressReport();
    }

    private void initializeViews() {
        txtTotalWorkouts = findViewById(R.id.txtTotalWorkouts);
        txtTotalCaloriesConsumed = findViewById(R.id.txtTotalCaloriesConsumed);
        txtAverageWorkoutDuration = findViewById(R.id.txtAverageWorkoutDuration);
        txtAverageCaloriesPerDay = findViewById(R.id.txtAverageCaloriesPerDay);
        txtGoalProgress = findViewById(R.id.txtGoalProgress);
        progressBarFitnessGoal = findViewById(R.id.progressBarFitnessGoal);
    }

    private void generateWeeklyProgressReport() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String endDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String startDate = dateFormat.format(calendar.getTime());

        // Get workouts
        List<Workout> workouts = databaseHelper.getWorkouts(startDate, endDate);
        int totalWorkouts = workouts.size();
        int totalDuration = 0;
        for (Workout workout : workouts) {
            totalDuration += workout.getDuration();
        }
        int avgWorkoutDuration = totalWorkouts > 0 ? totalDuration / totalWorkouts : 0;

        // Get calories
        List<CalorieEntry> calorieEntries = databaseHelper.getCalorieEntries(startDate, endDate);
        int totalCalories = 0;
        for (CalorieEntry entry : calorieEntries) {
            totalCalories += entry.getCaloriesConsumed();
        }
        int avgCalories = calorieEntries.size() > 0 ? totalCalories / calorieEntries.size() : 0;

        // Calculate progress
        int progress = calculateProgress(totalWorkouts, avgWorkoutDuration, avgCalories);

        // Update UI
        updateUI(totalWorkouts, totalCalories, avgWorkoutDuration, avgCalories, progress);
    }

    private int calculateProgress(int totalWorkouts, int avgDuration, int avgCalories) {
        int workoutGoal = 5; // 5 workouts per week
        int durationGoal = 45; // 45 minutes per workout
        int calorieGoal = 2000; // 2000 calories per day

        int workoutProgress = Math.min((totalWorkouts * 100) / workoutGoal, 100);
        int durationProgress = Math.min((avgDuration * 100) / durationGoal, 100);
        int calorieProgress = Math.min((avgCalories * 100) / calorieGoal, 100);

        return (workoutProgress + durationProgress + calorieProgress) / 3;
    }

    private void updateUI(int totalWorkouts, int totalCalories, int avgDuration,
                          int avgCalories, int progress) {
        txtTotalWorkouts.setText("Total Workouts: " + totalWorkouts);
        txtTotalCaloriesConsumed.setText("Total Calories: " + totalCalories);
        txtAverageWorkoutDuration.setText("Avg Duration: " + avgDuration + " min");
        txtAverageCaloriesPerDay.setText("Avg Calories: " + avgCalories);
        txtGoalProgress.setText("Progress: " + progress + "%");
        progressBarFitnessGoal.setProgress(progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}