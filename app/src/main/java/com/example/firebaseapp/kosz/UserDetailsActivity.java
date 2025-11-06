//package com.example.firebaseapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class UserDetailsActivity extends AppCompatActivity {
//
//    private EditText editTextFirstName, editTextLastName, editTextAge;
//    private Spinner spinnerMajor;
//    private Button buttonSave;
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_details);
//
//        editTextFirstName = findViewById(R.id.editTextFirstName);
//        editTextLastName = findViewById(R.id.editTextLastName);
//        editTextAge = findViewById(R.id.editTextAge);
//        spinnerMajor = findViewById(R.id.spinnerMajor);
//        buttonSave = findViewById(R.id.buttonSave);
//
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//
//        // Wypełnij spinner kierunkami studiów z tablicy w strings.xml
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.majors_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerMajor.setAdapter(adapter);
//
//        buttonSave.setOnClickListener(v -> saveUserDetails());
//    }
//
//    private void saveUserDetails() {
//        String firstName = editTextFirstName.getText().toString().trim();
//        String lastName = editTextLastName.getText().toString().trim();
//        String major = spinnerMajor.getSelectedItem().toString();
//        String ageStr = editTextAge.getText().toString().trim();
//
//        // Sprawdź, czy imię i nazwisko są poprawne
//        if (firstName.isEmpty() || !firstName.matches("[a-zA-Z]+")) {
//            Toast.makeText(UserDetailsActivity.this, "Imię musi składać się tylko z liter i nie może być puste", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (lastName.isEmpty() || !lastName.matches("[a-zA-Z]+")) {
//            Toast.makeText(UserDetailsActivity.this, "Nazwisko musi składać się tylko z liter i nie może być puste", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Sprawdź, czy wiek jest liczbą i w zakresie 0-100
//        if (ageStr.isEmpty()) {
//            Toast.makeText(UserDetailsActivity.this, "Wiek nie może być pusty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int age;
//        try {
//            age = Integer.parseInt(ageStr);
//            if (age < 0 || age > 100) {
//                Toast.makeText(UserDetailsActivity.this, "Wiek musi być liczbą z zakresu 0-100", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        } catch (NumberFormatException e) {
//            Toast.makeText(UserDetailsActivity.this, "Wiek musi być liczbą", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = mAuth.getCurrentUser().getUid();
//        User user = new User(firstName, lastName, major, age);
//
//        db.collection("users").document(userId)
//                .set(user)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(UserDetailsActivity.this, "Dane zapisane pomyślnie", Toast.LENGTH_SHORT).show();
//                    // Przekierowanie do ClubsActivity po zapisaniu danych
//                    startActivity(new Intent(UserDetailsActivity.this, ClubsActivity.class));
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(UserDetailsActivity.this, "Błąd zapisywania danych: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//
//}
