//package com.example.firebaseapp;
//
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SingleClubActivity extends AppCompatActivity {
//
//    private TextView clubNameTextView;
//    private TextView clubDescriptionTextView;
//    private ImageView clubLogoImageView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_single_club); // Upewnij się, że masz ten layout
//
////        clubNameTextView = findViewById(R.id.clubNameTextView);
////        clubDescriptionTextView = findViewById(R.id.clubDescriptionTextView);
////        clubLogoImageView = findViewById(R.id.clubLogoImageView);
//
//        // Odbierz dane z intencji
//        String clubName = getIntent().getStringExtra("clubName");
//        String clubDescription = getIntent().getStringExtra("clubDescription");
//        String imageName = getIntent().getStringExtra("imageName");
//
//        // Ustaw dane w widokach
//        clubNameTextView.setText(clubName);
//        clubDescriptionTextView.setText(clubDescription);
//        clubLogoImageView.setImageResource(
//                getResources().getIdentifier(imageName, "drawable", getPackageName())
//        );
//    }
//}
