package com.example.mpt_app.tasks;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpt_app.R;
import com.example.mpt_app.adapters.MembersSelectAdapter;
import com.example.mpt_app.models.Task;
import com.example.mpt_app.models.User;
import com.example.mpt_app.notifications.TaskNotificationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateTaskFragment extends Fragment implements MembersSelectAdapter.OnMemberSelectListener {

    private EditText editTextTaskTitle, editTextTaskDescription;
    private RecyclerView recyclerViewMembers;
    private Button buttonCreateTask, buttonSelectDueDate, buttonSelectStartDate, buttonSelectEndDate;
    private TextView textViewDueDate, textViewStartDate, textViewEndDate;
    private Spinner spinnerPriority;
    private RatingBar ratingBarProgress;

    private List<User> allMembers = new ArrayList<>();
    private List<String> selectedMemberIds = new ArrayList<>();
    private MembersSelectAdapter membersSelectAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String clubName;

    private static final String TAG = "CreateTaskFragment";

    // Zmienne do przechowywania wybranych dat
    private Date selectedStartDate = null;
    private Date selectedEndDate = null;
    private Date selectedDueDate = null;

    // Priorytety do wyboru
    private final String[] priorities = {"Wysoki", "Średni", "Niski"};

    public CreateTaskFragment() {
        // Wymagany pusty konstruktor
    }

    public static CreateTaskFragment newInstance(String clubName) {
        CreateTaskFragment fragment = new CreateTaskFragment();
        Bundle args = new Bundle();
        args.putString("clubName", clubName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            clubName = getArguments().getString("clubName");
            Log.d(TAG, "clubName: " + clubName);
        } else {
            Log.e(TAG, "Brak argumentów w CreateTaskFragment");
        }

        // Upewnij się, że kanał powiadomień jest utworzony
        TaskNotificationManager.createNotificationChannel(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);

        // Inicjalizacja widoków
        editTextTaskTitle = view.findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = view.findViewById(R.id.editTextTaskDescription);
        recyclerViewMembers = view.findViewById(R.id.recyclerViewMembers);
        buttonCreateTask = view.findViewById(R.id.buttonCreateTask);
        buttonSelectDueDate = view.findViewById(R.id.buttonSelectDueDate);
        buttonSelectStartDate = view.findViewById(R.id.buttonSelectStartDate);
        buttonSelectEndDate = view.findViewById(R.id.buttonSelectEndDate);
        textViewDueDate = view.findViewById(R.id.textViewDueDate);
        textViewStartDate = view.findViewById(R.id.textViewStartDate);
        textViewEndDate = view.findViewById(R.id.textViewEndDate);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        ratingBarProgress = view.findViewById(R.id.ratingBarProgress);

        setupSpinnerPriority();
        setupRecyclerView();
        fetchClubMembers();
        checkUserRoleAndSetButtonVisibility();

        buttonCreateTask.setOnClickListener(v -> createTask());
        buttonSelectDueDate.setOnClickListener(v -> showDatePicker("due"));
        buttonSelectStartDate.setOnClickListener(v -> showDatePicker("start"));
        buttonSelectEndDate.setOnClickListener(v -> showDatePicker("end"));

        return view;
    }

    private void setupSpinnerPriority() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        membersSelectAdapter = new MembersSelectAdapter(getContext(), allMembers, selectedMemberIds, this);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMembers.setAdapter(membersSelectAdapter);
    }

    private void checkUserRoleAndSetButtonVisibility() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            Log.d(TAG, "User role: " + role);
                            if ("admin".equals(role) || "prezes".equals(role) || "user".equals(role)) {
                                buttonCreateTask.setEnabled(true);
                                buttonCreateTask.setVisibility(View.VISIBLE);
                            } else {
                                buttonCreateTask.setEnabled(false);
                                buttonCreateTask.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getContext(), "Dokument użytkownika nie istnieje", Toast.LENGTH_SHORT).show();
                            buttonCreateTask.setEnabled(false);
                            buttonCreateTask.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Błąd podczas sprawdzania roli użytkownika", Toast.LENGTH_SHORT).show();
                        buttonCreateTask.setEnabled(false);
                        buttonCreateTask.setVisibility(View.GONE);
                        Log.e(TAG, "Error fetching user role", e);
                    });
        } else {
            buttonCreateTask.setEnabled(false);
            buttonCreateTask.setVisibility(View.GONE);
            Log.e(TAG, "Current user is null");
        }
    }

    private void fetchClubMembers() {
        if (clubName == null) {
            Log.e(TAG, "clubName jest null w fetchClubMembers");
            Toast.makeText(getContext(), "Błąd: Nazwa klubu jest nieznana", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("clubs").document(clubName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> memberIds = (List<String>) documentSnapshot.get("members");
                        if (memberIds != null) {
                            for (String userId : memberIds) {
                                fetchUserDetails(userId);
                            }
                        } else {
                            Toast.makeText(getContext(), "Brak członków w klubie", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Dokument klubu nie istnieje", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Club document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Nie udało się pobrać członków klubu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching club members", e);
                });
    }

    private void fetchUserDetails(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String role = documentSnapshot.getString("role");

                        User user = new User();
                        user.setUserId(userId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setEmail(email);
                        user.setRole(role);

                        // Unikaj duplikatów
                        if (!allMembers.contains(user)) {
                            allMembers.add(user);
                            membersSelectAdapter.notifyItemInserted(allMembers.size() - 1);
                            Log.d(TAG, "Added user: " + user.getFirstName() + " " + user.getLastName());
                        }
                    } else {
                        Toast.makeText(getContext(), "Dokument użytkownika nie istnieje", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "User document does not exist for userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Nie udało się pobrać danych użytkownika", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user details for userId: " + userId, e);
                });
    }

    private void showDatePicker(String type) {
        Calendar calendar = Calendar.getInstance();
        Date initialDate;
        TextView textView;

        switch (type) {
            case "start":
                initialDate = selectedStartDate != null ? selectedStartDate : new Date();
                textView = textViewStartDate;
                break;
            case "end":
                initialDate = selectedEndDate != null ? selectedEndDate : new Date();
                textView = textViewEndDate;
                break;
            case "due":
            default:
                initialDate = selectedDueDate != null ? selectedDueDate : new Date();
                textView = textViewDueDate;
                break;
        }

        calendar.setTime(initialDate);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    String formattedDate = formatDate(selectedDate);
                    textView.setText(
                            type.equals("start") ? "Data rozpoczęcia: " + formattedDate :
                                    type.equals("end") ? "Data zakończenia: " + formattedDate :
                                            "Data wykonania: " + formattedDate
                    );
                    Log.d(TAG, "Selected " + type + " date: " + selectedDate);
                    switch (type) {
                        case "start":
                            selectedStartDate = selectedDate;
                            break;
                        case "end":
                            selectedEndDate = selectedDate;
                            break;
                        case "due":
                        default:
                            selectedDueDate = selectedDate;
                            break;
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Dodanie ograniczeń do wyboru daty
        if ("end".equals(type) && selectedStartDate != null) {
            calendar.setTime(selectedStartDate);
            datePickerDialog.getDatePicker().setMinDate(selectedStartDate.getTime());
        }

        if ("due".equals(type) && selectedStartDate != null) {
            calendar.setTime(selectedStartDate);
            datePickerDialog.getDatePicker().setMinDate(selectedStartDate.getTime());
        }

        datePickerDialog.show();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public void onMemberSelected(String userId, boolean isSelected) {
        if (isSelected) {
            if (!selectedMemberIds.contains(userId)) {
                selectedMemberIds.add(userId);
                Log.d(TAG, "Member selected: " + userId);
            }
        } else {
            selectedMemberIds.remove(userId);
            Log.d(TAG, "Member deselected: " + userId);
        }
    }

    private void createTask() {
        if (clubName == null) {
            Toast.makeText(getContext(), "Błąd: Nazwa klubu jest nieznana", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "clubName is null in createTask");
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Błąd: Użytkownik nie jest zalogowany", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Current user is null in createTask");
            return;
        }

        String title = editTextTaskTitle.getText().toString().trim();
        String description = editTextTaskDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        int progress = (int) ratingBarProgress.getRating();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Wszystkie pola są wymagane", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Title or description is empty");
            return;
        }

        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(getContext(), "Wybierz co najmniej jednego członka", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No members selected");
            return;
        }

        // Walidacja dat
        if (selectedStartDate != null && selectedEndDate != null) {
            if (selectedStartDate.after(selectedEndDate)) {
                Toast.makeText(getContext(), "Data rozpoczęcia nie może być po dacie zakończenia", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Start date is after end date");
                return;
            }
        }

        if (selectedStartDate != null && selectedDueDate != null) {
            if (selectedDueDate.before(selectedStartDate)) {
                Toast.makeText(getContext(), "Data wykonania nie może być przed datą rozpoczęcia", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Due date is before start date");
                return;
            }
        }

        if (selectedStartDate != null && selectedEndDate != null && selectedDueDate != null) {
            if (selectedDueDate.before(selectedEndDate)) {
                Toast.makeText(getContext(), "Data wykonania nie może być przed datą zakończenia", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Due date is before end date");
                return;
            }
        }

        String createdBy = mAuth.getCurrentUser().getUid();
        Date dueDate = selectedDueDate != null ? selectedDueDate : new Date();

        // Tworzenie zadania
        Task newTask = new Task(title, description, dueDate, selectedStartDate, selectedEndDate,
                priority, progress, createdBy, clubName, selectedMemberIds);

        db.collection("clubs").document(clubName).collection("tasks")
                .add(newTask)
                .addOnSuccessListener(documentReference -> {
                    String createdTaskId = documentReference.getId();
                    Log.d(TAG, "Task created with ID: " + createdTaskId);
                    Toast.makeText(getContext(), "Zadanie utworzone", Toast.LENGTH_SHORT).show();

                    // Wysyłanie powiadomień
                    newTask.setId(createdTaskId); // Ustaw ID zadania
                    TaskNotificationManager.sendImmediateNotification(requireContext(), newTask);
                    TaskNotificationManager.scheduleTaskNotifications(requireContext(), newTask);

                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Błąd podczas tworzenia zadania", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error creating task", e);
                });
    }
}
