package com.example.mpt_app.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mpt_app.R;
import com.example.mpt_app.adapters.TasksAdapter;
import com.example.mpt_app.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserTasksFragment extends Fragment implements TasksAdapter.OnTaskClickListener {

    private RecyclerView recyclerViewTasks;
    private TasksAdapter tasksAdapter;
    private List<Task> taskList;
    private String clubName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "UserTasksFragment";

    public static UserTasksFragment newInstance(String clubName) {
        UserTasksFragment fragment = new UserTasksFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            clubName = getArguments().getString("clubName");
            Log.d(TAG, "clubName: " + clubName);
        } else {
            Log.e(TAG, "Brak nazwy klubu w argumentach UserTasksFragment");
        }

        // Dodanie niestandardowego callbacka dla przycisku wstecz
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                // Zastąp fragment DetailClubFragment
//                DetailClubFragment detailClubFragment = new DetailClubFragment();
//                requireActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, detailClubFragment)
//                        .commit();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tasks, container, false);

        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksAdapter(getContext(), taskList, this); // Przekazanie 'this' jako listener
        recyclerViewTasks.setAdapter(tasksAdapter);

        fetchUserTasks();

        // Opcjonalnie: Ustawienie kliknięcia przycisku dodawania zadania
        Button buttonAddTask = view.findViewById(R.id.buttonAddTask);
        buttonAddTask.setOnClickListener(v -> {
            if (clubName != null) {
                // Przejście do CreateTaskFragment
                CreateTaskFragment createTaskFragment = CreateTaskFragment.newInstance(clubName);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createTaskFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Błąd: Nazwa klubu jest nieznana", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "clubName is null when trying to add task");
            }
        });

        return view;
    }

    @Override
    public void onTaskClick(Task task) {
        if (clubName != null && task.getId() != null) {
            EditTaskFragment editTaskFragment = EditTaskFragment.newInstance(clubName, task.getId());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editTaskFragment)
                    .addToBackStack(null) // Dodanie do back stack
                    .commit();
        } else {
            Toast.makeText(getContext(), "Błąd: Nazwa klubu lub ID zadania jest nieznane", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "clubName or taskId is null in onTaskClick");
        }
    }

    private void fetchUserTasks() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Błąd: Użytkownik nie jest zalogowany", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Current user is null in fetchUserTasks");
            return;
        }

        if (clubName == null) {
            Toast.makeText(getContext(), "Błąd: Nazwa klubu jest nieznana", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "clubName is null in fetchUserTasks");
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Fetching tasks for userId: " + currentUserId);

        // Najpierw pobierz rolę użytkownika
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Log.d(TAG, "User role: " + role);
                        if ("admin".equals(role) || "prezes".equals(role)) {
                            // Użytkownik jest adminem lub prezesem - pobierz wszystkie zadania w klubie
                            db.collection("clubs").document(clubName).collection("tasks")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        taskList.clear();
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            Task task = document.toObject(Task.class);
                                            task.setId(document.getId());
                                            taskList.add(task);
                                            Log.d(TAG, "Fetched task: " + task.getTitle());
                                        }
                                        tasksAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Błąd podczas pobierania zadań", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Error fetching tasks for admin/prezes", e);
                                    });
                        } else {
                            // Normalny użytkownik - pobierz tylko przypisane zadania
                            db.collection("clubs").document(clubName).collection("tasks")
                                    .whereArrayContains("assignedTo", currentUserId)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        taskList.clear();
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            Task task = document.toObject(Task.class);
                                            task.setId(document.getId());
                                            taskList.add(task);
                                            Log.d(TAG, "Fetched task: " + task.getTitle());
                                        }
                                        tasksAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Błąd podczas pobierania zadań", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Error fetching tasks for user", e);
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Dokument użytkownika nie istnieje", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "User document does not exist for userId: " + currentUserId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas pobierania roli użytkownika", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user role in fetchUserTasks", e);
                });
    }
}
