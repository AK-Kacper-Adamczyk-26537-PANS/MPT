package com.example.firebaseapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Importy...
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.R;
import com.example.firebaseapp.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public interface OnUserRoleChangeListener {
        void onUserRoleChange(String userId, String newRole);
    }

    private Context context;
    private List<User> userList;
    private OnUserRoleChangeListener listener;

    public UsersAdapter(Context context, List<User> userList, OnUserRoleChangeListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_role, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUserName.setText(user.getFirstName() + " " + user.getLastName());
        holder.textViewEmail.setText(user.getEmail());

        // Ustawienie Spinnera z rolami
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.role_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerRole.setAdapter(adapter);

        // Ustawienie aktualnej roli
        int spinnerPosition = adapter.getPosition(user.getRole());
        holder.spinnerRole.setSelection(spinnerPosition);

        holder.buttonChangeRole.setOnClickListener(v -> {
            String newRole = holder.spinnerRole.getSelectedItem().toString();
            listener.onUserRoleChange(user.getUserId(), newRole);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewEmail;
        Spinner spinnerRole;
        Button buttonChangeRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            spinnerRole = itemView.findViewById(R.id.spinnerRole);
            buttonChangeRole = itemView.findViewById(R.id.buttonChangeRole);
        }
    }
}
