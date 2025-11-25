package com.example.mpt_app.clubs_home;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mpt_app.AboutFragment;
import com.example.mpt_app.AdminFragment;
import com.example.mpt_app.Main.MainActivity;
import com.example.mpt_app.R;
import com.example.mpt_app.SettingsFragment;
import com.example.mpt_app.ShareFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubsMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "ClubsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);

        // Tryb pełnoekranowy
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Ustawienie przezroczystego paska statusu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);  // Ustawienie koloru paska statusu na przezroczysty
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.navigation_bar_color)); // Jasne tło
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance(); // Inicjalizacja Firestore

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obsługa kliknięcia na ikonę hamburgera
        ImageView hamburgerIcon = findViewById(R.id.hamburger_icon);
        hamburgerIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Pobranie nagłówka nawigacji
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.nav_user_name);
        TextView userMajorTextView = headerView.findViewById(R.id.nav_user_major);

        // Sprawdź, czy użytkownik jest zalogowany
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Pobranie ID użytkownika

            // Odwołanie do dokumentu użytkownika w Firestore
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Pobranie imienia, nazwiska i kierunku studiów
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String major = documentSnapshot.getString("major");

                    // Ustawienie imienia i nazwiska
                    userNameTextView.setText(firstName + " " + lastName);
                    // Ustawienie kierunku studiów
                    userMajorTextView.setText("Kierunek studiów: " + major);
                } else {
                    Log.d(TAG, "Document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching document", e);
            });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClubsHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        updateStatusBarIconsColor(); // Ustaw kolor ikon na podstawie trybu
    }

    private void updateStatusBarIconsColor() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Tryb ciemny - ustaw ikonki na białe
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().setStatusBarColor(Color.TRANSPARENT); // Pasek stanu pozostaje przezroczysty
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE); // Przywrócenie domyślnego
            }
        } else {
            // Tryb jasny - ustaw ikonki na czarne

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().setStatusBarColor(Color.TRANSPARENT); // Pasek stanu pozostaje przezroczysty
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Ustaw flagi do jasnych ikon
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateStatusBarIconsColor(); // Aktualizuj kolor ikon na podstawie nowego trybu
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Pobierz ID wybranego elementu

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClubsHomeFragment()).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (id == R.id.nav_share) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (id == R.id.nav_admin_panel) {
            // Sprawdź, czy użytkownik jest adminem
            checkIfUserIsAdmin();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logging out!", Toast.LENGTH_SHORT).show();
            mAuth.signOut(); // Wylogowanie użytkownika
            Intent intent = new Intent(ClubsMainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); // Powrót do ekranu głównego po wylogowaniu
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void checkIfUserIsAdmin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("admin".equals(role)) {
                                // Użytkownik jest adminem - przejdź do AdminFragment
                                Fragment adminFragment = new AdminFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, adminFragment)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                // Użytkownik nie jest adminem - wyświetl komunikat
                                Toast.makeText(this, "Brak dostępu do panelu administratora", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Dokument użytkownika nie istnieje");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Błąd podczas pobierania dokumentu użytkownika", e);
                    });
        } else {
            Toast.makeText(this, "Nie jesteś zalogowany", Toast.LENGTH_SHORT).show();
        }
    }

}
