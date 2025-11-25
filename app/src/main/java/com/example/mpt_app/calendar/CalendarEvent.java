// CalendarEvent.java
package com.example.mpt_app.calendar;

import com.google.firebase.firestore.DocumentId;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarEvent {
    @DocumentId
    private String id; // Używamy String zamiast int dla identyfikatora dokumentu w Firestore
    private String date; // Format: yyyy-MM-dd
    private String title;
    private String description; // Dodane pole dla opisu wydarzenia
    private int iconResource; // Pole dla ikony wydarzenia

    // Konstruktor bezargumentowy wymagany przez Firestore
    public CalendarEvent() {
    }

    // Konstruktor z wszystkimi parametrami
    public CalendarEvent(String id, String date, String title, String description, int iconResource) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        this.iconResource = iconResource;
    }

    // Gettery i Settery
    public String getId() {
        return id;
    }

    public void setId(String id) { // Dodana metoda
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() { // Dodana metoda
        return description;
    }

    public void setDescription(String description) { // Dodana metoda
        this.description = description;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    // Metoda do formatowania daty
    public String getFormattedDate() {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return targetFormat.format(originalFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }
}
