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

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitals.data.MedicalHistory;
import com.example.hospitals.data.User;
import com.example.hospitals.db.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * SignUpActivity allows new users to register an account.
 * It validates user inputs, checks if the user ID already exists,
 * registers the user with Firebase Authentication, and then saves the
 * user's details to the Firebase Realtime Database.
 */
public class SignUpActivity extends AppCompatActivity {

    // UI components for registration
    private EditText editTextId, editTextName, editTextEmail, editTextPhone, editTextPassword;
    private Spinner spinnerBloodType;
    private Button buttonSignUp;
    private TextView textViewLogin;

    // Firebase Auth and Realtime Database references.
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    // Local database helper for saving login info.
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Authentication and Database references.
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize local SQLite database for saving user login info.
        userDatabase = new UserDatabase(this);
        userDatabase.open();

        // Check if there's previously saved login info and auto-login if available.
        String[] lastLogin = userDatabase.getLastLogin();
        if (lastLogin != null && lastLogin.length >= 2) {
            String savedId = lastLogin[0];
            String savedPassword = lastLogin[1];
          //  loginUser(savedId, savedPassword);
        }

        // Initialize UI components.
        editTextId = findViewById(R.id.editTextId);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        spinnerBloodType = findViewById(R.id.spinnerBloodType);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Populate blood types spinner from resources.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_types,             // Reference to your string-array resource for blood types.
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodType.setAdapter(adapter);

        // Set up the Sign Up button click listener.
        buttonSignUp.setOnClickListener(v -> {
            // Retrieve input values.
            String id = editTextId.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String bloodType = spinnerBloodType.getSelectedItem().toString();
            String password = editTextPassword.getText().toString().trim();

            // Validate inputs before proceeding.
            if (validateInputs(id, name, email, phone, password)) {
                // Create a new MedicalHistory object (customize as needed).
                MedicalHistory medicalHistory = new MedicalHistory();
                // Proceed with user registration.
                registerUser(email, password, id, name, phone, bloodType, medicalHistory);
            }
        });

        // Set up the Login redirection click listener.
        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            finish();
        });
    }

    /**
     * Validates the user's registration inputs.
     *
     * @param id       the user ID.
     * @param name     the user name.
     * @param email    the user email.
     * @param phone    the user phone number.
     * @param password the user password.
     * @return true if all inputs are valid; false otherwise.
     */
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

    /**
     * Registers the user with Firebase Authentication and saves their details to the Realtime Database.
     *
     * @param email         The user's email.
     * @param password      The user's password.
     * @param id            The user ID.
     * @param name          The user's name.
     * @param phone         The user's phone number.
     * @param bloodType     The user's blood type.
     * @param medicalHistory A MedicalHistory object for the user.
     */
    private void registerUser(String email, String password, String id, String name, String phone, String bloodType, MedicalHistory medicalHistory) {
        // First, check if the given ID already exists in Firebase.
        databaseReference.child(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // ID already exists.
                    Toast.makeText(SignUpActivity.this, "ID already exists. Please use a different ID.", Toast.LENGTH_LONG).show();
                } else {
                    // Proceed with registration using Firebase Authentication.
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        // Save the user data to Firebase Realtime Database.
                                        saveUserToDatabase(id, name, password, phone, bloodType, email, medicalHistory);
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

    /**
     * Saves the new user object to Firebase under "Users/{id}".
     *
     * @param id            the user ID.
     * @param name          the user's name.
     * @param password      the user's password.
     * @param phone         the user's phone number.
     * @param bloodType     the user's blood type.
     * @param gmail         the user's email.
     * @param medicalHistory the user's medical history.
     */
    private void saveUserToDatabase(String id, String name, String password, String phone, String bloodType, String gmail, MedicalHistory medicalHistory) {
        // Create a new User object.
        User user = new User(id, name, password, phone, bloodType, gmail, medicalHistory);

        // Save the User object to the Realtime Database.
        databaseReference.child(id).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "User saved successfully!", Toast.LENGTH_SHORT).show();
                // Redirect to the main activity.
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
