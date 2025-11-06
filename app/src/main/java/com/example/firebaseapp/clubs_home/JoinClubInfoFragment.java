package com.example.firebaseapp.clubs_home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.firebaseapp.R;
import com.example.firebaseapp.clubs_home.ClubsHomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class JoinClubInfoFragment extends Fragment {

    private TextView clubNameTextView;
    private Button joinButton;

    private static final int NOTIFICATION_PERMISSION_CODE = 1;
    private static final String CHANNEL_ID = "club_notifications";
    private static final String CHANNEL_NAME = "Club Notifications";
    private static final String CHANNEL_DESCRIPTION = "Channel for club join notifications";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_club, container, false);

        // Inicjalizacja widoków
        clubNameTextView = view.findViewById(R.id.club_name_text_view);
        joinButton = view.findViewById(R.id.join_club_button);

        // Ustawienie nazwy klubu
        String clubName = getArguments().getString("clubName");
        clubNameTextView.setText("Czy chcesz dołączyć do " + clubName + "?");

        // Sprawdź i poproś o uprawnienia
        checkNotificationPermission();
        createNotificationChannel();

        // Ustaw listener na przycisk
        joinButton.setOnClickListener(v -> addUserToClub(clubName));

        return view;
    }

    private void checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addUserToClub(String clubName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("clubs").document(clubName).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Klub już istnieje, aktualizuj członków
                            updateClubMembers(clubName, userId);
                        } else {
                            Log.e("JoinClubFragment", "Klub nie istnieje");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("JoinClubFragment", "Error fetching club document", e));
        }
    }

    private void updateClubMembers(String clubName, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clubs").document(clubName)
                .update("members", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("JoinClubFragment", "User added to club members successfully.");
                    sendLocalNotification(clubName); // Wyślij powiadomienie
                    navigateToClubsList(); // Przekieruj do listy klubów
                })
                .addOnFailureListener(e -> Log.e("JoinClubFragment", "Error updating members", e));
    }

    private void sendLocalNotification(String clubName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher) // Ustaw właściwą ikonę
                .setContentTitle("Dołączyłeś do koła!")
                .setContentText("Gratulacje! Jesteś teraz członkiem koła: " + clubName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }

    private void navigateToClubsList() {
        ClubsHomeFragment clubsListFragment = new ClubsHomeFragment(); // Załóżmy, że masz taki fragment

        // Nawiguj do fragmentu z listą klubów
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, clubsListFragment) // Upewnij się, że ID kontenera jest poprawne
                .addToBackStack(null) // Dodaj do stosu wstecz
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Uprawnienie przyznane
            } else {
                Log.d("JoinClubFragment", "Notification permission denied");
            }
        }
    }
}
