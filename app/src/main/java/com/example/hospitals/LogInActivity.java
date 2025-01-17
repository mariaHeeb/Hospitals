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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private EditText editTextId, editTextPassword;
    private Button buttonLogIn, buttonSignUp;
    private ImageView googleLogo;
    private TextView googleText;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI components
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        googleLogo = findViewById(R.id.googleLogo);
        googleText = findViewById(R.id.googleText);

        // Log In Button Click Listener
        buttonLogIn.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(id, password)) {
                loginUser(id, password);
            }
        });

        // Redirect to Sign Up Activity
        buttonSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            finish();
        });

        // Google Sign-In (Optional)
        googleLogo.setOnClickListener(v -> handleGoogleSignIn());
        googleText.setOnClickListener(v -> handleGoogleSignIn());
    }

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

    private void loginUser(String id, String password) {
        // Check if the ID exists in the database
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // ID exists, retrieve the stored password
                    String storedPassword = snapshot.child("password").getValue(String.class);

                    if (storedPassword != null && storedPassword.equals(password)) {
                        // Password matches
                        Toast.makeText(LogInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Redirect to the main activity or dashboard
                        startActivity(new Intent(LogInActivity.this, MainActivity2.class));
                        finish();
                    } else {
                        // Password does not match
                        Toast.makeText(LogInActivity.this, "Invalid password. Please try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // ID does not exist
                    Toast.makeText(LogInActivity.this, "ID does not exist. Please sign up.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LogInActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleGoogleSignIn() {
        Toast.makeText(LogInActivity.this, "Google Sign-In is not yet implemented.", Toast.LENGTH_SHORT).show();
        // Add Google Sign-In logic here if needed
    }
}
