//package com.example.firebaseapp.authentication;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.firebaseapp.ClubsActivity;
//import com.example.firebaseapp.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class EmailVerificationFragment extends Fragment {
//
//    private Button buttonCheckEmail;
//    private FirebaseAuth mAuth;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_email_verification, container, false);
//
//        buttonCheckEmail = view.findViewById(R.id.buttonCheckEmail);
//        mAuth = FirebaseAuth.getInstance();
//
//        buttonCheckEmail.setOnClickListener(v -> checkEmailVerification());
//
//        return view;
//    }
//
//    private void checkEmailVerification() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            user.reload().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    if (user.isEmailVerified()) {
//                        // Przejdź do głównej aktywności
//                        Intent intent = new Intent(getActivity(), ClubsActivity.class);
//                        startActivity(intent);
//                        requireActivity().finish();
//                    } else {
//                        Toast.makeText(getActivity(), "E-mail jeszcze nie zweryfikowany. Proszę sprawdzić swoją skrzynkę e-mailową.", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "Błąd podczas sprawdzania e-maila: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
