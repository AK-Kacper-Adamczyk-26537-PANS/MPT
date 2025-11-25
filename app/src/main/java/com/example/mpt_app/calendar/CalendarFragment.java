// CalendarFragment.java
package com.example.mpt_app.calendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpt_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fragment odpowiedzialny za wyświetlanie kalendarza zintegrowanego z wydarzeniami.
 */
public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private CollectionReference eventsCollection;

    private String selectedDate;
    private Button addEventButton;
    private TextView eventsLabel, eventsList, monthEventsLabel;
    private RecyclerView monthEventsRecyclerView;
    private EventsAdapter eventsAdapter;

    // Przechowywanie aktualnego miesiąca i roku
    protected int currentYear;
    protected int currentMonth;

    private static final String TAG = "CalendarFragment";

    public CalendarFragment() {
        // Wymagany pusty konstruktor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Inicjalizacja Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Użytkownik nie jest zalogowany. Przekieruj do ekranu logowania.
            Toast.makeText(getContext(), "Proszę się zalogować", Toast.LENGTH_SHORT).show();
            // Możesz tutaj dodać kod do przekierowania do ekranu logowania
            return view;
        }

        // Inicjalizacja referencji do kolekcji wydarzeń użytkownika
        eventsCollection = firestore.collection("users").document(currentUser.getUid()).collection("events");

        calendarView = view.findViewById(R.id.calendarView);
        addEventButton = view.findViewById(R.id.addEventButton);
        eventsLabel = view.findViewById(R.id.eventsLabel);
        eventsList = view.findViewById(R.id.eventsList);
        monthEventsLabel = view.findViewById(R.id.monthEventsLabel);
        monthEventsRecyclerView = view.findViewById(R.id.monthEventsRecyclerView);

        // Inicjalizacja RecyclerView
        monthEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsAdapter = new EventsAdapter(this, new ArrayList<>(), eventsCollection);
        monthEventsRecyclerView.setAdapter(eventsAdapter);

        // Ustawienie początkowego miesiąca i roku
        Calendar initialCalendar = Calendar.getInstance();
        currentYear = initialCalendar.get(Calendar.YEAR);
        currentMonth = initialCalendar.get(Calendar.MONTH);

        // Ustawienie wybranej daty na pierwszy dzień miesiąca
        initialCalendar.set(Calendar.DAY_OF_MONTH, 1);
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(initialCalendar.getTime());
        calendarView.setDate(initialCalendar.getTimeInMillis(), false, true);

        // Wyświetlenie listy wydarzeń dla początkowego miesiąca
        displayAllEventDatesForMonth(currentYear, currentMonth);

        // Ustawienie OnDateChangeListener
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            // Aktualizacja bieżącego miesiąca i roku
            currentYear = year;
            currentMonth = month;
            // Aktualizacja listy wydarzeń dla wybranego dnia i miesiąca
            updateEventsForSelectedDate();
        });

        // Ustawienie kliknięcia przycisku dodawania wydarzenia
        addEventButton.setOnClickListener(v -> showAddEventDialog());

        return view;
    }

    /**
     * Metoda do wyświetlania wszystkich wydarzeń dla danego miesiąca.
     *
     * @param year  Rok
     * @param month Miesiąc (0-based)
     */
    public void displayAllEventDatesForMonth(int year, int month) {
        String monthStr = String.format(Locale.getDefault(), "%04d-%02d", year, month + 1);
        Query query = eventsCollection.whereGreaterThanOrEqualTo("date", monthStr + "-01")
                .whereLessThanOrEqualTo("date", monthStr + "-31")
                .orderBy("date", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CalendarEvent> monthEvents = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CalendarEvent event = document.toObject(CalendarEvent.class);
                    event.setId(document.getId()); // Ustawienie ID dokumentu
                    monthEvents.add(event);
                }

                if (monthEvents.isEmpty()) {
                    monthEventsLabel.setVisibility(View.VISIBLE);
                    monthEventsLabel.setText("Brak zaplanowanych wydarzeń na ten miesiąc.");
                } else {
                    monthEventsLabel.setVisibility(View.VISIBLE);
                    monthEventsLabel.setText("Wydarzenia na ten miesiąc:");
                    eventsAdapter.updateEventsList(monthEvents);
                }

                Log.d(TAG, "Wyświetlono " + monthEvents.size() + " wydarzeń dla miesiąca " + (month + 1) + "/" + year);
            } else {
                Toast.makeText(getContext(), "Błąd podczas pobierania wydarzeń", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Metoda do aktualizacji wydarzeń dla wybranej daty.
     */
    public void updateEventsForSelectedDate() {
        Log.d(TAG, "Selected Date: " + selectedDate);
        Query query = eventsCollection.whereEqualTo("date", selectedDate);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> events = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    if (title != null) {
                        events.add(title);
                    }
                }

                if (!events.isEmpty()) {
                    StringBuilder eventsText = new StringBuilder();
                    for (String event : events) {
                        eventsText.append("- ").append(event).append("\n");
                    }
                    eventsList.setText(eventsText.toString());
                    eventsLabel.setVisibility(View.VISIBLE);
                    eventsList.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Znaleziono " + events.size() + " wydarzeń dla daty " + selectedDate);
                } else {
                    eventsLabel.setVisibility(View.GONE);
                    eventsList.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Brak wydarzeń dla tej daty", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Brak wydarzeń dla daty " + selectedDate);
                }

                // Aktualizacja listy wydarzeń dla miesiąca
                displayAllEventDatesForMonth(currentYear, currentMonth);
            } else {
                Toast.makeText(getContext(), "Błąd podczas pobierania wydarzeń", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Metoda do wyświetlania dialogu dodawania wydarzenia.
     */
    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Dodaj wydarzenie");

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_event, null, false);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.eventTitleInput);
        EditText eventDescriptionInput = dialogView.findViewById(R.id.eventDescriptionInput);
        RadioGroup iconRadioGroup = dialogView.findViewById(R.id.iconRadioGroup);
        RadioButton icon1RadioButton = dialogView.findViewById(R.id.icon1RadioButton);
        RadioButton icon2RadioButton = dialogView.findViewById(R.id.icon2RadioButton);
        RadioButton icon3RadioButton = dialogView.findViewById(R.id.icon3RadioButton);

        // Ustaw domyślne zaznaczenie pierwszej ikony
        icon1RadioButton.setChecked(true);

        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String eventTitle = eventTitleInput.getText().toString().trim();
            String eventDescription = eventDescriptionInput.getText().toString().trim();

            if (!eventTitle.isEmpty()) {
                // Pobranie wybranej ikony
                int selectedIcon = R.drawable.ic_event1; // Domyślna ikona
                int selectedId = iconRadioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.icon1RadioButton) {
                    selectedIcon = R.drawable.ic_event1;
                } else if (selectedId == R.id.icon2RadioButton) {
                    selectedIcon = R.drawable.ic_event2;
                } else if (selectedId == R.id.icon3RadioButton) {
                    selectedIcon = R.drawable.ic_event3;
                }

                // Tworzenie nowego wydarzenia z opisem
                CalendarEvent newEvent = new CalendarEvent(null, selectedDate, eventTitle, eventDescription, selectedIcon);

                // Dodawanie do Firestore
                eventsCollection.add(newEvent)
                        .addOnSuccessListener(documentReference -> {
                            newEvent.setId(documentReference.getId()); // Ustawienie ID
                            Toast.makeText(getContext(), "Dodano wydarzenie", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Wydarzenie dodane z ID: " + documentReference.getId());
                            displayAllEventDatesForMonth(currentYear, currentMonth);
                            updateEventsForSelectedDate();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Błąd podczas dodawania wydarzenia", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error adding document", e);
                        });
            } else {
                Toast.makeText(getContext(), "Wpisz tytuł wydarzenia", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Dodawanie wydarzenia anulowane: pusty tytuł");
            }
        });

        builder.setNegativeButton("Anuluj", null);
        builder.create().show();
    }
}
