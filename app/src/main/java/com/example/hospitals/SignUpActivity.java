package com.example.hospitals;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitals.data.MedicalHistory;
import com.example.hospitals.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextId, editTextName, editTextEmail, editTextPhone, editTextPassword;
    private Spinner spinnerBloodType;
    private Button buttonSignUp;
    private TextView textViewLogin;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Realtime Database
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI components
        editTextId = findViewById(R.id.editTextId);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        spinnerBloodType = findViewById(R.id.spinnerBloodType);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Populate Spinner with blood types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_types, // Reference to the blood types array in strings.xml
                android.R.layout.simple_spinner_item // Layout for each item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdown layout
        spinnerBloodType.setAdapter(adapter);

        // Sign Up Button Click Listener
        buttonSignUp.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String bloodType = spinnerBloodType.getSelectedItem().toString();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(id, name, email, phone, password)) {
                MedicalHistory medicalHistory = new MedicalHistory(); // Create a new MedicalHistory object (modify as per your logic)
                registerUser(email, password, id, name, phone, bloodType, medicalHistory);
            }
        });

        // Redirect to Login
        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            finish();
        });
    }

    private boolean validateInputs(String id, String name, String email, String phone, String password) {

        if (TextUtils.isEmpty(id)) {

            editTextId.setError("ID is required.");
            return false;
        }
        if (TextUtils.isEmpty(name)) {

            editTextName.setError("Name is required.");
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            editTextEmail.setError("Valid email is required.");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {

            editTextPhone.setError("Phone number is required.");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {

            editTextPassword.setError("Password must be at least 6 characters.");
            return false;
        }

        return true;
    }

    private void registerUser(String email, String password, String id, String name, String phone, String bloodType, MedicalHistory mH) {

        // Check if the ID already exists in the database
        databaseReference.child(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // ID already exists in the database
                    Toast.makeText(SignUpActivity.this, "ID already exists. Please use a different ID.", Toast.LENGTH_LONG).show();
                } else {
                    // Proceed with registration since the ID is not in use
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        saveUserToDatabase(id, name, password, phone, bloodType, email, mH);
                                    }
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Registration failed: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to check ID: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserToDatabase( String id, String name, String password, String phone, String bloodType, String gmail, MedicalHistory mH) {
        // Create a User object
        User user = new User(id, name, password, phone, bloodType, gmail, mH);

        // Save User object to Realtime Database
        databaseReference.child(id).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "User saved successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, MainActivity2.class)); // Redirect to main activity
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
