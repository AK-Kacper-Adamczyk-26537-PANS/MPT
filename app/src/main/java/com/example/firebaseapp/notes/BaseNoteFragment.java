package com.example.firebaseapp.notes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.models.User;
import com.example.firebaseapp.notifications.NoteNotificationManager;
import com.example.firebaseapp.adapters.MembersSelectAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa bazowa dla wszystkich fragmentów związanych z notatkami.
 * Zawiera wspólne funkcjonalności, takie jak inicjalizacja Firebase,
 * zarządzanie powiadomieniami, oraz obsługa członków klubu.
 */
public abstract class BaseNoteFragment extends Fragment implements MembersSelectAdapter.OnMemberSelectListener {

    protected FirebaseFirestore db;
    protected FirebaseAuth mAuth;
    protected NoteNotificationManager notificationManager;

    protected String clubName;

    protected MembersSelectAdapter membersSelectAdapter;
    protected List<User> allMembers = new ArrayList<>();
    protected List<String> selectedMemberIds = new ArrayList<>();

    protected static final String TAG = "BaseNoteFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        notificationManager = new NoteNotificationManager(requireContext());

        if (getArguments() != null) {
            clubName = getClubNameFromArguments(getArguments());
            if (clubName == null) {
                Log.e(TAG, "clubName is null in arguments");
            } else {
                Log.d(TAG, "clubName received: " + clubName);
            }
        } else {
            Log.e(TAG, "No arguments found in BaseNoteFragment");
        }
    }

    /**
     * Abstrakcyjna metoda do pobrania nazwy klubu z argumentów.
     * Implementowane przez klasy dziedziczące.
     *
     * @param args Bundle z argumentami.
     * @return Nazwa klubu.
     */
    protected abstract String getClubNameFromArguments(Bundle args);

    /**
     * Ustawia RecyclerView dla listy członków klubu.
     */
    protected void setupRecyclerView(View view, int recyclerViewId) {
        RecyclerView recyclerViewNoteMembers = view.findViewById(recyclerViewId);
        membersSelectAdapter = new MembersSelectAdapter(getContext(), allMembers, selectedMemberIds, this);
        recyclerViewNoteMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNoteMembers.setAdapter(membersSelectAdapter);
    }

    /**
     * Sprawdza rolę użytkownika i ustawia widoczność oraz dostępność przycisków.
     *
     * @param button przycisk do ustawienia.
     * @param roles listę ról, które mają dostęp.
     */
    protected void checkUserRoleAndSetButtonVisibility(Button button, List<String> roles) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if (roles.contains(role)) {
                                button.setEnabled(true);
                                button.setVisibility(View.VISIBLE);
                            } else {
                                button.setEnabled(false);
                                button.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Brak uprawnień do tej akcji", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Dokument użytkownika nie istnieje", Toast.LENGTH_SHORT).show();
                            button.setEnabled(false);
                            button.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Błąd podczas sprawdzania roli użytkownika", Toast.LENGTH_SHORT).show();
                        button.setEnabled(false);
                        button.setVisibility(View.GONE);
                    });
        } else {
            button.setEnabled(false);
            button.setVisibility(View.GONE);
        }
    }

    /**
     * Pobiera członków klubu z Firestore.
     */
    protected void fetchClubMembers() {
        if (clubName == null) {
            Log.e(TAG, "clubName is null in fetchClubMembers");
            Toast.makeText(getContext(), "Błąd: Nazwa klubu jest nieznana", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("clubs").document(clubName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> memberIds = (List<String>) documentSnapshot.get("members");
                        if (memberIds != null) {
                            for (String userId : memberIds) {
                                fetchUserDetails(userId);
                            }
                        } else {
                            Toast.makeText(getContext(), "Brak członków w klubie", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Dokument klubu nie istnieje", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Nie udało się pobrać członków klubu", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Pobiera szczegóły użytkownika z Firestore.
     *
     * @param userId ID użytkownika.
     */
    protected void fetchUserDetails(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String role = documentSnapshot.getString("role");

                        User user = new User();
                        user.setUserId(userId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setEmail(email);
                        user.setRole(role);

                        // Unikaj duplikatów
                        if (!allMembers.contains(user)) {
                            allMembers.add(user);
                            membersSelectAdapter.notifyItemInserted(allMembers.size() - 1);
                            Log.d(TAG, "Added user: " + user.getFirstName() + " " + user.getLastName());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Nie udało się pobrać danych użytkownika", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Obsługuje wybór członka klubu.
     *
     * @param userId     ID użytkownika.
     * @param isSelected Czy użytkownik został wybrany.
     */
    @Override
    public void onMemberSelected(String userId, boolean isSelected) {
        if (isSelected) {
            if (!selectedMemberIds.contains(userId)) {
                selectedMemberIds.add(userId);
            }
        } else {
            selectedMemberIds.remove(userId);
        }
    }

    // Dodatkowe wspólne metody mogą być tutaj dodane
}
