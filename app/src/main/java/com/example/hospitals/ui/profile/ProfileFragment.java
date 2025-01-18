package com.example.hospitals.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hospitals.R;
import com.example.hospitals.db.DatabaseHelper;
import com.example.hospitals.db.UserDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

public class ProfileFragment extends Fragment {

    private TextView userNameText, userRoleText, userEmailText, userPhoneText,user_blood_type;
    private ImageView userImageView;

    private UserDatabase userDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the views
        userNameText = rootView.findViewById(R.id.user_name);
        userRoleText = rootView.findViewById(R.id.user_role);
        userEmailText = rootView.findViewById(R.id.user_email);
        userPhoneText = rootView.findViewById(R.id.user_phone);
        userImageView = rootView.findViewById(R.id.user_picture);
        user_blood_type= rootView.findViewById(R.id.user_blood_type);
        // Initialize SQLite DatabaseHelper
        userDatabase = new UserDatabase(getContext());
        userDatabase.open();
        // Fetch the user ID from SQLite
        String[] lastLogin = userDatabase.getLastLogin();
        String userId=lastLogin[0];
        if (userId != null) {
            // Fetch user data from Firebase
            fetchUserDataFromFirebase(userId);
        }

        return rootView;
    }

    private void fetchUserDataFromFirebase(String userId) {
        // Reference to Firebase Realtime Database, assuming the user data is stored under "users"
        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Fetch the data from Firebase
                        if (dataSnapshot.exists()) {
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String id = dataSnapshot.child("id").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                            String pictureUrl = dataSnapshot.child("image").getValue(String.class);
                            String bloodType = dataSnapshot.child("bloodType").getValue(String.class);

                            // Set the data to the views
                            userNameText.setText(name);
                            userRoleText.setText("ID "+id);
                            userEmailText.setText("Email: " + email);
                            userPhoneText.setText("Phone: " + phone);
                            user_blood_type.setText("Blood Type: " + bloodType);
                            // Load profile picture using Picasso or Glide
                            if (pictureUrl != null) {
                                Glide.with(getContext())
                                        .load(pictureUrl)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop())) // Apply circular transformation
                                        .into(userImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }
}
