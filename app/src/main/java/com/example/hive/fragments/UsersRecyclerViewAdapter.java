package com.example.hive.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hive.R;
import com.example.hive.models.Desk;
import com.example.hive.models.User;
import com.example.hive.services.DeskService;
import com.example.hive.services.Response;
import com.example.hive.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private ArrayList<User> users;
    private FirebaseAuth firebase;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public UsersRecyclerViewAdapter(ArrayList<User> items) {
        firebase = FirebaseAuth.getInstance();
        users = items;
    }

    public void setData(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_results_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User user = users.get(position);

        // set user details
        UserDetailsBottomSheet bottomSheet = new UserDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putString("id", user.getId());
        args.putString("fullName", user.getFullName());
        args.putString("email", user.getEmail());
        args.putInt("status", user.getStatus().equals(Constants.Status.AVAILABLE) ? 1 : 0);
        bottomSheet.setArguments(args);

        holder.row.setOnClickListener(l -> {
            bottomSheet.show(((AppCompatActivity) holder.row.getContext()).getSupportFragmentManager(), user.getId());
        });

        // set full name
        holder.name.setText(user.getFullName());

        // set status indicator
        holder.statusIndicator.setImageDrawable(null);
        if (user.getStatus().equals(Constants.Status.AVAILABLE)) {
            holder.statusIndicator.setImageDrawable(ContextCompat.getDrawable(holder.statusIndicator.getContext(), R.drawable.green_badge));
        } else if (user.getStatus().equals(Constants.Status.DO_NOT_DISTURB)) {
            holder.statusIndicator.setImageDrawable(ContextCompat.getDrawable(holder.statusIndicator.getContext(), R.drawable.yellow_badge));
        }

        // set current location
        // honestly not ideal lol
        holder.location.setText(null);
        DeskService.getAllDesks(new Response() {
            @Override
            public void onSuccess(Object data) {
                ArrayList<Desk> desks = Desk.fromObjects(data);
                desks.removeIf(d -> !d.getCurrentUserId().equals(user.getId()));
                if (!desks.isEmpty()) {
                    String label = desks.get(0).getLabel();
                    holder.location.setText(String.format("Currently located at %s", label));

                    // update bottomsheet also
                    args.putString("location", label);
                    bottomSheet.setArguments(args);
                }
            }

            @Override
            public void onFailure() {
            }
        });

        // set profile pic

        StorageReference ref = storageReference.child(String.format("images/%s/profile_pic", user.getId()));
        holder.profilePic.setImageDrawable(null);
        ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.profilePic.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.profilePic.setImageDrawable(ContextCompat.getDrawable(holder.profilePic.getContext(), R.drawable.user_circle));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView row;
        TextView name, location, userDetailsName, userDetailsEmail;
        ImageView profilePic, statusIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.search_result_row_card);
            name = itemView.findViewById(R.id.searchRowText);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            profilePic = itemView.findViewById(R.id.searchResults_profilePic);
            location = itemView.findViewById(R.id.search_results_location);
            userDetailsName = itemView.findViewById(R.id.user_details_name);
            userDetailsEmail = itemView.findViewById(R.id.user_details_email);
        }
    }
}