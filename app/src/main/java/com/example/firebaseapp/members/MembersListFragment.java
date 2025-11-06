package com.example.firebaseapp.members;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.adapters.MembersAdapter;
import com.example.firebaseapp.authentication.LoginFragment;
import com.example.firebaseapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MembersListFragment extends Fragment implements MembersAdapter.OnMemberClickListener {

    private RecyclerView recyclerViewMembers;
    private MembersAdapter membersAdapter;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String clubName; // Nazwa klubu przekazana jako argument

    private String currentUserRole;

    private static final String TAG = "MembersListFragment";

    // Zestaw do śledzenia już dodanych userId, aby uniknąć duplikatów
    private final Set<String> addedUserIds = new HashSet<>();

    // ViewModel
    private MembersViewModel membersViewModel;

    public MembersListFragment() {
        // Wymagany pusty konstruktor
    }

    /**
     * Tworzy nową instancję fragmentu z przekazaną nazwą klubu.
     *
     * @param clubName Nazwa klubu.
     * @return Nowa instancja MembersListFragment.
     */
    public static MembersListFragment newInstance(String clubName) {
        MembersListFragment fragment = new MembersListFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        // Inicjalizacja ViewModel
        membersViewModel = new ViewModelProvider(this).get(MembersViewModel.class);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (getArguments() != null) {
                clubName = getArguments().getString("clubName");
                Log.d(TAG, "Przekazana nazwa klubu: " + clubName);
            } else {
                Log.e(TAG, "Nie przekazano nazwy klubu do MembersListFragment");
                Toast.makeText(getContext(), "Nie przekazano nazwy klubu", Toast.LENGTH_SHORT).show();
                // Opcjonalnie: Nawigacja do poprzedniego fragmentu lub pokazanie komunikatu
                return;
            }

            // Pobierz dane aktualnego użytkownika, aby uzyskać jego rolę
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                currentUserRole = user.getRole();
                                Log.d(TAG, "Aktualny użytkownik: " + user.getFirstName() + " " + user.getLastName() + ", Rola: " + currentUserRole);
                                setupAdapter(); // Ustawienie adaptera po pobraniu roli
                            } else {
                                Log.e(TAG, "Dane użytkownika są puste");
                                Toast.makeText(getContext(), "Dane użytkownika są puste", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Dokument użytkownika nie istnieje");
                            Toast.makeText(getContext(), "Dokument użytkownika nie istnieje", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Błąd podczas pobierania danych użytkownika", e);
                        Toast.makeText(getContext(), "Błąd podczas pobierania danych użytkownika", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(getContext(), "Użytkownik nie jest zalogowany", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Użytkownik nie jest zalogowany");
            // Nawigacja do ekranu logowania poprzez fragment transaction
            navigateToLogin();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_members_list, container, false);

        recyclerViewMembers = view.findViewById(R.id.recyclerViewMembers);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // Początkowo ukryty

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        // Obserwacja danych z ViewModel
        membersViewModel.getMembersLiveData().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                Log.d(TAG, "MembersLiveData changed, updating list with " + users.size() + " members");
                addedUserIds.clear();
                for (User user : users) {
                    addedUserIds.add(user.getUserId());
                }
                if (membersAdapter != null) {
                    // Przekazanie nowej listy do adaptera
                    membersAdapter.updateMembersList(new ArrayList<>(users));
                    Log.d(TAG, "MembersAdapter updated with new data");
                }
            } else {
                Log.e(TAG, "Members LiveData is null");
            }
        });

        membersViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                Log.d(TAG, "Set ProgressBar visibility to " + (isLoading ? "VISIBLE" : "GONE"));
            } else {
                Log.e(TAG, "isLoading LiveData is null");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if (clubName != null && !clubName.isEmpty()) {
            fetchMembers(); // Pobierz dane za każdym razem, gdy fragment staje się widoczny
        }
    }

    /**
     * Ustawia adapter RecyclerView.
     */
    private void setupAdapter() {
        if (currentUserRole != null && membersAdapter == null) { // Ustaw adapter tylko raz
            List<User> currentMembers = membersViewModel.getMembersLiveData().getValue();
            if (currentMembers == null) {
                currentMembers = new ArrayList<>();
                membersViewModel.setMembers(currentMembers);
            }
            membersAdapter = new MembersAdapter(requireContext(), currentMembers, this, currentUserRole);
            recyclerViewMembers.setAdapter(membersAdapter);
            Log.d(TAG, "Adapter ustawiony");
        }
    }

    /**
     * Pobiera listę członków z Firestore dla danego klubu i aktualizuje RecyclerView.
     */
    private void fetchMembers() {
        Log.d(TAG, "fetchMembers called");
        if (clubName == null || clubName.isEmpty()) {
            Toast.makeText(getContext(), "Nieprawidłowa nazwa klubu", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Nieprawidłowa nazwa klubu: " + clubName);
            return;
        }

        membersViewModel.setIsLoading(true);
        Log.d(TAG, "Set isLoading to true");

        // Czyszczenie listy przed pobraniem nowych danych
        List<User> currentMembers = membersViewModel.getMembersLiveData().getValue();
        if (currentMembers != null) {
            currentMembers.clear();
            Log.d(TAG, "Cleared currentMembers list");
            membersViewModel.setMembers(currentMembers);
        }
        addedUserIds.clear();
        if (membersAdapter != null) {
            membersAdapter.notifyDataSetChanged();
            Log.d(TAG, "Cleared memberList and addedUserIds, notifyDataSetChanged()");
        }

        // Pobierz dokument klubu
        db.collection("clubs").document(clubName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> memberIds = (List<String>) documentSnapshot.get("members");
                        if (memberIds == null || memberIds.isEmpty()) {
                            Toast.makeText(getContext(), "Brak członków w tym klubie.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Pole members jest puste dla klubu: " + clubName);
                            membersViewModel.setIsLoading(false);
                            return;
                        }

                        Log.d(TAG, "Pobrano " + memberIds.size() + " identyfikatorów członków.");

                        // Firestore ma limit 10 elementów w whereIn, więc dzielimy na partie
                        int batchSize = 10;
                        int totalBatches = (int) Math.ceil((double) memberIds.size() / batchSize);
                        final int[] pendingBatches = {totalBatches};
                        Log.d(TAG, "Total batches: " + totalBatches);

                        for (int i = 0; i < memberIds.size(); i += batchSize) {
                            int end = Math.min(i + batchSize, memberIds.size());
                            List<String> batch = memberIds.subList(i, end);
                            Log.d(TAG, "Fetching batch from " + i + " to " + end);
                            fetchUsersInBatch(batch, pendingBatches);
                        }

                    } else {
                        Toast.makeText(getContext(), "Klub nie istnieje.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Dokument klubu nie istnieje: " + clubName);
                        membersViewModel.setIsLoading(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas pobierania klubu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching club: " + clubName, e);
                    membersViewModel.setIsLoading(false);
                });
    }

    /**
     * Pobiera dane użytkowników w partiach (batch) z Firestore i dodaje je do listy członków.
     *
     * @param userIds Lista identyfikatorów użytkowników do pobrania.
     * @param pendingBatches Tablica z jednym elementem, śledząca liczbę pozostałych batchy.
     */
    private void fetchUsersInBatch(List<String> userIds, int[] pendingBatches) {
        Log.d(TAG, "fetchUsersInBatch called with userIds: " + userIds);
        db.collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Fetched " + queryDocumentSnapshots.size() + " users in batch");
                    List<User> newUsers = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        user.setUserId(document.getId());

                        // Sprawdzenie, czy użytkownik już został dodany
                        if (!addedUserIds.contains(user.getUserId())) {
                            newUsers.add(user);
                            addedUserIds.add(user.getUserId());
                            Log.d(TAG, "Dodano członka: " + user.getFirstName() + " " + user.getLastName());
                        } else {
                            Log.d(TAG, "Członek już dodany: " + user.getFirstName() + " " + user.getLastName());
                        }
                    }

                    // Dodaj nowe użytkowników do ViewModel
                    if (!newUsers.isEmpty()) {
                        membersViewModel.addMembers(newUsers);
                        Log.d(TAG, "Dodano " + newUsers.size() + " nowych członków.");
                    } else {
                        Log.d(TAG, "Brak nowych członków do dodania.");
                    }

                    // Decrement pendingBatches i sprawdź, czy wszystko zostało załadowane
                    pendingBatches[0]--;
                    Log.d(TAG, "Pending batches: " + pendingBatches[0]);
                    if (pendingBatches[0] <= 0) {
                        membersViewModel.setIsLoading(false);
                        Log.d(TAG, "Wszystkie partie zostały załadowane.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas pobierania danych użytkowników", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user data", e);

                    // Decrement pendingBatches nawet jeśli batch się nie powiódł
                    pendingBatches[0]--;
                    Log.d(TAG, "Pending batches after failure: " + pendingBatches[0]);
                    if (pendingBatches[0] <= 0) {
                        membersViewModel.setIsLoading(false);
                        Log.d(TAG, "Wszystkie partie zostały załadowane (część nieudana).");
                    }
                });
    }

    /**
     * Implementacja metody z interfejsu OnMemberClickListener.
     *
     * @param member Wybrany użytkownik.
     */
    @Override
    public void onMemberClick(User member) {
        Log.d(TAG, "onMemberClick called for userId: " + member.getUserId());
        // Przejdź do MemberDetailFragment z przekazanym userId
        MemberDetailFragment detailFragment = MemberDetailFragment.newInstance(member.getUserId());

        // Wykonaj transakcję fragmentu
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();

        Log.d(TAG, "Przejście do MemberDetailFragment dla użytkownika: " + member.getUserId());
    }

    /**
     * Nawiguje do LoginFragment.
     */
    private void navigateToLogin() {
        LoginFragment loginFragment = new LoginFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .addToBackStack(null)
                .commit();
        Log.d(TAG, "Nawigacja do LoginFragment");
    }
}
