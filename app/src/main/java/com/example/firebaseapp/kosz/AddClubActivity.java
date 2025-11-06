//package com.example.firebaseapp;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.firestore.FirebaseFirestore;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddClubActivity extends AppCompatActivity {
//
//    private static final String TAG = "AddClubActivity";
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    private EditText nameEditText, descriptionEditText;
//    private Button addButton, selectImageButton;
//    private ImageView imageView;
//    private Uri imageUri;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_club);
//
//        nameEditText = findViewById(R.id.edit_text_name);
//        descriptionEditText = findViewById(R.id.edit_text_description);
//        addButton = findViewById(R.id.button_add);
//        selectImageButton = findViewById(R.id.button_select_image);
//        imageView = findViewById(R.id.image_view_club);
//
//        selectImageButton.setOnClickListener(v -> openFileChooser());
//
//        addButton.setOnClickListener(v -> addClub());
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Wybierz obraz"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void addClub() {
//        String name = nameEditText.getText().toString().trim();
//        String description = descriptionEditText.getText().toString().trim();
//
//        if (name.isEmpty() || description.isEmpty() || imageUri == null) {
//            // Obsłuż błędy wprowadzania
//            return;
//        }
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> clubData = new HashMap<>();
//        clubData.put("name", name);
//        clubData.put("description", description);
//        clubData.put("members", Collections.emptyList()); // Na początku brak członków
//        clubData.put("imageName", imageUri.toString()); // Zapisz URI obrazu jako nazwę
//
//        db.collection("clubs").document(name).set(clubData)
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "Klub dodany: " + name);
//                    setResult(Activity.RESULT_OK);
//                    finish(); // Zamknij aktywność
//                })
//                .addOnFailureListener(e -> Log.w(TAG, "Błąd dodawania klubu", e));
//    }
//}
