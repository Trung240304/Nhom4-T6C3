package com.example.storephone.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.storephone.R;
import com.example.storephone.models.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    final int PICK_IMAGE_REQUEST = 1;
    final int TAKE_PHOTO_REQUEST = 2;

    ImageView profileImage;
    TextInputEditText profileName, profileEmail, profileNumber, profileAddress;
    MaterialButton updateButton;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef;
    StorageReference storageRef;
    private Uri imageUri;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        Bundle extras = result.getData().getExtras();
                        assert extras != null;
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        profileImage.setImageBitmap(imageBitmap);
                        assert imageBitmap != null;
                        imageUri = getImageUri(imageBitmap);
                    }
                });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        if (currentUser != null) {
            userRef = database.getReference("Users").child(currentUser.getUid());
        }

        // Initialize views
        profileImage = view.findViewById(R.id.profile_img);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profileNumber = view.findViewById(R.id.profile_number);
        profileAddress = view.findViewById(R.id.profile_address);
        updateButton = view.findViewById(R.id.update);

        // Load user data
        loadUserData();

        // Set up update button
        updateButton.setOnClickListener(v -> updateUserData());

        // Set up profile image click listener
        profileImage.setOnClickListener(v -> showImagePickerOptions());

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Bạn cần cấp quyền sử dụng camera để sử dụng tính năng này. Vui lòng vào phần Cài đặt và cấp quyền truy cập camera.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void loadUserData() {
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        if (user != null) {
                            profileName.setText(user.getName());
                            profileEmail.setText(user.getEmail());
                            profileNumber.setText(user.getPhoneNumber());
                            profileAddress.setText(user.getAddress());
                            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                                Glide.with(ProfileFragment.this)
                                        .load(user.getProfileImageUrl())
                                        .placeholder(R.drawable.user_img)
                                        .into(profileImage);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Lỗi khi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserData() {
        String name = Objects.requireNonNull(profileName.getText()).toString().trim();
        String email = Objects.requireNonNull(profileEmail.getText()).toString().trim();
        String phoneNumber = Objects.requireNonNull(profileNumber.getText()).toString().trim();
        String address = Objects.requireNonNull(profileAddress.getText()).toString().trim();

        if (imageUri != null) {
            uploadImage(name, email, phoneNumber, address);
        } else {
            updateUserInfoWithoutImage(name, email, phoneNumber, address);
        }
    }

    private void uploadImage(String name, String email, String phoneNumber, String address) {
        if (auth.getCurrentUser() != null) {
            StorageReference fileReference = storageRef.child(auth.getCurrentUser().getUid() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                updateUserInfo(name, email, phoneNumber, address, imageUrl);
                            }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updateUserInfo(String name, String email, String phoneNumber, String address, String imageUrl) {
        UserModel updatedUser = new UserModel(name, email, phoneNumber, address, imageUrl);
        userRef.setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                // Gửi local broadcast để thông báo dữ liệu đã thay đổi
                Intent intent = new Intent("com.example.storephone.USER_DATA_CHANGED");
                intent.putExtra("updatedImageUrl", imageUrl);
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
            } else {
                Toast.makeText(getContext(), "Lỗi khi cập nhật thông tin: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfoWithoutImage(String name, String email, String phoneNumber, String address) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel currentUser = dataSnapshot.getValue(UserModel.class);
                    String currentImageUrl = (currentUser != null) ? currentUser.getProfileImageUrl() : null;
                    updateUserInfo(name, email, phoneNumber, address, currentImageUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi khi cập nhật thông tin: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Xử lý hình ảnh trong Profile(Chụp, chọn)
    private void showImagePickerOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_image_picker_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        View optionGallery = bottomSheetView.findViewById(R.id.option_gallery);
        View optionCamera = bottomSheetView.findViewById(R.id.option_camera);
        View optionCancel = bottomSheetView.findViewById(R.id.option_cancel);

        optionGallery.setOnClickListener(v -> {
            openGallery();
            bottomSheetDialog.dismiss();
        });

        optionCamera.setOnClickListener(v -> {
            checkCameraPermissionAndOpen();
            bottomSheetDialog.dismiss();
        });

        optionCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            takePictureLauncher.launch(takePictureIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Không thể mở camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).into(profileImage);
            } else if (requestCode == TAKE_PHOTO_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profileImage.setImageBitmap(imageBitmap);

                    assert imageBitmap != null;
                    imageUri = getImageUri(imageBitmap);
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "ProfileImage", null);
        return Uri.parse(path);
    }
}