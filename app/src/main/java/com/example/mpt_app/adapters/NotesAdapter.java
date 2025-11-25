package com.example.mpt_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpt_app.R;
import com.example.mpt_app.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private OnNoteClickListener listener;
    private OnNoteDeleteListener deleteListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public interface OnNoteDeleteListener {
        void onNoteDelete(Note note, int position);
    }

    public NotesAdapter(List<Note> notesList, OnNoteClickListener listener, OnNoteDeleteListener deleteListener) {
        this.notesList = notesList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public NotesAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void removeItem(int position) {
        Note note = notesList.get(position);
        notesList.remove(position);
        notifyItemRemoved(position);
        if (deleteListener != null) {
            deleteListener.onNoteDelete(note, position);
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNoteTitle, textViewNoteDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteTitle = itemView.findViewById(R.id.textViewNoteTitle);
            textViewNoteDate = itemView.findViewById(R.id.textViewNoteDate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onNoteClick(notesList.get(position));
                }
            });
        }

        public void bind(Note note) {
            textViewNoteTitle.setText(note.getTitle());
            textViewNoteDate.setText("Data: " + formatDate(note.getDate()));
        }

        private String formatDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }
}
