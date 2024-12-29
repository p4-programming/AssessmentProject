package com.deepak.assesment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.assesment.R;
import com.deepak.assesment.dao.DataTable;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.UserViewHolder> {

    private List<DataTable> userList;

    public ItemAdapter(List<DataTable> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        DataTable user = userList.get(position);
        holder.nameTV.setText(user.getName());
        holder.descriptionTV.setText(user.getBody());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUsers(List<DataTable> newUsers) {
        // Avoid adding duplicates
        for (DataTable newUser : newUsers) {
            boolean alreadyExists = false;
            for (DataTable existingUser : userList) {
                if (existingUser.getId() == newUser.getId()) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                userList.add(newUser);
            }
        }
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, descriptionTV;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.name);
            descriptionTV = itemView.findViewById(R.id.description);
        }
    }
}
