package com.example.midnightgroove;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileLarge;
    private TextView tvUserName, tvUserEmail;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private FirebaseStorage storage;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadProfilePicture(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        ivProfileLarge = view.findViewById(R.id.ivProfileLarge);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        progressBar = view.findViewById(R.id.profileProgressBar);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        if (user != null) {
            tvUserEmail.setText(user.getEmail());
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else {
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    tvUserName.setText(email.split("@")[0]);
                }
            }

            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl())
                        .placeholder(R.drawable.circle_background)
                        .into(ivProfileLarge);
            }
        }

        ivProfileLarge.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.llProfileLikedSongs).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LikedSongsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.llProfileRecents).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RecentlyPlayedFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.llProfileTopArtists).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ArtistsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.llProfileMyUploads).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UploadedSongsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void uploadProfilePicture(Uri uri) {
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        String userId = user.getUid();
        StorageReference profilePicRef = storage.getReference().child("profile_pics/" + userId);

        profilePicRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    updateUserProfile(downloadUri);
                }))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserProfile(Uri downloadUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(downloadUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Glide.with(this).load(downloadUri)
                                .placeholder(R.drawable.circle_background)
                                .into(ivProfileLarge);
                        Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
