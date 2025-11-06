package com.example.firebaseapp.notes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.firebaseapp.R;
import com.example.firebaseapp.models.Note;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;

/**
 * Fragment do edycji istniejących notatek.
 */
public class EditNoteFragment extends BaseNoteFragment {

    private EditText editTextNoteTitle, editTextNoteDescription, editTextGoogleDriveLink;
    private TextView textViewNoteDate;
    private Button buttonSelectNoteDate, buttonUpdateNote, buttonDeleteNote;

    private Note note;
    private Date selectedDate;

    private static final String ARG_NOTE = "note";

    public EditNoteFragment() {
        // Wymagany pusty konstruktor
    }

    /**
     * Tworzy nową instancję EditNoteFragment z przekazaniem notatki.
     *
     * @param note Notatka do edycji.
     * @return Nowa instancja EditNoteFragment.
     */
    public static EditNoteFragment newInstance(Note note) {
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, note);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getClubNameFromArguments(Bundle args) {
        note = args.getParcelable(ARG_NOTE);
        if (note != null && note.getMembers() != null) {
            selectedMemberIds.addAll(note.getMembers());
        }
        return note != null ? note.getClubName() : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        // Inicjalizacja widoków
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteDescription = view.findViewById(R.id.editTextNoteDescription);
        editTextGoogleDriveLink = view.findViewById(R.id.editTextGoogleDriveLink);
        textViewNoteDate = view.findViewById(R.id.textViewNoteDate);
        buttonSelectNoteDate = view.findViewById(R.id.buttonSelectNoteDate);
        buttonUpdateNote = view.findViewById(R.id.buttonCreateOrUpdateNote);
        buttonDeleteNote = view.findViewById(R.id.delete_note_button);

        if (note != null) {
            editTextNoteTitle.setText(note.getTitle());
            editTextNoteDescription.setText(note.getDescription());
            textViewNoteDate.setText("Data: " + formatDate(note.getDate()));
            editTextGoogleDriveLink.setText(note.getGoogleDriveLink());
            selectedDate = note.getDate();
        } else {
            Log.e(TAG, "note jest null w EditNoteFragment");
            Toast.makeText(getContext(), "Błąd: Notatka nie została załadowana", Toast.LENGTH_SHORT).show();
        }

        setupRecyclerView(view, R.id.recyclerViewNoteMembers);
        fetchClubMembers();
        checkUserRoleAndSetButtonVisibility(buttonUpdateNote, List.of("admin", "prezes", "user"));
        checkUserRoleAndSetButtonVisibility(buttonDeleteNote, List.of("admin", "prezes", "user"));

        buttonSelectNoteDate.setOnClickListener(v -> showDatePicker());
        buttonUpdateNote.setOnClickListener(v -> updateNote());
        buttonDeleteNote.setOnClickListener(v -> confirmAndDeleteNote());

        return view;
    }

    /**
     * Wyświetla dialog wyboru daty.
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar.getTime();
                    textViewNoteDate.setText("Data: " + formatDate(selectedDate));
                    Log.d(TAG, "Selected date: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Aktualizuje istniejącą notatkę w Firestore.
     */
    private void updateNote() {
        String title = editTextNoteTitle.getText().toString().trim();
        String description = editTextNoteDescription.getText().toString().trim();
        String googleDriveLink = editTextGoogleDriveLink.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(googleDriveLink)) {
            Toast.makeText(getContext(), "Wszystkie pola są wymagane", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(getContext(), "Wybierz co najmniej jednego członka", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Nie jesteś zalogowany", Toast.LENGTH_SHORT).show();
            return;
        }

        String creatorId = mAuth.getCurrentUser().getUid();
        Date dueDate = selectedDate != null ? selectedDate : note.getDate();

        note.setTitle(title);
        note.setDescription(description);
        note.setGoogleDriveLink(googleDriveLink);
        note.setDate(dueDate);
        note.setMembers(new ArrayList<>(selectedMemberIds));

        db.collection("clubs").document(clubName).collection("notes").document(note.getId())
                .set(note)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Notatka zaktualizowana", Toast.LENGTH_SHORT).show();

                    // Wysyłanie powiadomienia typu "edit"
                    notificationManager.sendImmediateNotification(note, note.getId(), "edit");
                    notificationManager.cancelNotification(note.getId());

                    long currentTime = System.currentTimeMillis();

                    // Harmonogramowanie powiadomienia dzień przed
                    Calendar calendarDayBefore = Calendar.getInstance();
                    calendarDayBefore.setTime(note.getDate());
                    calendarDayBefore.add(Calendar.DAY_OF_YEAR, -1);
                    long triggerTimeDayBefore = calendarDayBefore.getTimeInMillis();

                    if (triggerTimeDayBefore > currentTime) {
                        notificationManager.scheduleNotification(note, note.getId(), triggerTimeDayBefore, false);
                    } else {
                        Log.d(TAG, "Czas powiadomienia dzień przed już minął dla notatki: " + note.getId());
                    }

                    // Harmonogramowanie powiadomienia tego samego dnia o 9:00
                    Calendar calendarSameDay = Calendar.getInstance();
                    calendarSameDay.setTime(note.getDate());
                    calendarSameDay.set(Calendar.HOUR_OF_DAY, 9);
                    calendarSameDay.set(Calendar.MINUTE, 0);
                    calendarSameDay.set(Calendar.SECOND, 0);
                    long triggerTimeSameDay = calendarSameDay.getTimeInMillis();

                    if (triggerTimeSameDay > currentTime) {
                        notificationManager.scheduleNotification(note, note.getId(), triggerTimeSameDay, true);
                    } else {
                        Log.d(TAG, "Czas powiadomienia tego samego dnia już minął dla notatki: " + note.getId());
                    }

                    navigateToNotesList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd aktualizacji notatki", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Błąd podczas aktualizacji notatki", e);
                });
    }

    /**
     * Potwierdza i usuwa notatkę.
     */
    private void confirmAndDeleteNote() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Usuń notatkę")
                .setMessage("Czy na pewno chcesz usunąć tę notatkę?")
                .setPositiveButton("Tak", (dialog, which) -> deleteNote())
                .setNegativeButton("Nie", null)
                .show();
    }

    /**
     * Usuwa notatkę z Firestore i anulowuje powiadomienia.
     */
    private void deleteNote() {
        if (note.getId() == null || note.getId().isEmpty()) {
            Toast.makeText(getContext(), "Nieprawidłowe ID notatki", Toast.LENGTH_SHORT).show();
            return;
        }

        notificationManager.cancelNotification(note.getId());

        db.collection("clubs").document(clubName).collection("notes").document(note.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Notatka usunięta", Toast.LENGTH_SHORT).show();
                    navigateToNotesList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Nie udało się usunąć notatki", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Błąd podczas usuwania notatki", e);
                });
    }

    /**
     * Nawiguje użytkownika do listy notatek po pomyślnym usunięciu lub aktualizacji notatki.
     */
    private void navigateToNotesList() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Formatuje datę na string w formacie dd/MM/yyyy.
     *
     * @param date Data do sformatowania.
     * @return Sformatowana data.
     */
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
