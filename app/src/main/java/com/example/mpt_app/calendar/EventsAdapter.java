// EventsAdapter.java
package com.example.mpt_app.calendar;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpt_app.R;
import com.google.firebase.firestore.CollectionReference;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Adapter odpowiedzialny za wyświetlanie wydarzeń w RecyclerView.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<CalendarEvent> eventsList;
    private CollectionReference eventsCollection;
    private CalendarFragment calendarFragment;

    /**
     * Konstruktor adaptera.
     *
     * @param calendarFragment Referencja do fragmentu kalendarza.
     * @param eventsList       Lista wydarzeń do wyświetlenia.
     * @param eventsCollection Referencja do kolekcji wydarzeń w Firestore.
     */
    public EventsAdapter(CalendarFragment calendarFragment, List<CalendarEvent> eventsList, CollectionReference eventsCollection) {
        this.calendarFragment = calendarFragment;
        this.eventsList = eventsList;
        this.eventsCollection = eventsCollection;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(calendarFragment.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventViewHolder holder, int position) {
        CalendarEvent event = eventsList.get(position);
        holder.eventDateTextView.setText(event.getFormattedDate());
        holder.eventTitleTextView.setText(event.getTitle());

        // Ustawienie ikony na podstawie wybranego zasobu
        holder.eventIconImageView.setImageResource(event.getIconResource());

        holder.itemView.setOnClickListener(v -> showEditDeleteDialog(event, position));
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    /**
     * Metoda do wyświetlania dialogu edycji lub usuwania wydarzenia.
     *
     * @param event    Wydarzenie do edycji lub usunięcia.
     * @param position Pozycja w RecyclerView.
     */
    private void showEditDeleteDialog(CalendarEvent event, int position) {
        Log.d("EventsAdapter", "showEditDeleteDialog: Wydarzenie ID " + event.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(calendarFragment.getContext());
        builder.setTitle("Wybierz akcję");

        String[] options = {"Edytuj", "Usuń"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Edytuj
                Log.d("EventsAdapter", "Wybrano opcję Edytuj dla wydarzenia ID " + event.getId());
                showEditDialog(event, position);
            } else if (which == 1) {
                // Usuń
                Log.d("EventsAdapter", "Wybrano opcję Usuń dla wydarzenia ID " + event.getId());
                showDeleteConfirmationDialog(event, position);
            }
        });

        builder.show();
    }

    /**
     * Metoda do wyświetlania dialogu edycji wydarzenia.
     *
     * @param event    Wydarzenie do edycji.
     * @param position Pozycja w RecyclerView.
     */
    private void showEditDialog(CalendarEvent event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(calendarFragment.getContext());
        builder.setTitle("Edytuj wydarzenie");

        View dialogView = LayoutInflater.from(calendarFragment.getContext()).inflate(R.layout.dialog_edit_event, null, false);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.editEventTitleInput);
        EditText eventDescriptionInput = dialogView.findViewById(R.id.editEventDescriptionInput);
        RadioGroup iconRadioGroup = dialogView.findViewById(R.id.editIconRadioGroup);
        RadioButton editIcon1RadioButton = dialogView.findViewById(R.id.editIcon1RadioButton);
        RadioButton editIcon2RadioButton = dialogView.findViewById(R.id.editIcon2RadioButton);
        RadioButton editIcon3RadioButton = dialogView.findViewById(R.id.editIcon3RadioButton);

        // Ustaw aktualne dane wydarzenia
        eventTitleInput.setText(event.getTitle());
        eventDescriptionInput.setText(event.getDescription());

        // Ustaw zaznaczenie odpowiedniego RadioButton na podstawie aktualnej ikony
        if (event.getIconResource() == R.drawable.ic_event1) {
            editIcon1RadioButton.setChecked(true);
        } else if (event.getIconResource() == R.drawable.ic_event2) {
            editIcon2RadioButton.setChecked(true);
        } else if (event.getIconResource() == R.drawable.ic_event3) {
            editIcon3RadioButton.setChecked(true);
        } else {
            editIcon1RadioButton.setChecked(true);
        }

        // Deklaracja 'event' jako finalny
        final CalendarEvent finalEvent = event;

        builder.setPositiveButton("Zapisz", (dialog, which) -> {
            String newTitle = eventTitleInput.getText().toString().trim();
            String newDescription = eventDescriptionInput.getText().toString().trim();

            if (!newTitle.isEmpty()) {
                // Pobranie wybranej ikony jako finalna zmienna
                final int selectedIcon;
                int selectedId = iconRadioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.editIcon1RadioButton) {
                    selectedIcon = R.drawable.ic_event1;
                } else if (selectedId == R.id.editIcon2RadioButton) {
                    selectedIcon = R.drawable.ic_event2;
                } else if (selectedId == R.id.editIcon3RadioButton) {
                    selectedIcon = R.drawable.ic_event3;
                } else {
                    selectedIcon = R.drawable.ic_event1;
                }

                // Aktualizacja w Firestore
                eventsCollection.document(finalEvent.getId())
                        .update("title", newTitle, "description", newDescription, "iconResource", selectedIcon)
                        .addOnSuccessListener(aVoid -> {
                            // Aktualizacja lokalnej listy
                            finalEvent.setTitle(newTitle);
                            finalEvent.setDescription(newDescription);
                            finalEvent.setIconResource(selectedIcon);
                            notifyItemChanged(position);
                            Toast.makeText(calendarFragment.getContext(), "Wydarzenie zaktualizowane", Toast.LENGTH_SHORT).show();
                            Log.d("EventsAdapter", "Wydarzenie zaktualizowane: ID " + finalEvent.getId());

                            // Aktualizacja listy wydarzeń
                            calendarFragment.displayAllEventDatesForMonth(calendarFragment.currentYear, calendarFragment.currentMonth);
                            calendarFragment.updateEventsForSelectedDate();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(calendarFragment.getContext(), "Błąd podczas aktualizacji wydarzenia", Toast.LENGTH_SHORT).show();
                            Log.e("EventsAdapter", "Error updating document", e);
                        });
            } else {
                Toast.makeText(calendarFragment.getContext(), "Tytuł nie może być pusty", Toast.LENGTH_SHORT).show();
                Log.d("EventsAdapter", "Edycja wydarzenia anulowana: pusty tytuł");
            }
        });

        builder.setNegativeButton("Anuluj", null);
        builder.show();
    }

    /**
     * Metoda do wyświetlania dialogu potwierdzenia usunięcia wydarzenia.
     *
     * @param event    Wydarzenie do usunięcia.
     * @param position Pozycja w RecyclerView.
     */
    private void showDeleteConfirmationDialog(CalendarEvent event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(calendarFragment.getContext());
        builder.setTitle("Usuń wydarzenie");
        builder.setMessage("Czy na pewno chcesz usunąć to wydarzenie?");

        builder.setPositiveButton("Tak", (dialog, which) -> {
            eventsCollection.document(event.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        eventsList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(calendarFragment.getContext(), "Wydarzenie usunięte", Toast.LENGTH_SHORT).show();

                        // Aktualizacja listy wydarzeń
                        calendarFragment.displayAllEventDatesForMonth(calendarFragment.currentYear, calendarFragment.currentMonth);
                        calendarFragment.updateEventsForSelectedDate();
                        Log.d("EventsAdapter", "Wydarzenie usunięte: ID " + event.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(calendarFragment.getContext(), "Błąd podczas usuwania wydarzenia", Toast.LENGTH_SHORT).show();
                        Log.e("EventsAdapter", "Error deleting document", e);
                    });
        });

        builder.setNegativeButton("Nie", null);
        builder.show();
    }

    // Metoda do walidacji formatu daty (opcjonalnie)
    private boolean isValidDate(String date) {
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        return Pattern.matches(regex, date);
    }

    // Aktualizacja listy wydarzeń
    public void updateEventsList(List<CalendarEvent> newEvents) {
        this.eventsList = newEvents;
        notifyDataSetChanged();
    }

    /**
     * Klasa ViewHolder dla wydarzeń.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventDateTextView, eventTitleTextView;
        ImageView eventIconImageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            eventIconImageView = itemView.findViewById(R.id.eventIconImageView);
        }
    }
}
