package com.example.firebaseapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.R;
import com.example.firebaseapp.models.User;

import java.util.List;

public class MembersSelectAdapter extends RecyclerView.Adapter<MembersSelectAdapter.MemberViewHolder> {

    private Context context;
    private List<User> membersList;
    private List<String> selectedMemberIds;
    private OnMemberSelectListener listener;

    public interface OnMemberSelectListener {
        void onMemberSelected(String userId, boolean isSelected);
    }

    public MembersSelectAdapter(Context context, List<User> membersList, List<String> selectedMemberIds, OnMemberSelectListener listener) {
        this.context = context;
        this.membersList = membersList;
        this.selectedMemberIds = selectedMemberIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member_select, parent, false);
        return new MemberViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = membersList.get(position);
        holder.bind(user, selectedMemberIds.contains(user.getUserId()));
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMemberName;
        CheckBox checkBoxSelect;

        public MemberViewHolder(@NonNull View itemView, OnMemberSelectListener listener) {
            super(itemView);
            textViewMemberName = itemView.findViewById(R.id.textViewMemberName);
            checkBoxSelect = itemView.findViewById(R.id.checkBoxSelect);

            checkBoxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String userId = (String) buttonView.getTag();
                if (listener != null && userId != null) {
                    listener.onMemberSelected(userId, isChecked);
                }
            });
        }

        public void bind(User user, boolean isSelected) {
            textViewMemberName.setText(user.getFirstName() + " " + user.getLastName());
            checkBoxSelect.setChecked(isSelected);
            checkBoxSelect.setTag(user.getUserId());
        }
    }
}
