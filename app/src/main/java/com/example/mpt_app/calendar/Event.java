//package com.example.firebaseapp.calendar;
//
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
//public class Event {
//    private int id;
//    private String date; // Format: yyyy-MM-dd
//    private String title;
//
//    public Event(int id, String date, String title) {
//        this.id = id;
//        this.date = date;
//        this.title = title;
//    }
//
//    // Gettery i Settery
//    public int getId() {
//        return id;
//    }
//
//    public String getDate() {
//        return date;
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
//    // Opcjonalnie: Metoda do formatowania daty
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
