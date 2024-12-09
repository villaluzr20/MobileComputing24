package com.example.fitnessapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class ExerciseReminderService extends Service {
    private static final String CHANNEL_ID = "FitnessReminderChannel";
    private static final String CHANNEL_NAME = "Exercise Reminders";
    private static final String CHANNEL_DESCRIPTION = "Notifications for exercise reminders";
    private static final int NOTIFICATION_ID = 1001;
    private static final long REMINDER_INTERVAL = AlarmManager.INTERVAL_DAY; // Daily reminder

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        scheduleReminder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("SHOW_REMINDER")) {
                showReminderNotification();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void scheduleReminder() {
        Intent intent = new Intent(this, ExerciseReminderService.class);
        intent.setAction("SHOW_REMINDER");

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                flags
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Set reminder time to 8:00 AM
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // If it's already past 8:00 AM, schedule for next day
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }

    private void showReminderNotification() {
        
        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                flags
        );

       
        Intent logWorkoutIntent = new Intent(this, WorkoutLogActivity.class);
        logWorkoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent logWorkoutPendingIntent = PendingIntent.getActivity(
                this,
                1,
                logWorkoutIntent,
                flags
        );

       
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_workout)
                .setContentTitle("Time to Exercise!")
                .setContentText("Don't forget your daily workout routine")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_workout, "Log Workout", logWorkoutPendingIntent);

        
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

      
        scheduleReminder();
    }

    public static void startReminders(Context context) {
        Intent serviceIntent = new Intent(context, ExerciseReminderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    public static void stopReminders(Context context) {
        Intent serviceIntent = new Intent(context, ExerciseReminderService.class);
        context.stopService(serviceIntent);

        
        Intent intent = new Intent(context, ExerciseReminderService.class);
        intent.setAction("SHOW_REMINDER");

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                flags
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
