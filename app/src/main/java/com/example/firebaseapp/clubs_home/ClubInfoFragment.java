package com.example.firebaseapp.clubs_home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firebaseapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubInfoFragment extends Fragment {

    private String clubName;
    private String clubDescription;
    private int clubImageResId;

    public static ClubInfoFragment newInstance(String clubName, String clubDescription, int clubImageResId) {
        ClubInfoFragment fragment = new ClubInfoFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        args.putString("clubDescription", clubDescription);
        args.putInt("clubImageResId", clubImageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sprawdź argumenty
        if (getArguments() != null) {
            clubName = getArguments().getString("clubName");
            clubDescription = getArguments().getString("clubDescription");
            clubImageResId = getArguments().getInt("clubImageResId");
        }

        // Inicjalizuj ekran ładowania
        View loadingView = inflater.inflate(R.layout.loading_screen, container, false);

        // Wczytaj szczegóły klubu w tle
        fetchClubDetails();

        return loadingView; // Zwróć widok ładowania
    }

    private void fetchClubDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clubs").document(clubName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        clubDescription = documentSnapshot.getString("description");

                        // Ustaw baner (możesz to dostosować)
                        String bannerImageName = documentSnapshot.getString("bannerURL");
                        int bannerImageResId = getDrawableResourceId(bannerImageName);

                        // Dodaj opóźnienie do wyświetlenia szczegółów
                        new Handler().postDelayed(() -> {
                            goToDetailClubFragment(bannerImageResId);
                        }, 0); // Opóźnienie 2 sekundy
                    }
                })
                .addOnFailureListener(e -> {
                    // Obsłuż błędy
                });
    }

    private void goToDetailClubFragment(int bannerImageResId) {
        DetailClubFragment detailClubFragment = DetailClubFragment.newInstance(clubName, clubDescription, clubImageResId, bannerImageResId);

        // Użyj FragmentManager do zamiany fragmentów
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, detailClubFragment);
        fragmentTransaction.addToBackStack(null); // Dodaj do stosu fragmentów
        fragmentTransaction.commit();
    }

    private int getDrawableResourceId(String resourceName) {
        return requireContext().getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());
    }
}
