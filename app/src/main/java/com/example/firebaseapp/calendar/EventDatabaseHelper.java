//// EventDatabaseHelper.java
//package com.example.firebaseapp.calendar;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import androidx.annotation.Nullable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EventDatabaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "events.db";
//    private static final int DATABASE_VERSION = 1;
//
//    private static final String TABLE_EVENTS = "events";
//    private static final String COLUMN_ID = "id";
//    private static final String COLUMN_DATE = "date"; // Format: yyyy-MM-dd
//    private static final String COLUMN_TITLE = "title";
//
//    public EventDatabaseHelper(@Nullable Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TABLE = "CREATE TABLE " + TABLE_EVENTS + " (" +
//                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                COLUMN_DATE + " TEXT, " +
//                COLUMN_TITLE + " TEXT)";
//        db.execSQL(CREATE_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
//        onCreate(db);
//    }
//
//    // Dodawanie wydarzenia
//    public void addEvent(String date, String title) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_DATE, date);
//        values.put(COLUMN_TITLE, title);
//        db.insert(TABLE_EVENTS, null, values);
//        db.close();
//    }
//
//    // Pobieranie wydarzeń po dacie
//    public List<String> getEventsByDate(String date) {
//        List<String> events = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_TITLE},
//                COLUMN_DATE + "=?", new String[]{date}, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                events.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return events;
//    }
//
//    // Pobieranie wszystkich dat z wydarzeniami
//    public List<String> getAllEventDates() {
//        List<String> dates = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_DATE},
//                null, null, COLUMN_DATE, null, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                dates.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return dates;
//    }
//
//    // Aktualizacja tytułu wydarzenia
//    public void updateEvent(int id, String newTitle) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_TITLE, newTitle);
//        db.update(TABLE_EVENTS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
//        db.close();
//    }
//
//    // Aktualizacja daty wydarzenia
//    public void updateEventDate(int id, String newDate) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_DATE, newDate);
//        db.update(TABLE_EVENTS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
//        db.close();
//    }
//
//    // Usuwanie wydarzenia
//    public void deleteEvent(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_EVENTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
//        db.close();
//    }
//
//    // Pobieranie wszystkich wydarzeń dla danego miesiąca
//    public List<CalendarEvent> getEventsForMonth(int year, int month) {
//        List<CalendarEvent> events = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        String monthStr = String.format("%04d-%02d", year, month + 1); // Format: yyyy-MM
//        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_TITLE},
//                COLUMN_DATE + " LIKE ?", new String[]{monthStr + "-%"}, null, null, COLUMN_DATE + " ASC");
//
//        if (cursor.moveToFirst()) {
//            do {
//                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
//                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
//                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
//                events.add(new CalendarEvent(id, date, title));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return events;
//    }
//}
