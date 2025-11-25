package com.example.mpt_app.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mpt_app.R;
import com.example.mpt_app.adapters.NotesAdapter;
import com.example.mpt_app.models.Note;
import com.example.mpt_app.receivers.NoteNotificationReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotesListFragment extends Fragment implements NotesAdapter.OnNoteClickListener, NotesAdapter.OnNoteDeleteListener {

    private RecyclerView recyclerViewNotes;
    private NotesAdapter notesAdapter;
    private List<Note> notesList;
    private FloatingActionButton fabAddNote;
    private String clubName;

    private FirebaseFirestore db;

    private static final String TAG = "NotesListFragment";

    public static NotesListFragment newInstance(String clubName) {
        NotesListFragment fragment = new NotesListFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notesList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            clubName = getArguments().getString("clubName");
            if (clubName == null) {
                Log.e(TAG, "clubName is null in NotesListFragment arguments");
            } else {
                Log.d(TAG, "clubName received: " + clubName);
            }
        } else {
            Log.e(TAG, "No arguments found in NotesListFragment");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes);
        fabAddNote = view.findViewById(R.id.fabAddNote);

        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        notesAdapter = new NotesAdapter(notesList, this, this);
        recyclerViewNotes.setAdapter(notesAdapter);

        fabAddNote.setOnClickListener(v -> {
            if (clubName != null) {
                CreateNoteFragment createNoteFragment = CreateNoteFragment.newInstance(clubName);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createNoteFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Invalid club name. Cannot add note.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Cannot navigate to CreateNoteFragment because clubName is null");
            }
        });

        fetchNotes();
        attachSwipeToDelete();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNotes(); // Pobierz notatki ponownie po powrocie do fragmentu
    }

    private void fetchNotes() {
        if (clubName == null) {
            Log.e(TAG, "clubName is null in fetchNotes");
            return;
        }

        db.collection("clubs").document(clubName).collection("notes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notesList.clear();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Note note = document.toObject(Note.class);
                        note.setId(document.getId());
                        note.setClubName(clubName);
                        notesList.add(note);
                    }
                    notesAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas pobierania notatek", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching notes", e);
                });
    }

    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // Nie obsługujemy przesuwania
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note noteToDelete = notesList.get(position);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Usuń notatkę")
                        .setMessage("Czy na pewno chcesz usunąć tę notatkę?")
                        .setPositiveButton("Tak", (dialog, which) -> {
                            deleteNote(noteToDelete, position);
                        })
                        .setNegativeButton("Nie", (dialog, which) -> {
                            notesAdapter.notifyItemChanged(position); // Przywróć notatkę
                        })
                        .setCancelable(false)
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewNotes);
    }

    private void deleteNote(Note note, int position) {
        cancelNoteNotification(note.getId());

        db.collection("clubs").document(clubName).collection("notes").document(note.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Note deleted with ID: " + note.getId());
                    notesAdapter.removeItem(position);
                    Toast.makeText(getContext(), "Notatka usunięta", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting note", e);
                    Toast.makeText(getContext(), "Nie udało się usunąć notatki", Toast.LENGTH_SHORT).show();
                    notesAdapter.notifyItemChanged(position); // Przywróć notatkę
                });
    }

    private void cancelNoteNotification(String noteId) {
        Intent intent = new Intent(requireContext(), NoteNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                noteId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Canceled notification for note: " + noteId);
        }

        // Anulowanie powiadomienia tego samego dnia
        PendingIntent sameDayPendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                noteId.hashCode() + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(sameDayPendingIntent);
            Log.d(TAG, "Canceled same-day notification for note: " + noteId);
        }
    }

    @Override
    public void onNoteClick(Note note) {
        if (note != null) {
            EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(note);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editNoteFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onNoteDelete(Note note, int position) {
        // Opcjonalne dodatkowe działania po usunięciu notatki
    }
}