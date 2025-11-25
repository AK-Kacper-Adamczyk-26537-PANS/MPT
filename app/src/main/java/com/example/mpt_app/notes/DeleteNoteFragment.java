package com.example.mpt_app.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mpt_app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Fragment do usuwania notatek.
 */
public class DeleteNoteFragment extends BaseNoteFragment {

    private Button deleteButton;
    private String noteId;

    private static final String ARG_NOTE_ID = "noteId";

    public DeleteNoteFragment() {
        // Wymagany pusty konstruktor
    }

    /**
     * Tworzy nową instancję DeleteNoteFragment z przekazaniem identyfikatora notatki i nazwy klubu.
     *
     * @param noteId   Identyfikator notatki do usunięcia.
     * @param clubName Nazwa klubu, do którego należy notatka.
     * @return Nowa instancja DeleteNoteFragment.
     */
    public static DeleteNoteFragment newInstance(String noteId, String clubName) {
        DeleteNoteFragment fragment = new DeleteNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTE_ID, noteId);
        args.putString("clubName", clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getClubNameFromArguments(Bundle args) {
        noteId = args.getString(ARG_NOTE_ID);
        return args.getString("clubName");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        deleteButton = view.findViewById(R.id.delete_note_button);

        // Sprawdzenie uprawnień użytkownika
        checkUserRoleAndSetButtonVisibility(deleteButton, List.of("admin", "prezes", "user"));

        deleteButton.setOnClickListener(v -> confirmAndDeleteNote());

        return view;
    }

    /**
     * Wyświetla dialog potwierdzający usunięcie notatki.
     */
    private void confirmAndDeleteNote() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Usuń notatkę")
                .setMessage("Czy na pewno chcesz usunąć tę notatkę?")
                .setPositiveButton("Tak", (dialog, which) -> deleteNote())
                .setNegativeButton("Nie", null)
                .show();
    }

    /**
     * Usuwa notatkę z Firestore i anuluje powiadomienia związane z tą notatką.
     */
    private void deleteNote() {
        if (noteId == null || noteId.isEmpty()) {
            Toast.makeText(getContext(), "Nieprawidłowe ID notatki", Toast.LENGTH_SHORT).show();
            return;
        }

        // Anulowanie powiadomień
        notificationManager.cancelNotification(noteId);

        // Usuwanie notatki z Firestore
        db.collection("clubs").document(clubName).collection("notes").document(noteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Notatka usunięta", Toast.LENGTH_SHORT).show();
                    navigateToNotesList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Nie udało się usunąć notatki", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Nawiguje użytkownika do listy notatek po pomyślnym usunięciu notatki.
     */
    private void navigateToNotesList() {
        NotesListFragment notesHomeFragment = NotesListFragment.newInstance(clubName);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, notesHomeFragment) // Upewnij się, że ID kontenera jest poprawne
                .addToBackStack(null)
                .commit();
    }
}
