//package com.example.firebaseapp.clubs_home;
//
//import androidx.fragment.app.Fragment;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.firebaseapp.R;
//import com.example.firebaseapp.models.Club;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ClubsHomeFragment extends Fragment {
//
//    private RecyclerView clubsRecyclerView;
//    private List<Club> clubsList;
//    private ClubsListAdapter clubsListAdapter;
//    private static final String TAG = "HomeFragment";
//    private static final int REQUEST_CODE_ADD_CLUB = 1001;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        clubsRecyclerView = view.findViewById(R.id.clubs_recycler_view);
//        clubsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//
//        clubsList = new ArrayList<>();
//        clubsListAdapter = new ClubsListAdapter(getContext(), clubsList);
//        clubsRecyclerView.setAdapter(clubsListAdapter);
//
//        // Ładowanie klubów z Firestore
//        loadClubsFromFirestore();
//
//        return view;
//    }
//
//    public void loadClubsFromFirestore() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        clubsList.clear();  // Czyścimy listę przed załadowaniem nowych danych
//
//        db.collection("clubs").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                String clubName = document.getString("name");
//                String clubDescription = document.getString("description");
//                String imageName = document.getString("imageName"); // Pobieramy nazwę pliku obrazka z Firebase
//                String bannerUrl = document.getString("bannerUrl"); // Pobieramy URL banera
//
//                // Tworzymy nowy obiekt Club z pobranymi danymi
//                Club club = new Club(clubName, clubDescription, imageName, bannerUrl);
//                clubsList.add(club);
//            }
//            clubsListAdapter.notifyDataSetChanged();  // Odświeżamy RecyclerView po załadowaniu danych
//        }).addOnFailureListener(e -> Log.w(TAG, "Error loading clubs: ", e));
//    }
//
//
//    // Metoda konwertująca nazwę pliku na zasób w drawable
//    private int getResourceIdFromFileName(String fileName) {
//        return getResources().getIdentifier(fileName.replace(".png", ""), "drawable", getContext().getPackageName());
//    }
//
//}

package com.example.mpt_app.clubs_home;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mpt_app.R;
import com.example.mpt_app.models.Club;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ClubsHomeFragment extends Fragment {

    private RecyclerView clubsRecyclerView;
    private List<Club> clubsList;
    private ClubsListAdapter clubsListAdapter;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        loadClubsFromFirestore();
        return view;
    }

    private void initializeViews(View view) {
        clubsRecyclerView = view.findViewById(R.id.clubs_recycler_view);
        clubsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        clubsList = new ArrayList<>();
        clubsListAdapter = new ClubsListAdapter(getContext(), clubsList);
        clubsRecyclerView.setAdapter(clubsListAdapter);
    }

    private void loadClubsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        clubsList.clear(); // Czyścimy listę przed załadowaniem nowych danych

        db.collection("clubs").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Club club = createClubFromDocument(document);
                clubsList.add(club);
            }
            clubsListAdapter.notifyDataSetChanged(); // Odświeżamy RecyclerView po załadowaniu danych
        }).addOnFailureListener(e -> Log.w(TAG, "Error loading clubs: ", e));
    }

    private Club createClubFromDocument(QueryDocumentSnapshot document) {
        String clubName = document.getString("name");
        String clubDescription = document.getString("description");
        String imageName = document.getString("imageName"); // Pobieramy nazwę pliku obrazka z Firebase
        String bannerUrl = document.getString("bannerUrl"); // Pobieramy URL banera
        return new Club(clubName, clubDescription, imageName, bannerUrl);
    }
}
