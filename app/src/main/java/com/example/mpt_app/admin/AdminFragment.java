//package com.example.firebaseapp.admin;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.firebaseapp.R;
//import com.example.firebaseapp.adapters.UsersAdapter;
//import com.example.firebaseapp.models.User;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AdminFragment extends Fragment implements UsersAdapter.OnUserRoleChangeListener {
//
//    private RecyclerView recyclerViewUsers;
//    private UsersAdapter usersAdapter;
//    private List<User> userList;
//    private FirebaseFirestore db;
//
//    public AdminFragment() {
//        // Wymagany pusty konstruktor
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_admin, container, false);
//
//        db = FirebaseFirestore.getInstance();
//
//        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
//        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        userList = new ArrayList<>();
//        usersAdapter = new UsersAdapter(getContext(), userList, this);
//        recyclerViewUsers.setAdapter(usersAdapter);
//
//        fetchAllUsers();
//
//        return view;
//    }
//
//    private void fetchAllUsers() {
//        db.collection("users").get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    userList.clear();
//                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        User user = document.toObject(User.class);
//                        user.setUserId(document.getId());
//                        userList.add(user);
//                    }
//                    usersAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Nie udało się pobrać listy użytkowników", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    @Override
//    public void onUserRoleChange(String userId, String newRole) {
//        changeUserRole(userId, newRole);
//    }
//
//    public void changeUserRole(String userId, String newRole) {
//        db.collection("users").document(userId)
//                .update("role", newRole)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(getContext(), "Rola użytkownika zaktualizowana", Toast.LENGTH_SHORT).show();
//                    fetchAllUsers(); // Odśwież listę użytkowników
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Nie udało się zaktualizować roli użytkownika", Toast.LENGTH_SHORT).show();
//                });
//    }
//}
