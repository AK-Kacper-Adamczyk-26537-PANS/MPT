package com.example.firebaseapp.members;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MemberDetailFragment extends Fragment {

    private static final String ARG_USER_ID = "arg_user_id";

    private String userId;
    private User user;

    private TextView textViewFullName, textViewEmail, textViewRole;
    // Dodaj więcej widoków, jeśli potrzebujesz

    private FirebaseFirestore db;

    private static final String TAG = "MemberDetailFragment";

    public MemberDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Tworzy nową instancję fragmentu z identyfikatorem użytkownika.
     *
     * @param userId Identyfikator użytkownika.
     * @return Nowa instancja MemberDetailFragment.
     */
    public static MemberDetailFragment newInstance(String userId) {
        MemberDetailFragment fragment = new MemberDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        // Pobierz userId z argumentów
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            Log.d(TAG, "Przekazany userId: " + userId);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_member_detail, container, false);

        // Znajdź widoki
        textViewFullName = view.findViewById(R.id.textViewFullName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewRole = view.findViewById(R.id.textViewRole);
        // Dodaj więcej widoków, jeśli potrzebujesz

        // Pobierz dane użytkownika
        if (userId != null && !userId.isEmpty()) {
            fetchUserDetails(userId);
        } else {
            Toast.makeText(getContext(), "Nieprawidłowy identyfikator użytkownika", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Nieprawidłowy identyfikator użytkownika");
        }

        return view;
    }

    /**
     * Pobiera dane użytkownika z Firestore.
     *
     * @param userId Identyfikator użytkownika.
     */
    private void fetchUserDetails(String userId) {
        Log.d(TAG, "fetchUserDetails called for userId: " + userId);
        try {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                Log.d(TAG, "User data fetched successfully");
                                displayUserDetails();
                            } else {
                                Toast.makeText(getContext(), "Dane użytkownika są puste", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Dane użytkownika są puste");
                            }
                        } else {
                            Toast.makeText(getContext(), "Użytkownik nie istnieje", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Dokument użytkownika nie istnieje: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Błąd podczas pobierania danych użytkownika", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error getting user data", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in fetchUserDetails", e);
            Toast.makeText(getContext(), "Błąd podczas pobierania danych użytkownika", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Wyświetla dane użytkownika w widokach.
     */
    private void displayUserDetails() {
        String fullName = user.getFirstName() + " " + user.getLastName();
        textViewFullName.setText(fullName);
        textViewEmail.setText(user.getEmail());
        textViewRole.setText("Rola: " + user.getRole());
        Log.d(TAG, "User details displayed");
        // Ustaw inne dane, jeśli istnieją
    }
}
