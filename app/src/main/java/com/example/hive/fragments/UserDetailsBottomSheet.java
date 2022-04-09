package com.example.hive.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.hive.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserDetailsBottomSheet extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.user_details,
                container, false);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        TextView name = v.findViewById(R.id.user_details_name);
        TextView emailView = v.findViewById(R.id.user_details_email);
        TextView statusText = v.findViewById(R.id.user_details_status);
        TextView location = v.findViewById(R.id.user_details_location);
        TextView bio = v.findViewById(R.id.user_details_bio);
        ImageView profilePic = v.findViewById(R.id.user_details_profile_pic);
        ImageView statusIndicator = v.findViewById(R.id.user_details_status_badge);

        Bundle args = getArguments();
        String id = args.getString("id");
        String locationStr = args.getString("location", "");
        String email = args.getString("email", "");
        int status = args.getInt("status");
        String bioStr = args.getString("bio");
        name.setText(args.getString("fullName", ""));

        // email
        emailView.setText(email);
        emailView.setOnClickListener(l -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            startActivity(intent);
        });

        // location
        if (!locationStr.isEmpty()) {
            location.setText(String.format("Desk %s",locationStr));
        }

        // set status
        statusText.setText(status == 1 ? "Available" : "Do not disturb");
        if (status == 1) {
            statusIndicator.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.green_badge));
        } else {
            statusIndicator.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.yellow_badge));
        }


        // get profile pic
        StorageReference ref = storageReference.child(String.format("images/%s/profile_pic", id));
        ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePic.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        // bio
        bio.setText(bioStr);

        return v;
    }
}