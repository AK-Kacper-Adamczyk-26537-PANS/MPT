//package com.example.firebaseapp.clubs_home;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.firebaseapp.R;
//
//public class ClubDetailsActivity extends AppCompatActivity {
//    private TextView clubNameTextView;
//    private TextView clubDescriptionTextView;
//    private ImageView clubImageView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_club_details);
//
//        clubNameTextView = findViewById(R.id.club_name);
//        clubDescriptionTextView = findViewById(R.id.club_description);
//        clubImageView = findViewById(R.id.club_image);
//
//        // Odbieramy dane przekazane z poprzedniej aktywności
//        Intent intent = getIntent();
//        String clubName = intent.getStringExtra("clubName");
//        String clubDescription = intent.getStringExtra("clubDescription");
//        int clubImageResId = intent.getIntExtra("clubImageResId", 0);
//
//        // Ustawiamy dane w widokach
//        clubNameTextView.setText(clubName);
//        clubDescriptionTextView.setText(clubDescription);
//        clubImageView.setImageResource(clubImageResId);
//    }
//}
