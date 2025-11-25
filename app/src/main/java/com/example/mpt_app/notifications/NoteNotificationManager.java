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
import com.example.mpt_app.models.Note;
import com.example.mpt_app.receivers.NoteNotificationReceiver;

import java.util.Date;

/**
 * Klasa zarządzająca powiadomieniami dotyczącymi notatek.
 * Obsługuje powiadomienia natychmiastowe oraz harmonogramowane (dzień przed i tego samego dnia).
 */
public class NoteNotificationManager {

    private static final String TAG = "NoteNotificationManager";
    private static final String CHANNEL_ID = "note_notifications_channel";
    private static final String CHANNEL_NAME = "Powiadomienia o notatkach";
    private static final String CHANNEL_DESCRIPTION = "Powiadomienia przypominające o notatkach";

    private final Context context;

    /**
     * Konstruktor klasy NoteNotificationManager.
     *
     * @param context Kontekst aplikacji.
     */
    public NoteNotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * Tworzy kanał powiadomień, jeśli wersja Androida jest Oreo lub nowsza.
     */
    private void createNotificationChannel() {
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
                    Log.d(TAG, "Kanał powiadomień został utworzony");
                }
            } catch (Exception e) {
                Log.e(TAG, "Błąd podczas tworzenia kanału powiadomień", e);
            }
        }
    }

    /**
     * Wysyła natychmiastowe powiadomienie o notatce.
     *
     * @param note   Obiekt notatki.
     * @param noteId ID notatki.
     * @param action Typ powiadomienia ("create" lub "edit").
     */
    public void sendImmediateNotification(Note note, String noteId, String action) {
        try {
            createNotificationChannel();

            Intent intent = new Intent(context, NoteNotificationReceiver.class);
            intent.putExtra("noteId", noteId);
            intent.putExtra("noteTitle", note.getTitle());
            intent.putExtra("clubName", note.getClubName());
            intent.putExtra("action", action); // "create" lub "edit"

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    noteId.hashCode(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );

            String contentTitle;
            String contentText;

            if ("edit".equals(action)) {
//                contentTitle = "Zaktualizowałeś notatkę: " + note.getTitle();
                contentTitle = "Zaktualizowana notatka";
//                contentText = "Notatka w klubie " + note.getClubName() + " została zaktualizowana.";
                contentText = "Zaktualizowałeś notatkę: " + note.getTitle();
            } else { // Domyślnie "create"
//                contentTitle = "Utworzyłeś nową notatkę: " + note.getTitle();
                contentTitle = "Nowa Notatka";
                contentText = "Utworzyłeś nową notatkę: " + note.getTitle();
//                contentText = "Nowa notatka została dodana do klubu " + note.getClubName() + ".";
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.woman_home) // Użyj odpowiedniej ikony
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = ("note_" + noteId + "_" + action).hashCode();
            notificationManager.notify(notificationId, builder.build());

            Log.d(TAG, "Powiadomienie natychmiastowe wysłane dla notatki: " + noteId + ", akcja: " + action);
        } catch (Exception e) {
            Log.e(TAG, "Błąd podczas wysyłania natychmiastowego powiadomienia", e);
        }
    }

    /**
     * Harmonogramuje powiadomienia na dzień przed datą notatki oraz tego samego dnia.
     *
     * @param note      Obiekt notatki.
     * @param noteId    ID notatki.
     * @param triggerTime Czas, kiedy powiadomienie ma zostać wysłane.
     * @param sameDay   Czy powiadomienie jest tego samego dnia.
     */
    public void scheduleNotification(Note note, String noteId, long triggerTime, boolean sameDay) {
        try {
            Intent intent = new Intent(context, NoteNotificationReceiver.class);
            intent.putExtra("noteId", noteId);
            intent.putExtra("noteTitle", note.getTitle());
            intent.putExtra("clubName", note.getClubName());
            intent.putExtra("action", "reminder"); // "reminder" dla harmonogramowanych powiadomień

            int requestCode = sameDay ? (noteId.hashCode() + 1) : noteId.hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                    );
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                    );
                } else {
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                    );
                }

                String timeType = sameDay ? "powiadomienie tego samego dnia" : "powiadomienie dzień wcześniej";
                Log.d(TAG, "Zaplanowano " + timeType + " dla notatki: " + noteId + " na " + new Date(triggerTime));
            } else {
                Log.e(TAG, "AlarmManager jest null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Błąd podczas harmonogramowania powiadomienia dla notatki: " + noteId, e);
        }
    }

    /**
     * Anuluje zaplanowane powiadomienia związane z notatką.
     *
     * @param noteId ID notatki.
     */
    public void cancelNotification(String noteId) {
        try {
            Intent intent = new Intent(context, NoteNotificationReceiver.class);
            intent.putExtra("noteId", noteId);
            intent.putExtra("action", "reminder"); // Upewnij się, że akcja jest taka sama jak przy harmonogramowaniu

            PendingIntent pendingIntentDayBefore = PendingIntent.getBroadcast(
                    context,
                    noteId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            PendingIntent pendingIntentSameDay = PendingIntent.getBroadcast(
                    context,
                    noteId.hashCode() + 1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntentDayBefore);
                alarmManager.cancel(pendingIntentSameDay);
                Log.d(TAG, "Anulowano powiadomienia dla notatki: " + noteId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Błąd podczas anulowania powiadomień dla notatki: " + noteId, e);
        }
    }
}
