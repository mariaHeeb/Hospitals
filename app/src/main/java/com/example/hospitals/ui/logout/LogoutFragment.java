package com.example.hospitals.ui.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hospitals.LogInActivity;
import com.example.hospitals.MainActivity2;
import com.example.hospitals.R;
import com.example.hospitals.db.UserDatabase;


public class LogoutFragment extends Fragment {
    private UserDatabase userDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userDatabase = new UserDatabase(getContext());
        userDatabase.open();
        logoutUser();

        return inflater.inflate(R.layout.fragment_profile, container, false);

    }
    private void logoutUser() {
        // Clear saved login information from the database
        userDatabase.clearLoginData();

        // Redirect the user back to the login activity
        startActivity(new Intent(getContext(), LogInActivity.class));
        getActivity().finish(); // Finish this activity so the user cannot navigate back
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}