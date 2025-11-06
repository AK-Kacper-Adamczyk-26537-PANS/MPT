//package com.example.firebaseapp;
//
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
//    private EditText editTextFirstName, editTextLastName, editTextAge, editTextPhone;
//    private Spinner spinnerMajor;
//    private ImageView imageViewShowPassword, imageViewShowConfirmPassword;
//    private Button buttonRegister;
//    private TextView passwordError;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;  // Dodajemy bazę Firestore
//    private boolean isPasswordVisible = false;
//    private boolean isConfirmPasswordVisible = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        // Inicjalizacja pól
//        editTextEmail = findViewById(R.id.editTextEmail);
//        editTextPassword = findViewById(R.id.editTextPassword);
//        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
//        editTextFirstName = findViewById(R.id.editTextFirstName);
//        editTextLastName = findViewById(R.id.editTextLastName);
//        editTextAge = findViewById(R.id.editTextAge);
//        editTextPhone = findViewById(R.id.editTextPhone);
//        spinnerMajor = findViewById(R.id.spinnerMajor);
//
//        imageViewShowPassword = findViewById(R.id.imageViewShowPassword);
//        imageViewShowConfirmPassword = findViewById(R.id.imageViewShowConfirmPassword);
//        buttonRegister = findViewById(R.id.buttonRegister);
//        passwordError = findViewById(R.id.password_error);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();  // Inicjalizacja Firestore
//
//        // Ustawienie obrazków (skalowanie)
//        imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
//        imageViewShowConfirmPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
//
//        // Konfiguracja Spinnera (kierunki studiów)
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.major_options, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerMajor.setAdapter(adapter);
//
//        // Obsługa widoczności hasła
//        imageViewShowPassword.setOnClickListener(v -> togglePasswordVisibility());
//        imageViewShowConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());
//
//        // Obsługa rejestracji
//        buttonRegister.setOnClickListener(v -> registerUser());
//    }
//
//    private void togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
//        } else {
//            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.eye, 60, 60));
//        }
//        isPasswordVisible = !isPasswordVisible;
//        editTextPassword.setSelection(editTextPassword.getText().length());
//    }
//
//    private void toggleConfirmPasswordVisibility() {
//        if (isConfirmPasswordVisible) {
//            editTextConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            imageViewShowConfirmPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
//        } else {
//            editTextConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            imageViewShowConfirmPassword.setImageBitmap(resizeImage(R.drawable.eye, 60, 60));
//        }
//        isConfirmPasswordVisible = !isConfirmPasswordVisible;
//        editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().length());
//    }
//
//    private void registerUser() {
//        String email = editTextEmail.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
//        String firstName = editTextFirstName.getText().toString().trim();
//        String lastName = editTextLastName.getText().toString().trim();
//        String ageStr = editTextAge.getText().toString().trim();
//        String phone = editTextPhone.getText().toString().trim();
//        String major = spinnerMajor.getSelectedItem().toString();
//
//        // Walidacja pustych pól
//        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() ||
//                lastName.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
//            Toast.makeText(RegisterActivity.this, "Proszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Walidacja długości numeru telefonu
//        if (phone.length() != 9) {
//            Toast.makeText(this, "Numer telefonu musi zawierać 9 cyfr", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Walidacja hasła
//        if (!password.equals(confirmPassword)) {
//            passwordError.setVisibility(View.VISIBLE);
//            return;
//        } else {
//            passwordError.setVisibility(View.GONE);
//        }
//
//        // Rejestracja w Firebase Authentication
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null) {
//                            String userId = user.getUid();
//                            saveUserDetails(userId, firstName, lastName, ageStr, phone, major);
//                        }
//                    } else {
//                        Toast.makeText(RegisterActivity.this, "Błąd rejestracji: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void saveUserDetails(String userId, String firstName, String lastName, String ageStr, String phone, String major) {
//        int age;
//        try {
//            age = Integer.parseInt(ageStr);
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Błędny wiek", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Pobieramy adres email z pola email
//        String email = editTextEmail.getText().toString().trim();
//
//        // Tworzymy obiekt użytkownika z dodatkowymi danymi (numer telefonu i email)
//        User user = new User(firstName, lastName, major, age, phone, email);
//
//        // Zapisujemy dane użytkownika do Firestore
//        db.collection("users").document(userId)
//                .set(user)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(RegisterActivity.this, "Rejestracja udana", Toast.LENGTH_SHORT).show();
//                    sendVerificationEmail();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(RegisterActivity.this, "Błąd zapisu danych: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//
//    private void sendVerificationEmail() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            user.sendEmailVerification()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(RegisterActivity.this, "Wysłano e-mail weryfikacyjny. Sprawdź swoją skrzynkę e-mailową.", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(RegisterActivity.this, EmailVerificationActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(RegisterActivity.this, "Błąd wysyłania e-maila weryfikacyjnego: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
//
//    // Metoda skalująca obrazek
//    private Bitmap resizeImage(int resourceId, int width, int height) {
//        Resources res = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId);
//        return Bitmap.createScaledBitmap(bitmap, width, height, false);
//    }
//}
