package com.example.fitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class FitnessDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FitnessAppDatabase";
    private static final int DATABASE_VERSION = 1;

  
    public static final String TABLE_WORKOUTS = "workouts";
    public static final String TABLE_CALORIES = "calories";

    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";

    
    public static final String COLUMN_WORKOUT_TYPE = "type";
    public static final String COLUMN_WORKOUT_DURATION = "duration";

  
    public static final String COLUMN_CALORIES_CONSUMED = "calories_consumed";

    
    private static final String CREATE_WORKOUT_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_WORKOUT_TYPE + " TEXT, "
            + COLUMN_WORKOUT_DURATION + " INTEGER)";

    private static final String CREATE_CALORIES_TABLE = "CREATE TABLE " + TABLE_CALORIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_CALORIES_CONSUMED + " INTEGER)";

    public FitnessDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUT_TABLE);
        db.execSQL(CREATE_CALORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALORIES);
        onCreate(db);
    }

    public List<Workout> getWorkouts(String startDate, String endDate) {
        List<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_DATE, COLUMN_WORKOUT_TYPE, COLUMN_WORKOUT_DURATION};
        String selection = COLUMN_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};

        try (Cursor cursor = db.query(TABLE_WORKOUTS, columns, selection, selectionArgs,
                null, null, COLUMN_DATE + " DESC")) {

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE);
                int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_TYPE);
                int durationIndex = cursor.getColumnIndexOrThrow(COLUMN_WORKOUT_DURATION);

                do {
                    Workout workout = new Workout(
                            cursor.getInt(idIndex),
                            cursor.getString(dateIndex),
                            cursor.getString(typeIndex),
                            cursor.getInt(durationIndex)
                    );
                    workouts.add(workout);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return workouts;
    }

    public List<CalorieEntry> getCalorieEntries(String startDate, String endDate) {
        List<CalorieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_DATE, COLUMN_CALORIES_CONSUMED};
        String selection = COLUMN_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};

        try (Cursor cursor = db.query(TABLE_CALORIES, columns, selection, selectionArgs,
                null, null, COLUMN_DATE + " DESC")) {

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE);
                int caloriesIndex = cursor.getColumnIndexOrThrow(COLUMN_CALORIES_CONSUMED);

                do {
                    CalorieEntry entry = new CalorieEntry(
                            cursor.getInt(idIndex),
                            cursor.getString(dateIndex),
                            cursor.getInt(caloriesIndex)
                    );
                    entries.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entries;
    }

    
    public long insertWorkout(String date, String type, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_WORKOUT_TYPE, type);
        values.put(COLUMN_WORKOUT_DURATION, duration);
        return db.insert(TABLE_WORKOUTS, null, values);
    }

    public long insertCalorieEntry(String date, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_CALORIES_CONSUMED, calories);
        return db.insert(TABLE_CALORIES, null, values);
    }
}
