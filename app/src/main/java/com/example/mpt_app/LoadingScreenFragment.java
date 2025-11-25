package com.example.mpt_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoadingScreenFragment extends Fragment {

    private TextView clubNameTextView;
    private TextView clubDescriptionTextView;

    public static LoadingScreenFragment newInstance(String clubName, String clubDescription) {
        LoadingScreenFragment fragment = new LoadingScreenFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        args.putString("clubDescription", clubDescription);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_screen, container, false);

        // Inicjalizacja TextView
        clubNameTextView = view.findViewById(R.id.loading_club_name);
        clubDescriptionTextView = view.findViewById(R.id.loading_club_description);

        // Ustaw nazwy klubu i opisy
        if (getArguments() != null) {
            clubNameTextView.setText(getArguments().getString("clubName", "Klub"));
            clubDescriptionTextView.setText(getArguments().getString("clubDescription", "Opis klubu"));
        }

        return view;
    }
}

//package com.example.firebaseapp;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//public class LoadingScreenFragment extends Fragment {
//
//    private TextView clubNameTextView;
//    private TextView clubDescriptionTextView;
//
//    public static LoadingScreenFragment newInstance(String clubName, String clubDescription) {
//        LoadingScreenFragment fragment = new LoadingScreenFragment();
//        Bundle args = new Bundle();
//        args.putString("clubName", clubName);
//        args.putString("clubDescription", clubDescription);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.loading_screen, container, false);
//
//        // Inicjalizacja TextView
//        clubNameTextView = view.findViewById(R.id.loading_club_name);
//        clubDescriptionTextView = view.findViewById(R.id.loading_club_description);
//
//        // Ustaw nazwy klubu i opisy
//        if (getArguments() != null) {
//            clubNameTextView.setText(getArguments().getString("clubName", "Klub"));
//            clubDescriptionTextView.setText(getArguments().getString("clubDescription", "Opis klubu"));
//        }
//
//        return view;
//    }
//}
