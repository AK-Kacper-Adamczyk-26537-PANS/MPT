//package com.example.firebaseapp;
//
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class UserProfileActivity extends AppCompatActivity {
//
//    private TextView textViewName, textViewSurname, textViewFieldOfStudy, textViewAge;
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_profile);
//
//        textViewName = findViewById(R.id.textViewName);
//        textViewSurname = findViewById(R.id.textViewSurname);
//        textViewFieldOfStudy = findViewById(R.id.textViewFieldOfStudy);
//        textViewAge = findViewById(R.id.textViewAge);
//
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//
//        loadUserProfile();
//    }
//
//    private void loadUserProfile() {
//        String userId = mAuth.getCurrentUser().getUid();
//
//        db.collection("users").document(userId).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        User user = documentSnapshot.toObject(User.class);
//
//                        if (user != null) {
//                            textViewName.setText("Imię: " + user.getFirstName());
//                            textViewSurname.setText("Nazwisko: " + user.getLastName());
//                            textViewFieldOfStudy.setText("Kierunek studiów: " + user.getMajor());
//                            textViewAge.setText("Wiek: " + user.getAge());
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Obsłuż błąd, np. wyświetl komunikat
//                });
//    }
//}
