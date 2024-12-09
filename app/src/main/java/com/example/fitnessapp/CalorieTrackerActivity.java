package com.example.fitnessapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalorieTrackerActivity extends AppCompatActivity {
    private EditText editCalorieIntake;
    private Button btnSaveCalories;
    private FitnessDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_tracker);

        // Initialize database helper
        databaseHelper = new FitnessDatabaseHelper(this);

        // Initialize UI components
        editCalorieIntake = findViewById(R.id.editCalorieIntake);
        btnSaveCalories = findViewById(R.id.btnSaveCalories);

        btnSaveCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCalorieIntake();
            }
        });
    }

    private void saveCalorieIntake() {
        try {
            String calorieStr = editCalorieIntake.getText().toString().trim();
            if (calorieStr.isEmpty()) {
                Toast.makeText(this, "Please enter calorie intake", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories = Integer.parseInt(calorieStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FitnessDatabaseHelper.COLUMN_DATE, currentDate);
            values.put(FitnessDatabaseHelper.COLUMN_CALORIES_CONSUMED, calories);

            long result = db.insert(FitnessDatabaseHelper.TABLE_CALORIES, null, values);

            if (result != -1) {
                Toast.makeText(this, "Calories logged successfully", Toast.LENGTH_SHORT).show();
                editCalorieIntake.setText("");
            } else {
                Toast.makeText(this, "Failed to log calories", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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