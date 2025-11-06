package com.example.firebaseapp.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.firebaseapp.R;
import com.example.firebaseapp.notifications.TaskNotificationManager;

public class TaskNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "TaskNotificationReceiver";
    private static final String CHANNEL_ID = "task_notifications_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskId = intent.getStringExtra("taskId");
        String taskTitle = intent.getStringExtra("taskTitle");
        String clubName = intent.getStringExtra("clubName");

        if (taskId == null || taskTitle == null || clubName == null) {
            Log.e(TAG, "Brakujące informacje o zadaniu");
            return;
        }

        // Upewnij się, że kanał powiadomień jest utworzony
        TaskNotificationManager.createNotificationChannel(context);

        String contentText = "Zadanie: " + taskTitle + " w klubie " + clubName + " jest do wykonania.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.woman_home) // Użyj monochromatycznej ikony
                .setContentTitle("Przypomnienie o zadaniu")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = ("task_" + taskId).hashCode();
        notificationManager.notify(notificationId, builder.build());

        Log.d(TAG, "Powiadomienie wysłane dla zadania: " + taskId);
    }
}
