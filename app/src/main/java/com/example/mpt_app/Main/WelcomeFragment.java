package com.example.mpt_app.Main;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mpt_app.R;
import com.example.mpt_app.authentication.LoginFragment;
import com.example.mpt_app.authentication.RegisterFragment;

public class WelcomeFragment extends Fragment {

    private Button buttonLogin, buttonRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Pogrubienie fragmentu tekstu w welcome_text
        TextView welcomeText = view.findViewById(R.id.welcome_text);
        String welcome = "Witaj w aplikacji do zarządzania kołami naukowymi PANS w Krośnie!";
        SpannableString spannableWelcome = new SpannableString(welcome);
        // Pogrubienie tekstu "PANS w Krośnie"
        spannableWelcome.setSpan(new StyleSpan(Typeface.BOLD), 49, 64, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeText.setText(spannableWelcome);

        // Pogrubienie i kursywa fragmentu tekstu w description_text
        TextView descriptionText = view.findViewById(R.id.description_text);
        String description = "Twoje koło naukowe w jednym miejscu. Zorganizuj, planuj, działaj!";
        SpannableString spannableDescription = new SpannableString(description);
        // Pogrubienie i kursywa dla tekstu "Zorganizuj, planuj, działaj!"
        spannableDescription.setSpan(new StyleSpan(Typeface.BOLD), 37, 65, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableDescription.setSpan(new StyleSpan(Typeface.ITALIC), 37, 65, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionText.setText(spannableDescription);

        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(v -> {
            Fragment loginFragment = new LoginFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, loginFragment) // Upewnij się, że fragment_container to id kontenera w Twoim layout
                    .addToBackStack(null) // Dodaje do stosu, aby można było wrócić
                    .commit();
        });

        buttonRegister.setOnClickListener(v -> {
            Fragment registerFragment = new RegisterFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, registerFragment) // Upewnij się, że fragment_container to id kontenera w Twoim layout
                    .addToBackStack(null) // Dodaje do stosu, aby można było wrócić
                    .commit();
        });


        return view;
    }
}
