// DatePickerFragment.java
package com.example.mpt_app.calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public interface DatePickerListener {
        void onDateSelected(int year, int month, int day);
    }

    private DatePickerListener listener;

    public DatePickerFragment(DatePickerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Ustawienie początkowej daty na bieżącą
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Tworzenie DatePickerDialog
        return new DatePickerDialog(getActivity(), (view, selectedYear, selectedMonth, selectedDay) -> {
            if (listener != null) {
                listener.onDateSelected(selectedYear, selectedMonth, selectedDay);
            }
        }, year, month, day);
    }
}
