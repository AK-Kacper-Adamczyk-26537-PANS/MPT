//// UnifiedCalendarEvent.java
//package com.example.firebaseapp.calendar;
//
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
///**
// * Klasa reprezentująca uniwersalne wydarzenie w kalendarzu.
// * Może to być bezpośrednie wydarzenie, zadanie lub notatka.
// */
//public class UnifiedCalendarEvent {
//    private String id;
//    private String date; // Format: yyyy-MM-dd
//    private String title;
//    private String description;
//    private String type; // "event", "task", "note"
//    private String sourceId; // ID w oryginalnej kolekcji (events/tasks/notes)
//
//    // Konstruktor bezargumentowy wymagany przez Firestore
//    public UnifiedCalendarEvent() {}
//
//    public UnifiedCalendarEvent(String id, String date, String title, String description, String type, String sourceId) {
//        this.id = id;
//        this.date = date;
//        this.title = title;
//        this.description = description;
//        this.type = type;
//        this.sourceId = sourceId;
//    }
//
//    // Gettery i Settery
//
//    public String getId() {
//        return id;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getSourceId() {
//        return sourceId;
//    }
//
//    public void setSourceId(String sourceId) {
//        this.sourceId = sourceId;
//    }
//
//    /**
//     * Metoda do formatowania daty na czytelny format.
//     *
//     * @return Sformatowana data jako String.
//     */
//    public String getFormattedDate() {
//        try {
//            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
//            return targetFormat.format(originalFormat.parse(date));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return date;
//        }
//    }
//}
