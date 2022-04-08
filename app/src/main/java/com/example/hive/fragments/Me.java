package com.example.hive.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hive.MainActivity;
import com.example.hive.R;
import com.example.hive.activities.QR.QRCodeScanner;
import com.example.hive.activities.SignIn;
import com.example.hive.models.User;
import com.example.hive.services.BaseService;
import com.example.hive.services.Response;
import com.example.hive.services.UserService;
import com.example.hive.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class Me extends Fragment{
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth firebase;
    private FirebaseUser user;
    private User currentUser;
    private TextView currentStatus;
    private ImageButton profilePic;
    private EditText profileBio, email, fullName;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference collection;
    private Boolean enableEdit;
    private Button editProfileBtn, saveEditBtn;

    public Me() { firebase = FirebaseAuth.getInstance(); }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        enableEdit = false;

        String currentUserId = firebase.getCurrentUser().getUid();

        collection = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        // get current user
        UserService.getUserById(currentUserId, new Response() {
            @Override
            public void onSuccess(Object data) {
                currentUser = User.fromObject(data);
                fullName.setText(currentUser.getFullName());
                email.setText(currentUser.getEmail());
                profileBio.setText(currentUser.getBio());
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
        profileBio = view.findViewById(R.id.profile_bio);

        profilePic.setOnClickListener(l -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            galleryLauncher.launch(intent);

        });

        //set edit fields for name email to be invisible
        profileBio.setEnabled(enableEdit);
        fullName.setEnabled(enableEdit);
        email.setEnabled(enableEdit);

        // signOut button
        Button signOutBtn = view.findViewById(R.id.sign_out);
        signOutBtn.setText("Sign Out");
        signOutBtn.setOnClickListener((View v) -> signOut());

        // editProfile button
        editProfileBtn = view.findViewById(R.id.editProfile);
        editProfileBtn.setText("Edit Profile");
        editProfileBtn.setOnClickListener((View v) -> editProfile());

        //saveEdit button
        saveEditBtn = view.findViewById(R.id.saveEdits);
        saveEditBtn.setText("Save");
        saveEditBtn.setOnClickListener((View v) -> saveEdits());
    }

    private void editProfile(){
        if(!enableEdit){
            editProfileBtn.setVisibility(View.GONE);
            saveEditBtn.setVisibility(View.VISIBLE);
            enableEdit = true;
            fullName.setEnabled(enableEdit);
            email.setEnabled(enableEdit);
            profileBio.setEnabled(enableEdit);
        }
    }

    private void saveEdits(){
        enableEdit = false;
        fullName.setEnabled(enableEdit);
        email.setEnabled(enableEdit);
        profileBio.setEnabled(enableEdit);
        HashMap toUpdate = new HashMap();
        toUpdate.put("bio", profileBio.getText().toString().trim());
        toUpdate.put("fullName", fullName.getText().toString().trim());
        toUpdate.put("email", email.getText().toString().trim());
        try {
            BaseService.updateFirebase(collection, toUpdate, new Response() {
                @Override
                public void onSuccess(Object data) {
                    Toast.makeText(getActivity(), "Updated Profile", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Failed to update bio.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to update bio.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        saveEditBtn.setVisibility(View.GONE);
        editProfileBtn.setVisibility(View.VISIBLE);
        enableEdit = false;
        user.updateEmail(email.getText().toString().trim());
    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getActivity().getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.getActivity().dispatchTouchEvent( event );
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