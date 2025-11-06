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
 * Fragment do tworzenia nowych notatek.
 */
public class CreateNoteFragment extends BaseNoteFragment {

    private EditText editTextNoteTitle, editTextNoteDescription, editTextGoogleDriveLink;
    private TextView textViewNoteDate;
    private Button buttonSelectNoteDate, buttonCreateNote;

    private Date selectedDate;

    private static final String ARG_CLUB_NAME = "clubName";

    public CreateNoteFragment() {
        // Wymagany pusty konstruktor
    }

    /**
     * Tworzy nową instancję CreateNoteFragment z przekazaniem nazwy klubu.
     *
     * @param clubName Nazwa klubu.
     * @return Nowa instancja CreateNoteFragment.
     */
    public static CreateNoteFragment newInstance(String clubName) {
        CreateNoteFragment fragment = new CreateNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLUB_NAME, clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getClubNameFromArguments(Bundle args) {
        return args.getString(ARG_CLUB_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_note, container, false);

        // Inicjalizacja widoków
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteDescription = view.findViewById(R.id.editTextNoteDescription);
        editTextGoogleDriveLink = view.findViewById(R.id.editTextGoogleDriveLink);
        textViewNoteDate = view.findViewById(R.id.textViewNoteDate);
        buttonSelectNoteDate = view.findViewById(R.id.buttonSelectNoteDate);
        buttonCreateNote = view.findViewById(R.id.buttonCreateNote);

        setupRecyclerView(view, R.id.recyclerViewNoteMembers);
        fetchClubMembers();
        checkUserRoleAndSetButtonVisibility(buttonCreateNote, List.of("admin", "prezes", "user"));

        buttonCreateNote.setOnClickListener(v -> createNote());
        buttonSelectNoteDate.setOnClickListener(v -> showDatePicker());

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
     * Tworzy nową notatkę w Firestore.
     */
    private void createNote() {
        if (clubName == null) {
            Toast.makeText(getContext(), "Invalid club name. Cannot create note.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Cannot create note because clubName is null");
            return;
        }

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
        Date dueDate = selectedDate != null ? selectedDate : new Date();

        Note note = new Note(title, description, dueDate, selectedMemberIds, googleDriveLink, clubName, creatorId);

        db.collection("clubs").document(clubName).collection("notes")
                .add(note)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Note created with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "Notatka utworzona", Toast.LENGTH_SHORT).show();

                    // Wysyłanie powiadomienia typu "create"
                    notificationManager.sendImmediateNotification(note, documentReference.getId(), "create");

                    long currentTime = System.currentTimeMillis();

                    // Harmonogramowanie powiadomienia dzień przed
                    Calendar calendarDayBefore = Calendar.getInstance();
                    calendarDayBefore.setTime(note.getDate());
                    calendarDayBefore.add(Calendar.DAY_OF_YEAR, -1);
                    long triggerTimeDayBefore = calendarDayBefore.getTimeInMillis();

                    if (triggerTimeDayBefore > currentTime) {
                        notificationManager.scheduleNotification(note, documentReference.getId(), triggerTimeDayBefore, false);
                    } else {
                        Log.d(TAG, "Czas powiadomienia dzień przed już minął dla notatki: " + documentReference.getId());
                    }

                    // Harmonogramowanie powiadomienia tego samego dnia o 9:00
                    Calendar calendarSameDay = Calendar.getInstance();
                    calendarSameDay.setTime(note.getDate());
                    calendarSameDay.set(Calendar.HOUR_OF_DAY, 9);
                    calendarSameDay.set(Calendar.MINUTE, 0);
                    calendarSameDay.set(Calendar.SECOND, 0);
                    long triggerTimeSameDay = calendarSameDay.getTimeInMillis();

                    if (triggerTimeSameDay > currentTime) {
                        notificationManager.scheduleNotification(note, documentReference.getId(), triggerTimeSameDay, true);
                    } else {
                        Log.d(TAG, "Czas powiadomienia tego samego dnia już minął dla notatki: " + documentReference.getId());
                    }

                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating note", e);
                    Toast.makeText(getContext(), "Błąd podczas tworzenia notatki", Toast.LENGTH_SHORT).show();
                });
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
