//package com.example.firebaseapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class ResetPasswordActivity extends AppCompatActivity {
//
//    private EditText editTextEmail;
//    private Button buttonResetPassword;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reset_password);
//
//        editTextEmail = findViewById(R.id.editTextEmail);
//        buttonResetPassword = findViewById(R.id.buttonResetPassword);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        buttonResetPassword.setOnClickListener(v -> resetPassword());
//    }
//
//    private void resetPassword() {
//        String email = editTextEmail.getText().toString().trim();
//
//        if (email.isEmpty()) {
//            Toast.makeText(ResetPasswordActivity.this, "Adres e-mail nie może być pusty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(ResetPasswordActivity.this, "Jeśli konto z tym adresem e-mail istnieje, otrzymasz link do resetowania hasła", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
//                        finish(); // Opcjonalnie, zamknij aktywność resetowania hasła
//                    } else {
//                        Toast.makeText(ResetPasswordActivity.this, "Błąd: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
