package com.example.mpt_app.authentication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mpt_app.Main.MainActivity;
import com.example.mpt_app.R;
import com.example.mpt_app.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private EditText editTextFirstName, editTextLastName, editTextAge, editTextPhone;
    private Spinner spinnerMajor;
    private ImageView imageViewShowPassword, imageViewShowConfirmPassword;
    private Button buttonRegister;
    private TextView passwordError;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Inicjalizacja pól
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        spinnerMajor = view.findViewById(R.id.spinnerMajor);

        imageViewShowPassword = view.findViewById(R.id.imageViewShowPassword);
        imageViewShowConfirmPassword = view.findViewById(R.id.imageViewShowConfirmPassword);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        passwordError = view.findViewById(R.id.password_error);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ustawienie obrazków (skalowanie)
        imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
        imageViewShowConfirmPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));

        // Konfiguracja Spinnera (kierunki studiów)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.major_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajor.setAdapter(adapter);

        // Obsługa widoczności hasła
        imageViewShowPassword.setOnClickListener(v -> togglePasswordVisibility());
        imageViewShowConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        // Obsługa rejestracji
        buttonRegister.setOnClickListener(v -> registerUser());

        return view;
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageViewShowPassword.setImageBitmap(resizeImage(R.drawable.eye, 60, 60));
        }
        isPasswordVisible = !isPasswordVisible;
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageViewShowConfirmPassword.setImageBitmap(resizeImage(R.drawable.hidden, 60, 60));
        } else {
            editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageViewShowConfirmPassword.setImageBitmap(resizeImage(R.drawable.eye, 60, 60));
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().length());
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String major = spinnerMajor.getSelectedItem().toString();

        // Walidacja pustych pól
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() ||
                lastName.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getActivity(), "Proszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        // Walidacja długości numeru telefonu
        if (phone.length() != 9) {
            Toast.makeText(getActivity(), "Numer telefonu musi zawierać 9 cyfr", Toast.LENGTH_SHORT).show();
            return;
        }

        // Walidacja hasła
        if (!password.equals(confirmPassword)) {
            passwordError.setVisibility(View.VISIBLE);
            return;
        } else {
            passwordError.setVisibility(View.GONE);
        }

        // Rejestracja w Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            saveUserDetails(userId, firstName, lastName, ageStr, phone, major);
                            sendVerificationEmail();  // Wyślij e-mail weryfikacyjny
                        }
                    } else {
                        Toast.makeText(getActivity(), "Błąd rejestracji: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String firstName, String lastName, String ageStr, String phone, String major) {
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Błędny wiek", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = editTextEmail.getText().toString().trim();
        String role = "user";  // Domyślna rola

        User user = new User(firstName, lastName, major, age, phone, email, role);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Rejestracja udana", Toast.LENGTH_SHORT).show();
                    sendVerificationEmail();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Błąd zapisu danych: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Wysłano e-mail weryfikacyjny. Sprawdź swoją skrzynkę e-mailową.", Toast.LENGTH_SHORT).show();
                            // Wyloguj użytkownika po wysłaniu e-maila
                            mAuth.signOut();
                            // Przekieruj użytkownika do ekranu powiadomienia
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Błąd wysyłania e-maila weryfikacyjnego: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private Bitmap resizeImage(int resourceId, int width, int height) {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}
