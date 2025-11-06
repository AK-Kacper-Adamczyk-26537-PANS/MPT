package com.example.firebaseapp.authentication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firebaseapp.clubs_home.ClubsMainActivity;
import com.example.firebaseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.SharedPreferences; // Importujemy SharedPreferences
import android.widget.CheckBox; // Importujemy CheckBox


public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private ImageView imageViewShowPassword;
    private Button buttonLogin;
    private TextView textForgotPassword;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;

    private CheckBox checkBoxRememberMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        imageViewShowPassword = view.findViewById(R.id.imageViewShowPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        textForgotPassword = view.findViewById(R.id.textForgotPassword);

        imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));

        imageViewShowPassword.setOnClickListener(v -> togglePasswordVisibility());

        buttonLogin.setOnClickListener(v -> loginUser());

        textForgotPassword.setOnClickListener(v -> {
            // Przejdź do fragmentu resetowania hasła
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ResetPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Inicjalizacja SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

// Znajdź CheckBox i ustaw wartości
        checkBoxRememberMe = view.findViewById(R.id.checkBoxRememberMe);

// Wczytaj zapisane dane logowania
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            editTextEmail.setText(sharedPreferences.getString("email", ""));
            editTextPassword.setText(sharedPreferences.getString("password", ""));
            checkBoxRememberMe.setChecked(true);
        }


        return view;
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
        } else {
            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.eye, 60, 60));
        }
        isPasswordVisible = !isPasswordVisible;
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Wszystkie pola muszą być wypełnione", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Jeśli "Zapamiętaj mnie" jest zaznaczone, zapisz dane logowania
                            if (checkBoxRememberMe.isChecked()) {
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putBoolean("rememberMe", true);
                                editor.apply();
                            } else {
                                // Usuń zapisane dane, jeśli "Zapamiętaj mnie" jest odznaczone
                                editor.clear();
                                editor.apply();
                            }

                            // Przejście do innej aktywności
                            Intent intent = new Intent(getActivity(), ClubsMainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Proszę potwierdzić swój adres e-mail.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Błąd logowania. Sprawdź dane i spróbuj ponownie.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private Bitmap resizeImage(int resourceId, int width, int height) {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}
