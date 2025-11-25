//package com.example.firebaseapp.workers;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.example.firebaseapp.Main.MainActivity;
//import com.example.firebaseapp.R;
//
//public class NotificationWorker extends Worker {
//
//    private static final String CHANNEL_ID = "task_due_notifications";
//    private static final String CHANNEL_NAME = "Task Due Notifications";
//    private static final String CHANNEL_DESCRIPTION = "Notifications for task due dates";
//
//    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        String taskTitle = getInputData().getString("taskTitle");
//        String clubName = getInputData().getString("clubName");
//
//        sendNotification(taskTitle, clubName);
//
//        return Result.success();
//    }
//
//    private void sendNotification(String taskTitle, String clubName) {
//        Context context = getApplicationContext();
//
//        // Tworzenie kanału powiadomień (tylko dla Android O i nowszych)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            channel.setDescription(CHANNEL_DESCRIPTION);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//
//        // Intent do otwarcia aplikacji po kliknięciu powiadomienia
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                intent,
//                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        // Budowanie powiadomienia
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.woman_home) // Upewnij się, że masz odpowiednią ikonę
//                .setContentTitle("Zadanie wkrótce do wykonania")
//                .setContentText("Zadanie \"" + taskTitle + "\" w klubie \"" + clubName + "\" ma już dziś termin wykonania.")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        // Wyświetlanie powiadomienia
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager != null) {
//            // Unikalny ID powiadomienia może być np. hashCode tytułu zadania
//            int notificationId = taskTitle.hashCode();
//            notificationManager.notify(notificationId, builder.build());
//        }
//    }
//}
