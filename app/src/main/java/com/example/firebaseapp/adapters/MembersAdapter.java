package com.example.firebaseapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.R;
import com.example.firebaseapp.models.User;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    // Definicja interfejsu do obsługi kliknięć
    public interface OnMemberClickListener {
        void onMemberClick(User member);
    }

    private final Context context;
    private final List<User> membersList;
    private final OnMemberClickListener listener;
    private String currentUserRole;

    public MembersAdapter(Context context, List<User> membersList, OnMemberClickListener listener, String currentUserRole) {
        this.context = context;
        this.membersList = membersList;
        this.listener = listener;
        this.currentUserRole = currentUserRole;
        Log.d("MembersAdapter", "Adapter initialized with role: " + currentUserRole);
    }

    /**
     * Ustawia rolę użytkownika i informuje adapter o zmianach.
     *
     * @param currentUserRole Nowa rola użytkownika.
     */
    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
        notifyDataSetChanged();
        Log.d("MembersAdapter", "Current user role set to: " + currentUserRole);
    }

    /**
     * Aktualizuje listę członków i powiadamia adapter o zmianach.
     *
     * @param newMembers Nowa lista członków.
     */
    public void updateMembersList(List<User> newMembers) {
        membersList.clear();
        membersList.addAll(newMembers);
        notifyDataSetChanged();
        Log.d("MembersAdapter", "Members list updated with " + newMembers.size() + " members");
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        if (position < membersList.size()) {
            User member = membersList.get(position);
            Log.d("MembersAdapter", "Binding member: " + member.getFirstName() + " " + member.getLastName());
            holder.bind(member);

            // Ustawienie kliknięcia tylko jeśli użytkownik jest admin lub prezes
            if ("admin".equalsIgnoreCase(currentUserRole) || "prezes".equalsIgnoreCase(currentUserRole)) {
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onMemberClick(member);
                        Log.d("MembersAdapter", "Member clicked: " + member.getUserId());
                    }
                });
            } else {
                holder.itemView.setOnClickListener(null); // Usunięcie kliknięcia dla innych ról
                Log.d("MembersAdapter", "Click listener removed for member: " + member.getUserId());
            }
        } else {
            Log.e("MembersAdapter", "Position out of bounds: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName, textViewEmail, textViewRole;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewMemberName);
            textViewEmail = itemView.findViewById(R.id.textViewMemberEmail);
            textViewRole = itemView.findViewById(R.id.textViewMemberRole);
            Log.d("MembersAdapter", "ViewHolder created");
        }

        public void bind(User member) {
            String fullName = member.getFirstName() + " " + member.getLastName();
            textViewName.setText(fullName);
            textViewEmail.setText(member.getEmail());
            textViewRole.setText("Rola: " + member.getRole());
            Log.d("MembersAdapter", "Member details set: " + fullName);
        }
    }
}
