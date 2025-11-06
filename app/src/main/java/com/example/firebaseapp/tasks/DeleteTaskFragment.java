//package com.example.firebaseapp.tasks;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.firebaseapp.R;
//import com.example.firebaseapp.clubs_home.ClubsHomeFragment;
//import com.example.firebaseapp.models.Task;
//import com.example.firebaseapp.notifications.TaskNotificationManager;
//import com.google.firebase.firestore.FirebaseFirestore;
//
///**
// * Fragment odpowiedzialny za usuwanie istniejącego zadania.
// */
//public class DeleteTaskFragment extends Fragment {
//
//    private Button deleteButton;
//    private String taskId;
//    private String clubName;
//    private FirebaseFirestore db;
//
//    private static final String TAG = "DeleteTaskFragment";
//
//    public DeleteTaskFragment() {
//        // Wymagany pusty konstruktor
//    }
//
//    /**
//     * Tworzenie nowej instancji fragmentu z przekazaniem ID zadania i nazwy klubu.
//     *
//     * @param taskId   ID zadania do usunięcia.
//     * @param clubName Nazwa klubu, do którego należy zadanie.
//     * @return Nowa instancja DeleteTaskFragment.
//     */
//    public static DeleteTaskFragment newInstance(String taskId, String clubName) {
//        DeleteTaskFragment fragment = new DeleteTaskFragment();
//        Bundle args = new Bundle();
//        args.putString("taskId", taskId);
//        args.putString("clubName", clubName);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        db = FirebaseFirestore.getInstance();
//
//        if (getArguments() != null) {
//            taskId = getArguments().getString("taskId");
//            clubName = getArguments().getString("clubName");
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);
//
//        deleteButton = view.findViewById(R.id.delete_task_button);
//
//        deleteButton.setOnClickListener(v -> confirmAndDeleteTask());
//
//        return view;
//    }
//
//    /**
//     * Wyświetla dialog potwierdzający usunięcie zadania.
//     */
//    private void confirmAndDeleteTask() {
//        new AlertDialog.Builder(requireContext())
//                .setTitle("Usuń zadanie")
//                .setMessage("Czy na pewno chcesz usunąć to zadanie?")
//                .setPositiveButton("Tak", (dialog, which) -> deleteTask())
//                .setNegativeButton("Nie", null)
//                .show();
//    }
//
//    /**
//     * Usuwa zadanie z Firestore i anuluje powiadomienia związane z tym zadaniem.
//     */
//    private void deleteTask() {
//        if (taskId == null || taskId.isEmpty()) {
//            Toast.makeText(getContext(), "Nieprawidłowe ID zadania", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Nieprawidłowe ID zadania w deleteTask");
//            return;
//        }
//
//        // Anulowanie powiadomień
//        TaskNotificationManager.cancelTaskNotifications(requireContext(), taskId);
//
//        // Usuwanie zadania z Firestore
//        db.collection("clubs").document(clubName).collection("tasks").document(taskId)
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(getContext(), "Zadanie zostało usunięte", Toast.LENGTH_SHORT).show();
//                    navigateToTasksList();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Nie udało się usunąć zadania", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Error deleting task", e);
//                });
//    }
//
//    /**
//     * Nawiguje użytkownika do listy zadań po pomyślnym usunięciu zadania.
//     */
//    private void navigateToTasksList() {
//        UserTasksFragment tasksListFragment = UserTasksFragment.newInstance(clubName);
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, tasksListFragment)
//                .addToBackStack(null)
//                .commit();
//    }
//}
