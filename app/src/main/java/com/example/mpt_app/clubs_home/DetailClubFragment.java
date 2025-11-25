package com.example.mpt_app.clubs_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout; // Użyj LinearLayout zamiast ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mpt_app.R;
import com.example.mpt_app.calendar.CalendarFragment;
import com.example.mpt_app.members.MembersListFragment;
import com.example.mpt_app.notes.NotesListFragment;
import com.example.mpt_app.tasks.UserTasksFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailClubFragment extends Fragment {

    private TextView clubNameTextView, clubDescriptionTextView;
    private ImageView clubImageView, bannerImageView, fullWidthImageView, galleryBannerImageView; // Dodano galleryBannerImageView
    private LinearLayout buttonTasks, buttonNotes, buttonMembers; // Zmiana typu na LinearLayout
    private LinearLayout buttonChat, buttonCalendar, buttonEquipment;
    // Nowe przyciski i sekcje
    private LinearLayout buttonStatistics, buttonQuizzes;
    private LinearLayout sectionNews, sectionAnnouncements;

    private String clubName;
    private String clubDescription;
    private int clubImageResId;
    private int bannerImageResId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public DetailClubFragment() {
        // Wymagany pusty konstruktor
    }

    public static DetailClubFragment newInstance(String clubName, String clubDescription, int clubImageResId, int bannerImageResId) {
        DetailClubFragment fragment = new DetailClubFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        args.putString("clubDescription", clubDescription);
        args.putInt("clubImageResId", clubImageResId);
        args.putInt("bannerImageResId", bannerImageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            clubName = getArguments().getString("clubName");
            clubDescription = getArguments().getString("clubDescription");
            clubImageResId = getArguments().getInt("clubImageResId");
            bannerImageResId = getArguments().getInt("bannerImageResId");
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ClubsHomeFragment())
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_details, container, false);

        clubNameTextView = view.findViewById(R.id.club_name);
        clubDescriptionTextView = view.findViewById(R.id.club_description);
        clubImageView = view.findViewById(R.id.club_image);
        bannerImageView = view.findViewById(R.id.banner_image);
        fullWidthImageView = view.findViewById(R.id.fullimage);
        galleryBannerImageView = view.findViewById(R.id.gallery_banner); // Inicjalizacja gallery_banner

        // Graficzne przyciski
        buttonTasks = view.findViewById(R.id.buttonTasks);
        buttonNotes = view.findViewById(R.id.buttonNotes);
        buttonMembers = view.findViewById(R.id.buttonMembers);

        buttonChat = view.findViewById(R.id.buttonChat);
        buttonCalendar = view.findViewById(R.id.buttonCalendar);
        buttonEquipment = view.findViewById(R.id.buttonEquipment);

        // Nowe przyciski i sekcje
        buttonStatistics = view.findViewById(R.id.buttonStatistics);
        buttonQuizzes = view.findViewById(R.id.buttonQuizzes);
        sectionNews = view.findViewById(R.id.sectionNews);
        sectionAnnouncements = view.findViewById(R.id.sectionAnnouncements);

        clubNameTextView.setText(clubName);
        clubDescriptionTextView.setText(clubDescription);
        clubImageView.setImageResource(clubImageResId);
        bannerImageView.setImageResource(bannerImageResId);
        // fullWidthImageView.setImageResource(R.drawable.girl_it); // Zastąp własnym obrazem

        checkUserRoleAndSetButtonVisibility();

        // Obsługa kliknięć dla graficznych przycisków
        buttonTasks.setOnClickListener(v -> navigateToFragment(UserTasksFragment.newInstance(clubName)));
        buttonNotes.setOnClickListener(v -> navigateToFragment(NotesListFragment.newInstance(clubName)));
        buttonMembers.setOnClickListener(v -> navigateToFragment(MembersListFragment.newInstance(clubName)));

        buttonChat.setOnClickListener(v ->
                Toast.makeText(getContext(), "Funkcja czatu w fazie programowania", Toast.LENGTH_SHORT).show());

        buttonCalendar.setOnClickListener(v ->
                navigateToFragment(new CalendarFragment()));

        buttonEquipment.setOnClickListener(v ->
                Toast.makeText(getContext(), "Lista sprzętu w fazie programowania", Toast.LENGTH_SHORT).show());

        // Obsługa kliknięć dla nowych przycisków
        buttonStatistics.setOnClickListener(v ->
                Toast.makeText(getContext(), "Funkcja statystyk w fazie programowania", Toast.LENGTH_SHORT).show());

        buttonQuizzes.setOnClickListener(v ->
                Toast.makeText(getContext(), "Funkcja quizów i ankiet w fazie programowania", Toast.LENGTH_SHORT).show());

        // Opcjonalnie: Kliknięcie na sekcje Nowości i Ogłoszeń
        sectionNews.setOnClickListener(v ->
                Toast.makeText(getContext(), "Sekcja Nowości w fazie programowania", Toast.LENGTH_SHORT).show());

        sectionAnnouncements.setOnClickListener(v ->
                Toast.makeText(getContext(), "Sekcja Ogłoszeń w fazie programowania", Toast.LENGTH_SHORT).show());

        // **Dodanie OnClickListener dla galerii**
        galleryBannerImageView.setOnClickListener(v ->
                Toast.makeText(getContext(), "Funkcja galerii w fazie programowania", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void checkUserRoleAndSetButtonVisibility() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Ukryj przyciski, jeśli użytkownik nie jest zalogowany
            buttonTasks.setVisibility(View.GONE);
            buttonNotes.setVisibility(View.GONE);
            buttonMembers.setVisibility(View.GONE);
            buttonStatistics.setVisibility(View.GONE);
            buttonQuizzes.setVisibility(View.GONE);
            return;
        }

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        // Przykład: jeśli użytkownik jest adminem lub prezesem, pokaż dodatkowe opcje
                        if ("admin".equals(role) || "prezes".equals(role)) {
                            // Możesz tutaj pokazać dodatkowe przyciski lub funkcje
                            // np. pokazanie przycisku utworzenia zadania
                            // buttonCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            // Ukryj przyciski, jeśli użytkownik nie ma odpowiedniej roli
                            buttonMembers.setVisibility(View.GONE);
//                            buttonStatistics.setVisibility(View.GONE);
//                            buttonQuizzes.setVisibility(View.GONE);
                        }
                    } else {
                        // Ukryj przyciski, jeśli dokument nie istnieje
                        buttonTasks.setVisibility(View.GONE);
                        buttonNotes.setVisibility(View.GONE);
                        buttonMembers.setVisibility(View.GONE);
                        buttonStatistics.setVisibility(View.GONE);
                        buttonQuizzes.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas sprawdzania roli użytkownika", Toast.LENGTH_SHORT).show();
                    buttonTasks.setVisibility(View.GONE);
                    buttonNotes.setVisibility(View.GONE);
                    buttonMembers.setVisibility(View.GONE);
                    buttonStatistics.setVisibility(View.GONE);
                    buttonQuizzes.setVisibility(View.GONE);
                });
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
