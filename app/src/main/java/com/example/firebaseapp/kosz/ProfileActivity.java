//package com.example.firebaseapp;
//
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    private TextView textViewFirstName, textViewLastName, textViewCourse, textViewAge;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        textViewFirstName = findViewById(R.id.textViewFirstName);
//        textViewLastName = findViewById(R.id.textViewLastName);
//        textViewCourse = findViewById(R.id.textViewCourse);
//        textViewAge = findViewById(R.id.textViewAge);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        loadUserData();
//    }
//
//    private void loadUserData() {
//        String userId = mAuth.getCurrentUser().getUid();
//        //Log.d("ProfileActivity", "Loading user data for userId: " + userId);
//
//        db.collection("users").document(userId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            User user = document.toObject(User.class);
//                            if (user != null) {
//                                textViewFirstName.setText(user.firstName);
//                                textViewLastName.setText(user.lastName);
//                                textViewCourse.setText(user.course);
//                                textViewAge.setText(String.valueOf(user.age));
//                            }
//                        } else {
//                            Toast.makeText(ProfileActivity.this, "Brak danych użytkownika", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(ProfileActivity.this, "Błąd pobierania danych: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//
//    private static class User {
//        String firstName;
//        String lastName;
//        String course;
//        int age;
//
//        User() {
//            // Domyślny konstruktor wymagany do deserializacji
//        }
//
//        User(String firstName, String lastName, String course, int age) {
//            this.firstName = firstName;
//            this.lastName = lastName;
//            this.course = course;
//            this.age = age;
//        }
//    }
//}
