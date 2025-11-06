//package com.example.firebaseapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class ScienceCircleActivity extends AppCompatActivity {
//
//    private ImageView bannerImage;
//    private ImageView circleLogo;
//    private TextView circleName;
//    private TextView circleDescription;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_science_circle);
//
//        // Inicjalizacja widoków
////        bannerImage = findViewById(R.id.banner_image);
////        circleLogo = findViewById(R.id.circle_logo);
////        circleName = findViewById(R.id.circle_name);
////        circleDescription = findViewById(R.id.circle_description);
//
//        // Pobranie danych koła z Intenta
//        Intent intent = getIntent();
//        String name = intent.getStringExtra("circle_name");
//        String description = intent.getStringExtra("circle_description");
//        String logoFileName = intent.getStringExtra("circle_logo_file"); // Nazwa pliku loga
//        String bannerFileName = intent.getStringExtra("circle_banner_file"); // Nazwa pliku banera z Firebase
//
//        // Ustawianie danych w widokach
//        circleName.setText(name);
//        circleDescription.setText(description);
//
//        // Ustaw logo koła
//        int logoResId = getResources().getIdentifier(logoFileName, "drawable", getPackageName());
//        if (logoResId != 0) {
//            circleLogo.setImageResource(logoResId);
//        } else {
//            circleLogo.setImageResource(R.drawable.woman_home); // Domyślne logo
//        }
//
//        // Pobranie banera z Firebase na podstawie bannerUrl
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference bannerRef = database.getReference("clubs").child(name).child("bannerUrl");
//
//        bannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String bannerFileName = dataSnapshot.getValue(String.class); // Pobierz nazwę pliku banera
//                Log.d("ScienceCircleActivity", "Banner file name: " + bannerFileName); // Debug log
//                int bannerResId = getResources().getIdentifier(bannerFileName, "drawable", getPackageName());
//
//                if (bannerResId != 0) {
//                    bannerImage.setImageResource(bannerResId); // Ustawienie banera
//                } else {
//                    bannerImage.setImageResource(R.drawable.happy); // Domyślny baner
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("ScienceCircleActivity", "Error loading banner", databaseError.toException());
//            }
//        });
//    }
//}
