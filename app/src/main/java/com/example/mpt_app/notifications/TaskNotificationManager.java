package com.example.mpt_app.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mpt_app.R;
import com.example.mpt_app.models.Task;
import com.example.mpt_app.receivers.TaskNotificationReceiver;

import java.util.Calendar;
import java.util.Date;

public class TaskNotificationManager {

    private static final String TAG = "TaskNotificationManager";
    private static final String CHANNEL_ID = "task_notifications_channel";
    private static final String CHANNEL_NAME = "Powiadomienia o zadaniach";
    private static final String CHANNEL_DESCRIPTION = "Powiadomienia przypominające o zadaniach";

    /**
     * Tworzy kanał powiadomień, jeśli wersja Androida jest Oreo lub nowsza.
     *
     * @param context Kontekst aplikacji.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESCRIPTION);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating notification channel", e);
            }
        }
    }

    /**
     * Wysyła natychmiastowe powiadomienie o zadaniu.
     *
     * @param context Kontekst aplikacji.
     * @param task    Zadanie, o którym ma być powiadomienie.
     */
    public static void sendImmediateNotification(Context context, Task task) {
        try {
            // Upewnij się, że kanał powiadomień jest utworzony
            createNotificationChannel(context);

            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("clubName", task.getClubName());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.getId().hashCode(), // Unikalny request code
                    intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.woman_home) // Użyj odpowiedniej ikony
                    .setContentTitle("Nowe Zadanie")
                    .setContentText("Utworzyłeś nowe zadanie: " + task.getTitle())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = ("create_" + task.getId()).hashCode();
            notificationManager.notify(notificationId, builder.build());

            Log.d(TAG, "Powiadomienie natychmiastowe wysłane dla zadania: " + task.getId());
        } catch (Exception e) {
            Log.e(TAG, "Error sending immediate notification", e);
        }
    }

    /**
     * Harmonogramuje powiadomienia na dzień przed datą zadania oraz tego samego dnia.
     *
     * @param context Kontekst aplikacji.
     * @param task    Zadanie, dla którego harmonogramowane są powiadomienia.
     */
    public static void scheduleTaskNotifications(Context context, Task task) {
        // Ustawienie powiadomienia na dzień przed datą zadania
        Calendar calendarDayBefore = Calendar.getInstance();
        calendarDayBefore.setTime(task.getDueDate());
        calendarDayBefore.add(Calendar.DAY_OF_YEAR, -1); // Dzień przed

        long triggerTimeDayBefore = calendarDayBefore.getTimeInMillis();
        long currentTime = System.currentTimeMillis();

        if (triggerTimeDayBefore > currentTime) {
            scheduleTaskNotification(context, task, triggerTimeDayBefore, false);
        } else {
            Log.d(TAG, "Day-before notification time has already passed for task: " + task.getId());
        }

        // Ustawienie powiadomienia na ten sam dzień
        Calendar calendarSameDay = Calendar.getInstance();
        calendarSameDay.setTime(task.getDueDate());
        calendarSameDay.set(Calendar.HOUR_OF_DAY, 9); // Godzina 9:00
        calendarSameDay.set(Calendar.MINUTE, 0);
        calendarSameDay.set(Calendar.SECOND, 0);

        long triggerTimeSameDay = calendarSameDay.getTimeInMillis();

        if (triggerTimeSameDay > currentTime) {
            scheduleTaskNotification(context, task, triggerTimeSameDay, true);
        } else {
            Log.d(TAG, "Same-day notification time has already passed for task: " + task.getId());
        }
    }

    /**
     * Harmonogramuje pojedyncze powiadomienie.
     *
     * @param context    Kontekst aplikacji.
     * @param task       Zadanie, dla którego harmonogramowane jest powiadomienie.
     * @param triggerAt  Czas, kiedy powiadomienie ma zostać wysłane.
     * @param sameDay    Czy powiadomienie jest tego samego dnia.
     */
    private static void scheduleTaskNotification(Context context, Task task, long triggerAt, boolean sameDay) {
        try {
            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("clubName", task.getClubName());

            int requestCode = sameDay ? (task.getId().hashCode() + 1) : task.getId().hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode, // Unikalny request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAt,
                            pendingIntent
                    );
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerAt,
                            pendingIntent
                    );
                } else {
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerAt,
                            pendingIntent
                    );
                }

                Log.d(TAG, "Scheduled " + (sameDay ? "same-day" : "day-before") + " notification for task: " + task.getId() + " at " + new Date(triggerAt));
            } else {
                Log.e(TAG, "AlarmManager is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification for task: " + task.getId(), e);
        }
    }

    /**
     * Anuluje zaplanowane powiadomienia związane z zadaniem.
     *
     * @param context Kontekst aplikacji.
     * @param taskId  ID zadania.
     */
    public static void cancelTaskNotifications(Context context, String taskId) {
        try {
            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("taskTitle", ""); // Możesz przekazać więcej danych, jeśli potrzebujesz
            intent.putExtra("clubName", "");

            PendingIntent pendingIntentDayBefore = PendingIntent.getBroadcast(
                    context,
                    taskId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            PendingIntent pendingIntentSameDay = PendingIntent.getBroadcast(
                    context,
                    taskId.hashCode() + 1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntentDayBefore);
                alarmManager.cancel(pendingIntentSameDay);
                Log.d(TAG, "Anulowano powiadomienia dla zadania: " + taskId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error canceling notifications for task: " + taskId, e);
        }
    }

    /**
     * Wysyła natychmiastowe powiadomienie o aktualizacji zadania.
     *
     * @param context Kontekst aplikacji.
     * @param task    Zaktualizowane zadanie.
     */
    public static void sendUpdateNotification(Context context, Task task) {
        try {
            // Upewnij się, że kanał powiadomień jest utworzony
            createNotificationChannel(context);

            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("clubName", task.getClubName());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.getId().hashCode(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.woman_home) // Użyj odpowiedniej ikony
                    .setContentTitle("Zaktualizowane Zadanie")
                    .setContentText("Zaktualizowałeś zadanie: " + task.getTitle())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = ("update_" + task.getId()).hashCode();
            notificationManager.notify(notificationId, builder.build());

            Log.d(TAG, "Powiadomienie natychmiastowe wysłane dla zadania: " + task.getId());
        } catch (Exception e) {
            Log.e(TAG, "Error sending update notification", e);
        }
    }
}
