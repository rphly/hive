package com.example.hive.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hive.R;
import com.example.hive.activities.SignIn;
import com.example.hive.models.User;
import com.example.hive.services.Response;
import com.example.hive.services.UserService;
import com.example.hive.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Me extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth firebase;
    private User currentUser;
    private TextView fullName, email, currentStatus;
    private ImageButton profilePic;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public Me() {
        firebase = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        String currentUserId = firebase.getCurrentUser().getUid();

        // get current user
        UserService.getUserById(currentUserId, new Response() {
            @Override
            public void onSuccess(Object data) {
                currentUser = User.fromObject(data);
                fullName.setText(currentUser.getFullName());
                email.setText(currentUser.getEmail());
                currentStatus.setText(currentUser.getStatus() == Constants.Status.AVAILABLE ? "Available" : "Unavailable");

                // get profile pic
                StorageReference ref = storageReference.child(String.format("images/%s/profile_pic", currentUser.getId()));
                ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profilePic.setImageBitmap(bmp);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "Failed to load profile pic", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure() {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullName = view.findViewById(R.id.profile_fullName);
        currentStatus = view.findViewById(R.id.profile_currentStatus);
        email = view.findViewById(R.id.profile_email);
        profilePic = view.findViewById(R.id.profile_pic);

        profilePic.setOnClickListener(l -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            galleryLauncher.launch(intent);

        });

        // signOut button
        Button signOutBtn = view.findViewById(R.id.sign_out);
        signOutBtn.setText("Sign Out");
        signOutBtn.setOnClickListener((View v) -> signOut());
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignIn.class);
        getActivity().finishAffinity();
        this.startActivity(intent);
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        Uri filePath = data.getData();
                        try {
                            Bitmap bitmap = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getContext().getContentResolver(), filePath);
                            profilePic.setImageBitmap(bitmap);

                            ProgressDialog progressDialog
                                    = new ProgressDialog(getContext());
                            progressDialog.setTitle("Uploading image...");

                            StorageReference ref = storageReference.child(String.format("images/%s/profile_pic", currentUser.getId()));
                            ref.putFile(filePath).addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                        @Override
                                        public void onSuccess(
                                                UploadTask.TaskSnapshot taskSnapshot)
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Upload success", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnProgressListener(
                                    new OnProgressListener<UploadTask.TaskSnapshot>() {

                                        // Progress Listener for loading
                                        // percentage on the dialog box
                                        @Override
                                        public void onProgress(
                                                UploadTask.TaskSnapshot taskSnapshot)
                                        {
                                            double progress
                                                    = (100.0
                                                    * taskSnapshot.getBytesTransferred()
                                                    / taskSnapshot.getTotalByteCount());
                                            progressDialog.setMessage(
                                                    "Uploaded " + (int) progress + "%");
                                        }
                                    });

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
}