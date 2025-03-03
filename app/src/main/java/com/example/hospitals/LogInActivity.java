package com.example.hospitals;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitals.db.UserDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * LogInActivity handles the user login process.
 * It checks for previously saved login credentials from a local SQLite database.
 * If found, it automatically logs in the user; otherwise, it allows manual login.
 * Additionally, it provides options for Google Sign-In and navigation to the SignUpActivity.
 */
public class LogInActivity extends AppCompatActivity {

    // UI components for login
    private EditText editTextId, editTextPassword;
    private Button buttonLogIn, buttonSignUp;
    private ImageView googleLogo;
    private TextView googleText;

    // Firebase Database reference pointing to "Users"
    private DatabaseReference databaseReference;
    // Local SQLite database helper for saving/retrieving login info.
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase reference (assuming user data is stored under "Users")
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize local SQLite database
        userDatabase = new UserDatabase(this);
        userDatabase.open();

        // Check for previously saved login credentials
        String[] lastLogin = userDatabase.getLastLogin();
        if (lastLogin != null && lastLogin.length >= 2) {
            // Automatically attempt login with saved credentials.
            String savedId = lastLogin[0];
            String savedPassword = lastLogin[1];
            loginUser(savedId, savedPassword);
        }

        // Initialize UI components from layout.
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        googleLogo = findViewById(R.id.googleLogo);
        googleText = findViewById(R.id.googleText);

        // Set up Log In button click listener.
        buttonLogIn.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input fields.
            if (validateInputs(id, password)) {
                loginUser(id, password);
            }
        });

        // Redirect to Sign Up Activity when the Sign Up button is clicked.
        buttonSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            finish();
        });

        // Set up Google Sign-In (optional functionality).
        googleLogo.setOnClickListener(v -> handleGoogleSignIn());
        googleText.setOnClickListener(v -> handleGoogleSignIn());
    }

    /**
     * Validates the login input fields.
     *
     * @param id       The user ID input.
     * @param password The password input.
     * @return True if both inputs are non-empty; false otherwise.
     */
    private boolean validateInputs(String id, String password) {
        if (TextUtils.isEmpty(id)) {
            editTextId.setError("ID is required.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
            return false;
        }
        return true;
    }

    /**
     * Logs in the user by checking credentials in Firebase.
     *
     * @param id       The user ID.
     * @param password The password.
     */
    private void loginUser(String id, String password) {
        // Check if the user exists in Firebase.
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the stored password.
                    String storedPassword = snapshot.child("password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        // Successful login.
                        Toast.makeText(LogInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Save the login info locally for future use.
                        userDatabase.saveUserLogin(id, password);
                        // Redirect to MainActivity2 or your main dashboard.
                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Password mismatch.
                        Toast.makeText(LogInActivity.this, "Invalid password. Please try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // User ID not found in Firebase.
                    Toast.makeText(LogInActivity.this, "ID does not exist. Please sign up.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LogInActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Handles Google Sign-In. Currently, this functionality is not implemented.
     */
    private void handleGoogleSignIn() {
        Toast.makeText(LogInActivity.this, "Google Sign-In is not yet implemented.", Toast.LENGTH_SHORT).show();
        // Add your Google Sign-In logic here if needed.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the local user database when activity is destroyed.
        userDatabase.close();
    }
}
