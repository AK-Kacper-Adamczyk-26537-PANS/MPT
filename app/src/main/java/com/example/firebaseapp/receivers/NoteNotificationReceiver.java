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

public class NoteNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NoteNotificationReceiver";
    private static final String CHANNEL_ID = "note_notifications_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String noteId = intent.getStringExtra("noteId");
        String noteTitle = intent.getStringExtra("noteTitle");
        String clubName = intent.getStringExtra("clubName");
        String action = intent.getStringExtra("action"); // "create", "edit", "reminder"

        if (noteId == null || noteTitle == null || clubName == null || action == null) {
            Log.e(TAG, "Brak wymaganych informacji o notatce");
            return;
        }

        createNotificationChannel(context);

        String contentTitle;
        String contentText;

        switch (action) {
            case "create":
                contentTitle = "Utworzyłeś nową notatkę: " + noteTitle;
                contentText = "Nowa notatka została dodana do klubu " + clubName + ".";
                break;
            case "edit":
                contentTitle = "Zaktualizowałeś notatkę: " + noteTitle;
                contentText = "Notatka w klubie " + clubName + " została zaktualizowana.";
                break;
            case "reminder":
                contentTitle = "Przypomnienie o notatce: " + noteTitle;
                contentText = "Nie zapomnij o notatce w klubie " + clubName + ".";
                break;
            default:
                Log.e(TAG, "Nieznany typ akcji: " + action);
                return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.woman_home) // Użyj odpowiedniej ikony
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = ("note_" + noteId + "_" + action).hashCode();
        notificationManager.notify(notificationId, builder.build());

        Log.d(TAG, "Powiadomienie wysłane dla notatki: " + noteId + ", akcja: " + action);
    }

    /**
     * Tworzy kanał powiadomień, jeśli wersja Androida jest Oreo lub nowsza.
     *
     * @param context Kontekst aplikacji.
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Powiadomienia o notatkach",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Powiadomienia przypominające o notatkach");
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            } catch (Exception e) {
                Log.e(TAG, "Błąd podczas tworzenia kanału powiadomień", e);
            }
        }
    }
}
