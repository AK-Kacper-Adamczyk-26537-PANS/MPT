//package com.example.firebaseapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class EmailVerificationActivity extends AppCompatActivity {
//
//    private Button buttonCheckEmail;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_email_verification);
//
//        buttonCheckEmail = findViewById(R.id.buttonCheckEmail);
//        mAuth = FirebaseAuth.getInstance();
//
//        buttonCheckEmail.setOnClickListener(v -> checkEmailVerification());
//    }
//
//    private void checkEmailVerification() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            user.reload().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    if (user.isEmailVerified()) {
//                        // Przejdź do aktywności głównej
////                        Intent intent = new Intent(EmailVerificationActivity.this, UserDetailsActivity.class);
//                        Intent intent = new Intent(EmailVerificationActivity.this, ClubsActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(EmailVerificationActivity.this, "E-mail jeszcze nie zweryfikowany. Proszę sprawdzić swoją skrzynkę e-mailową.", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(EmailVerificationActivity.this, "Błąd podczas sprawdzania e-maila: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
