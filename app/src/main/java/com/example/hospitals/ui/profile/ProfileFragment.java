package com.example.hospitals.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hospitals.R;
import com.example.hospitals.adapters.AppointmentAdapter;
import com.example.hospitals.data.Appointment;
import com.example.hospitals.db.UserDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ProfileFragment loads and displays the user profile (name, email, phone, blood type,
 * and profile picture) and a list of the user's appointments. It also allows the user to edit or
 * delete an appointment.
 */
public class ProfileFragment extends Fragment implements AppointmentAdapter.OnAppointmentActionListener {

    // UI elements for displaying profile data.
    private TextView userNameText, userRoleText, userEmailText, userPhoneText, user_blood_type;
    private ImageView userImageView;
    // RecyclerView for showing the user's appointments.
    private RecyclerView recyclerViewAppointmentsProfile;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;

    // Local database helper to retrieve user information (such as userId).
    private UserDatabase userDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize profile UI components.
        userNameText = rootView.findViewById(R.id.user_name);
        userRoleText = rootView.findViewById(R.id.user_role);
        userEmailText = rootView.findViewById(R.id.user_email);
        userPhoneText = rootView.findViewById(R.id.user_phone);
        user_blood_type = rootView.findViewById(R.id.user_blood_type);
        userImageView = rootView.findViewById(R.id.user_picture);

        // Set up the RecyclerView for appointments.
        recyclerViewAppointmentsProfile = rootView.findViewById(R.id.recyclerViewAppointmentsProfile);
        recyclerViewAppointmentsProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentList = new ArrayList<>();
        // Pass 'this' as the OnAppointmentActionListener and a valid Context.
        appointmentAdapter = new AppointmentAdapter(appointmentList, this, getContext());
        recyclerViewAppointmentsProfile.setAdapter(appointmentAdapter);

        // Initialize the local user database and fetch the user ID.
        userDatabase = new UserDatabase(getContext());
        userDatabase.open();
        String[] lastLogin = userDatabase.getLastLogin();
        String userId = (lastLogin != null && lastLogin.length > 0) ? lastLogin[0] : null;

        if (userId != null) {
            // Fetch profile data and user's appointments from Firebase.
            fetchUserDataFromFirebase(userId);
            fetchUserAppointments(userId);
        } else {
            Toast.makeText(getContext(), "User not found. Please log in again.", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    /**
     * Fetches the user profile data from Firebase at "Users/{userId}" and displays it.
     *
     * @param userId the user's unique identifier.
     */
    private void fetchUserDataFromFirebase(String userId) {
        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Extract profile information.
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String id = dataSnapshot.child("id").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                            String pictureUrl = dataSnapshot.child("image").getValue(String.class);
                            String bloodType = dataSnapshot.child("bloodType").getValue(String.class);

                            // Set the profile views.
                            userNameText.setText(name != null ? name : "N/A");
                            userRoleText.setText("ID " + (id != null ? id : "N/A"));
                            userEmailText.setText("Email: " + (email != null ? email : "N/A"));
                            userPhoneText.setText("Phone: " + (phone != null ? phone : "N/A"));
                            user_blood_type.setText("Blood Type: " + (bloodType != null ? bloodType : "N/A"));

                            // Load the profile picture using Glide.
                            if (pictureUrl != null && !pictureUrl.isEmpty()) {
                                Glide.with(getContext())
                                        .load(pictureUrl)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(userImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Log or handle database error.
                        Toast.makeText(getContext(), "Error loading profile: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Fetches the user's appointments from Firebase at "Users/{userId}/appointments" and updates the RecyclerView.
     *
     * @param userId the user's unique identifier.
     */
    private void fetchUserAppointments(String userId) {
        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("appointments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentList.clear();
                        for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                            Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                // Save the Firebase key to the Appointment for later editing or deletion.
                                appointment.setId(appointmentSnapshot.getKey());
                                appointmentList.add(appointment);
                            }
                        }
                        appointmentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading appointments: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Called when the user taps the "Edit" button on an appointment.
     *
     * @param appointment the appointment to be edited.
     */
    @Override
    public void onEdit(Appointment appointment) {
        // Open a dialog to allow the user to edit the appointment's date.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Appointment");

        // Inflate the custom dialog layout.
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_dialog_edit_appointment, null);
        final EditText etNewDate = dialogView.findViewById(R.id.etNewDate);
        etNewDate.setText(appointment.getDate());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newDate = etNewDate.getText().toString().trim();
                    if (!TextUtils.isEmpty(newDate)) {
                        updateAppointment(appointment, newDate);
                    } else {
                        Toast.makeText(getContext(), "Please enter a valid date", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Called when the user taps the "Delete" button on an appointment.
     *
     * @param appointment the appointment to be removed.
     */
    @Override
    public void onDelete(Appointment appointment) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton("Delete", (dialog, which) -> removeAppointment(appointment))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Updates an appointment's date in Firebase.
     *
     * @param appointment the appointment to update.
     * @param newDate     the new date value.
     */
    private void updateAppointment(Appointment appointment, String newDate) {
        String userId = userDatabase.getLastLogin()[0];
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("appointments")
                .child(appointment.getId())
                .child("date")
                .setValue(newDate, (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getContext(), "Appointment updated", Toast.LENGTH_SHORT).show();
                        fetchUserAppointments(userId);
                    } else {
                        Toast.makeText(getContext(), "Error updating appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Removes an appointment from Firebase.
     *
     * @param appointment the appointment to remove.
     */
    private void removeAppointment(Appointment appointment) {
        String userId = userDatabase.getLastLogin()[0];
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("appointments")
                .child(appointment.getId())
                .removeValue((error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getContext(), "Appointment removed", Toast.LENGTH_SHORT).show();
                        fetchUserAppointments(userId);
                    } else {
                        Toast.makeText(getContext(), "Error removing appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
