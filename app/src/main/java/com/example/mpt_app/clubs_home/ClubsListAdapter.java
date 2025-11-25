package com.example.mpt_app.clubs_home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpt_app.R;
import com.example.mpt_app.models.Club;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClubsListAdapter extends RecyclerView.Adapter<ClubsListAdapter.ClubViewHolder> {

    private Context context;
    private List<Club> clubsList;

    public ClubsListAdapter(Context context, List<Club> clubsList) {
        this.context = context;
        this.clubsList = clubsList;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.club_item, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubsList.get(position);
        holder.clubName.setText(club.getName());
        setClubLogo(holder, club);
        setItemClickListener(holder, club);
    }

    private void setClubLogo(ClubViewHolder holder, Club club) {
        int logoResId = context.getResources().getIdentifier(club.getImageName().replace(".png", ""), "drawable", context.getPackageName());
        holder.clubLogo.setImageResource(logoResId != 0 ? logoResId : R.drawable.default_club_image); // Ustaw domyślny obrazek, jeśli nie znaleziono
    }

    private void setItemClickListener(ClubViewHolder holder, Club club) {
        holder.itemView.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                checkUserMembership(currentUser.getUid(), club);
            }
        });
    }

    private void checkUserMembership(String userId, Club club) {
        DocumentReference clubRef = FirebaseFirestore.getInstance().collection("clubs").document(club.getName());
        clubRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                handleUserMembership(documentSnapshot, userId, club); // Tutaj używasz DocumentSnapshot
            }
        }).addOnFailureListener(e -> Toast.makeText(context, "Błąd podczas sprawdzania członkostwa", Toast.LENGTH_SHORT).show());
    }


    private void handleUserMembership(DocumentSnapshot documentSnapshot, String userId, Club club) {
        List<String> members = (List<String>) documentSnapshot.get("members");
        AppCompatActivity activity = (AppCompatActivity) context;
        Bundle bundle = new Bundle();
        bundle.putString("clubName", club.getName());

        if (members != null && members.contains(userId)) {
            // Użytkownik należy do koła
            ClubInfoFragment clubInfoFragment = new ClubInfoFragment();
            bundle.putString("clubDescription", club.getDescription());
            bundle.putInt("clubImageResId", context.getResources().getIdentifier(club.getImageName().replace(".png", ""), "drawable", context.getPackageName()));
            clubInfoFragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, clubInfoFragment).addToBackStack(null).commit();
        } else {
            // Użytkownik nie należy do koła
            JoinClubInfoFragment joinClubInfoFragment = new JoinClubInfoFragment();
            joinClubInfoFragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, joinClubInfoFragment).addToBackStack(null).commit();
        }
    }


    @Override
    public int getItemCount() {
        return clubsList.size();
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView clubLogo;
        TextView clubName;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            clubLogo = itemView.findViewById(R.id.club_logo);
            clubName = itemView.findViewById(R.id.club_name);
        }
    }
}
