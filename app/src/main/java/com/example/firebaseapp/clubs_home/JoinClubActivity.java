//package com.example.firebaseapp.clubs_home;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.example.firebaseapp.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//public class JoinClubActivity extends AppCompatActivity {
//
//    private TextView clubNameTextView;
//    private Button joinButton;
//
//    private static final int NOTIFICATION_PERMISSION_CODE = 1;
//    private static final String CHANNEL_ID = "club_notifications";
//    private static final String CHANNEL_NAME = "Club Notifications";
//    private static final String CHANNEL_DESCRIPTION = "Channel for club join notifications";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_join_club);
//
//        // Inicjalizacja widoków
//        initializeViews();
//
//        // Sprawdź i poproś o uprawnienia
//        checkNotificationPermission();
//        createNotificationChannel();
//
//        // Ustaw listener na przycisk
//        String clubName = getIntent().getStringExtra("clubName");
//        clubNameTextView.setText("Czy chcesz dołączyć do " + clubName + "?");
//        joinButton.setOnClickListener(v -> addUserToClub(clubName));
//    }
//
//    private void initializeViews() {
//        clubNameTextView = findViewById(R.id.club_name_text_view);
//        joinButton = findViewById(R.id.join_button);
//    }
//
//    private void checkNotificationPermission() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
//        }
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription(CHANNEL_DESCRIPTION);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    private void addUserToClub(String clubName) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            db.collection("clubs").document(clubName).get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            // Klub już istnieje, aktualizuj członków
//                            updateClubMembers(clubName, userId);
//                        } else {
//                            // Klub nie istnieje, twórz nowy
////                            createNewClub(clubName, userId);
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.e("JoinClubActivity", "Error fetching club document", e));
//        }
//    }
//
//
//    private void updateClubMembers(String clubName, String userId) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("clubs").document(clubName)
//                .update("members", FieldValue.arrayUnion(userId))
//                .addOnSuccessListener(aVoid -> {
//                    sendLocalNotification(clubName);
//                    finish();
//                })
//                .addOnFailureListener(e -> Log.e("JoinClubActivity", "Error updating members", e));
//    }
//
////    private void createNewClub(String clubName, String userId) {
////        Map<String, Object> newClubData = new HashMap<>();
////        newClubData.put("members", Collections.singletonList(userId));
////        newClubData.put("logoUrl", "https://example.com/default_logo.png"); // Dodaj URL logo
////
////        FirebaseFirestore db = FirebaseFirestore.getInstance();
////        db.collection("clubs").document(clubName).set(newClubData)
////                .addOnSuccessListener(aVoid -> {
////                    sendLocalNotification(clubName);
////                    finish();
////                })
////                .addOnFailureListener(e -> Log.e("JoinClubActivity", "Error creating new club", e));
////    }
//
//
//    private void sendLocalNotification(String clubName) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher) // Wstaw odpowiednią ikonę
//                .setContentTitle("Dołączyłeś do koła!")
//                .setContentText("Gratulacje! Jesteś teraz członkiem koła: " + clubName)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            notificationManager.notify(1, builder.build());
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Uprawnienie przyznane
//            } else {
//                Log.d("JoinClubActivity", "Notification permission denied");
//            }
//        }
//    }
//}
