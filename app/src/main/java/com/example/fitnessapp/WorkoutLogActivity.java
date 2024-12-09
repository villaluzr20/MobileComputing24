package com.example.fitnessapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutLogActivity extends AppCompatActivity {
    private EditText editWorkoutDuration;
    private Spinner spinnerWorkoutType;
    private Button btnSaveWorkout;
    private FitnessDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);

        databaseHelper = new FitnessDatabaseHelper(this);

        editWorkoutDuration = findViewById(R.id.editWorkoutDuration);
        spinnerWorkoutType = findViewById(R.id.spinnerWorkoutType);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.workout_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkoutType.setAdapter(adapter);

        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkoutLog();
            }
        });
    }

    private void saveWorkoutLog() {
        String workoutType = spinnerWorkoutType.getSelectedItem().toString();
        String durationStr = editWorkoutDuration.getText().toString().trim();

        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Please enter workout duration", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            long result = databaseHelper.insertWorkout(currentDate, workoutType, duration);

            if (result != -1) {
                Toast.makeText(this, "Workout logged successfully", Toast.LENGTH_SHORT).show();
                editWorkoutDuration.setText("");
                spinnerWorkoutType.setSelection(0);
            } else {
                Toast.makeText(this, "Failed to log workout", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
