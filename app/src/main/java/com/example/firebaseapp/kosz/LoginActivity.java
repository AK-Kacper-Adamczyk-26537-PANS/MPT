//package com.example.firebaseapp;
//
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText editTextEmail, editTextPassword;
//    private ImageView imageViewShowPassword;
//    private Button buttonLogin;
//    private TextView textForgotPassword;
//    private boolean isPasswordVisible = false;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        editTextEmail = findViewById(R.id.editTextEmail);
//        editTextPassword = findViewById(R.id.editTextPassword);
//        imageViewShowPassword = findViewById(R.id.imageViewShowPassword);
//        buttonLogin = findViewById(R.id.buttonLogin);
//        textForgotPassword = findViewById(R.id.textForgotPassword);
//
//        // Ustawienie obrazków
//        imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
//
//        imageViewShowPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                togglePasswordVisibility();
//            }
//        });
//
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginUser();
//            }
//        });
//
//        textForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));  // Użyj skalowanego obrazka
//        } else {
//            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.eye, 60, 60));  // Użyj skalowanego obrazka
//        }
//        isPasswordVisible = !isPasswordVisible;
//        editTextPassword.setSelection(editTextPassword.getText().length());
//    }
//
//    private void loginUser() {
//        String email = editTextEmail.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Wszystkie pola muszą być wypełnione", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Przejdź do głównej aktywności
//                        Intent intent = new Intent(LoginActivity.this, ClubsActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Błąd logowania. Sprawdź dane i spróbuj ponownie.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private Bitmap resizeImage(int resourceId, int width, int height) {
//        Resources res = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId);
//        return Bitmap.createScaledBitmap(bitmap, width, height, false);
//    }
//}
