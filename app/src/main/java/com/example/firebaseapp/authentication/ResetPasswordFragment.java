package com.example.firebaseapp.authentication;

import android.content.Intent;
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

import com.example.firebaseapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {

    private EditText editTextEmail;
    private Button buttonResetPassword;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonResetPassword = view.findViewById(R.id.buttonResetPassword);

        mAuth = FirebaseAuth.getInstance();

        buttonResetPassword.setOnClickListener(v -> resetPassword());

        return view;
    }

    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(getActivity(), "Adres e-mail nie może być pusty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Jeśli konto z tym adresem e-mail istnieje, otrzymasz link do resetowania hasła", Toast.LENGTH_LONG).show();
                        // Przejdź do fragmentu logowania
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "Błąd: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
