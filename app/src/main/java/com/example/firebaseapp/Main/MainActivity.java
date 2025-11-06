package com.example.firebaseapp.Main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.firebaseapp.clubs_home.ClubsMainActivity;
import com.example.firebaseapp.R;
import com.example.firebaseapp.authentication.LoginFragment;
import com.example.firebaseapp.authentication.RegisterFragment;
import com.example.firebaseapp.authentication.ResetPasswordFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        // Tryb pełnoekranowy
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
// Ustawienie trybu pełnoekranowego
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        // Opcjonalnie: Ustawienie trybu immersive
//        getWindow().getDecorView().setSystemUiVisibility(
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Tryb pełnoekranowy
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Ustawienie przezroczystego paska statusu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);  // Ustawienie koloru paska statusu na przezroczysty
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.navigation_bar_color)); // Jasne tło
        }


        // Sprawdź aktualny tryb systemowy i ustaw odpowiednie kolory ikon
//        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WelcomeFragment())
                    .commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Sprawdź, czy zmienił się tryb (jasny/ciemny)
        if ((newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Tryb ciemny
            recreate();  // Odśwież widok aktywności
        } else {
            // Tryb jasny
            recreate();  // Odśwież widok aktywności
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof LoginFragment) {
            // Cofnij do WelcomeFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WelcomeFragment())
                    .commit();
        } else if (currentFragment instanceof RegisterFragment) {
            // Cofnij do WelcomeFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WelcomeFragment())
                    .commit();
        } else if (currentFragment instanceof ResetPasswordFragment) {
            // Cofnij do LoginFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        } else {
            super.onBackPressed(); // Pozwól na standardowe cofanie dla innych fragmentów
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Użytkownik jest zalogowany, przejdź bezpośrednio do aplikacji
            Intent intent = new Intent(this, ClubsMainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

