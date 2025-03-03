package com.example.hospitals;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitals.adapters.AppointmentAdapter;
import com.example.hospitals.data.Appointment;
import com.example.hospitals.db.UserDatabase;
import com.example.hospitals.utils.DateValidatorUnavailable;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * AppointmentsActivity allows users to book appointments with a hospital.
 * It loads existing appointments, allows selection of a date (via a MaterialDatePicker)
 * and available working hours, and saves the appointment under both the hospital node
 * (with sequential numeric keys) and the user's node in Firebase.
 */
public class AppointmentsActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentActionListener {

    // Hospital name is passed via intent.
    private String hospitalName;
    // RecyclerView and adapter for displaying appointments.
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentsList;
    // Button to launch the appointment booking dialog.
    private Button btnMakeAppointment;
    // Departments available at the hospital (loaded from resources).
    private List<String> departmentList = new ArrayList<>();
    // Local SQLite helper for user info.
    private UserDatabase userDatabase;
    // User ID obtained from local database.
    private String userId;
    // Default working hours (adjust as needed)
    private final List<String> defaultHours = Arrays.asList("08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00");

    // Firebase and ChatGPT API parameters.
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    // Replace with your actual ChatGPT API key.
    private final String API_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Retrieve hospital name from intent extras.
        hospitalName = getIntent().getStringExtra("hospitalName");

        // Initialize RecyclerView and its adapter.
        recyclerView = findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentsList = new ArrayList<>();
        // 'this' is passed both as OnAppointmentActionListener and Context.
        adapter = new AppointmentAdapter(appointmentsList, this, this);
        recyclerView.setAdapter(adapter);

        // Initialize the "Make Appointment" button and set its click listener.
        btnMakeAppointment = findViewById(R.id.btnMakeAppointment);
        btnMakeAppointment.setOnClickListener(v -> showMakeAppointmentDialog());

        // Load existing appointments and available departments.
        loadAppointments();
        loadDepartments();

        // Initialize local database and retrieve the user ID.
        userDatabase = new UserDatabase(AppointmentsActivity.this);
        userDatabase.open();
        String[] lastLogin = userDatabase.getLastLogin();
        if (lastLogin != null && lastLogin.length > 0) {
            userId = lastLogin[0];
        } else {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_LONG).show();
            userId = "defaultUser";
        }
    }

    /**
     * Loads appointments from Firebase under "hospitals" node.
     */
    private void loadAppointments() {
        DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
        hospitalsRef.orderByChild("name").equalTo(hospitalName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentsList.clear();
                        // Iterate over hospital nodes matching hospitalName.
                        for (DataSnapshot hospitalSnapshot : snapshot.getChildren()) {
                            // Loop through departments under "airDap".
                            for (DataSnapshot departmentSnapshot : hospitalSnapshot.child("airDap").getChildren()) {
                                // Loop through appointments under "arrAp".
                                for (DataSnapshot appointmentSnapshot : departmentSnapshot.child("arrAp").getChildren()) {
                                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                                    if (appointment != null) {
                                        appointmentsList.add(appointment);
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentsActivity.this, "Error loading appointments: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Loads available departments from a string-array resource.
     */
    private void loadDepartments() {
        String[] categories = getResources().getStringArray(R.array.categories);
        departmentList.addAll(Arrays.asList(categories));
    }

    /**
     * Normalizes a date string ("yyyy-MM-dd HH:mm") to a timestamp at midnight.
     *
     * @param dateString the date string.
     * @return normalized timestamp (in millis) or 0 if error occurs.
     */
    private long normalizeDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dateString));
            // Set time to midnight.
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Shows a dialog for making a new appointment.
     * Uses MaterialDatePicker to select a date and a spinner to select an available hour.
     */
    @SuppressLint("SetTextI18n")
    private void showMakeAppointmentDialog() {
        // Inflate the custom dialog layout.
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_make_appointment, null);

        // Get dialog UI elements.
        final TextView tvSelectDate = dialogView.findViewById(R.id.tvSelectDate);
        final Spinner spinnerTime = dialogView.findViewById(R.id.spinnerTime);
        final EditText etUserId = dialogView.findViewById(R.id.etUserId);
        final Spinner spinnerDepartment = dialogView.findViewById(R.id.spinnerDepartment);

        // Set user ID (non-editable).
        etUserId.setText(userId);
        etUserId.setEnabled(false);

        // Populate the department spinner.
        if (departmentList.isEmpty()) {
            departmentList.add("General");
        }
        ArrayAdapter<String> spinnerDeptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentList);
        spinnerDeptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(spinnerDeptAdapter);

        // Set up the time spinner with the default working hours.
        ArrayAdapter<String> spinnerTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(defaultHours));
        spinnerTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(spinnerTimeAdapter);

        // Build a set of disabled dates based on already booked appointments.
        HashSet<Long> disabledDates = new HashSet<>();
        for (Appointment appointment : appointmentsList) {
            long normalized = normalizeDate(appointment.getDate());
            if (normalized != 0) {
                disabledDates.add(normalized);
            }
        }
        // Create a custom date validator to disable already booked dates.
        CalendarConstraints.DateValidator dateValidator = new DateValidatorUnavailable(disabledDates);
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(dateValidator);
        MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTitleText("Select appointment date");
        datePickerBuilder.setCalendarConstraints(constraintsBuilder.build());
        final MaterialDatePicker<Long> datePicker = datePickerBuilder.build();

        // When the user taps the date TextView, show the date picker.
        tvSelectDate.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        // When a date is selected, update the TextView and available hours.
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selection);
            tvSelectDate.setText(formattedDate);

            // Update available hours based on appointments for the selected day and department.
            String selectedDept = spinnerDepartment.getSelectedItem().toString();
            HashSet<String> bookedHours = new HashSet<>();
            for (Appointment appointment : appointmentsList) {
                if (appointment.getDepartment() != null && appointment.getDepartment().equals(selectedDept)) {
                    if (appointment.getDate() != null && appointment.getDate().startsWith(formattedDate)) {
                        // Extract hour part ("HH:mm") from "yyyy-MM-dd HH:mm".
                        String timePart = appointment.getDate().substring(11);
                        bookedHours.add(timePart);
                    }
                }
            }
            // Build list of hours that are not booked.
            List<String> availableHours = new ArrayList<>();
            for (String hour : defaultHours) {
                if (!bookedHours.contains(hour)) {
                    availableHours.add(hour);
                }
            }
            if (availableHours.isEmpty()) {
                availableHours.add("No hours available");
            }
            // Update the spinner adapter with the available hours.
            ArrayAdapter<String> newTimeAdapter = new ArrayAdapter<>(AppointmentsActivity.this, android.R.layout.simple_spinner_item, availableHours);
            newTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTime.setAdapter(newTimeAdapter);
        });

        // Build the dialog.
        new AlertDialog.Builder(this)
                .setTitle("Make an Appointment")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialogInterface, i) -> {
                    String date = tvSelectDate.getText().toString().trim();
                    String selectedTime = spinnerTime.getSelectedItem().toString().trim();
                    String dept = spinnerDepartment.getSelectedItem().toString();

                    // Validate that a valid date and time have been selected.
                    if (TextUtils.isEmpty(date) || TextUtils.isEmpty(selectedTime)
                            || date.equals("Select appointment date") || selectedTime.equals("No hours available")) {
                        Toast.makeText(AppointmentsActivity.this, "Please select a valid date and time", Toast.LENGTH_SHORT).show();
                    } else {
                        // Combine the selected date and time into one string.
                        String fullDate = date + " " + selectedTime;
                        saveAppointment(fullDate, userId, dept);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Saves a new appointment to Firebase under the hospital's "airDap" node and to the user's node.
     *
     * @param date       the appointment date string ("yyyy-MM-dd HH:mm").
     * @param userId     the user's ID.
     * @param department the department name.
     */
    private void saveAppointment(String date, String userId, String department) {
        Appointment newAppointment = new Appointment();
        newAppointment.setDate(date);
        newAppointment.setUserId(userId);
        newAppointment.setDepartment(department);

        // Reference the hospitals node.
        DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
        hospitalsRef.orderByChild("name").equalTo(hospitalName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean saved = false;
                        // Try to find an existing department.
                        for (DataSnapshot hospitalSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot departmentSnapshot : hospitalSnapshot.child("airDap").getChildren()) {
                                String deptName = departmentSnapshot.child("name").getValue(String.class);
                                if (deptName != null && deptName.equals(department)) {
                                    // Get the count of current appointments for a sequential key.
                                    long appCount = departmentSnapshot.child("arrAp").getChildrenCount();
                                    // Set the appointment ID to the next numeric key.
                                    newAppointment.setId(String.valueOf(appCount));
                                    DatabaseReference arrApRef = departmentSnapshot.getRef().child("arrAp");
                                    arrApRef.child(String.valueOf(appCount)).setValue(newAppointment);
                                    saved = true;
                                    break;
                                }
                            }
                            if (saved) break;
                        }
                        // If no matching department exists, create a new one.
                        if (!saved) {
                            for (DataSnapshot hospitalSnapshot : snapshot.getChildren()) {
                                long deptCount = hospitalSnapshot.child("airDap").getChildrenCount();
                                DatabaseReference newDeptRef = hospitalSnapshot.getRef().child("airDap").child(String.valueOf(deptCount));
                                newDeptRef.child("name").setValue(department);
                                newDeptRef.child("arrAp").child("0").setValue(newAppointment);
                                saved = true;
                                break;
                            }
                        }
                        // If saved successfully in hospital node, also save appointment under user's node.
                        if (saved) {
                            Toast.makeText(AppointmentsActivity.this, "Appointment saved", Toast.LENGTH_SHORT).show();
                            // Save to user's appointments (using push() here)
                            DatabaseReference userAppointmentsRef = FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(userId)
                                    .child("appointments");
                            userAppointmentsRef.push().setValue(newAppointment);
                            // Reload appointments list to update UI.
                            loadAppointments();
                        } else {
                            Toast.makeText(AppointmentsActivity.this, "Error: Could not save appointment", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentsActivity.this, "Error saving appointment: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Methods for AppointmentAdapter callbacks (Edit and Delete)

    @Override
    public void onEdit(Appointment appointment) {
        // For demonstration, open a dialog to edit only the appointment date.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Appointment");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog_edit_appointment, null);
        final EditText etNewDate = dialogView.findViewById(R.id.etNewDate);
        etNewDate.setText(appointment.getDate());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newDate = etNewDate.getText().toString().trim();
                    if (!TextUtils.isEmpty(newDate)) {
                        updateAppointment(appointment, newDate);
                    } else {
                        Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDelete(Appointment appointment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton("Delete", (dialog, which) -> removeAppointment(appointment))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Updates an appointment's date in the user's Firebase node.
     *
     * @param appointment the appointment to update.
     * @param newDate     the new date value.
     */
    private void updateAppointment(Appointment appointment, String newDate) {
        String currentUserId = userDatabase.getLastLogin()[0];
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("appointments")
                .child(appointment.getId())
                .child("date")
                .setValue(newDate, (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(this, "Appointment updated", Toast.LENGTH_SHORT).show();
                        fetchUserAppointments(currentUserId);
                    } else {
                        Toast.makeText(this, "Error updating appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Removes an appointment from the user's Firebase node.
     *
     * @param appointment the appointment to remove.
     */
    private void removeAppointment(Appointment appointment) {
        String currentUserId = userDatabase.getLastLogin()[0];
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("appointments")
                .child(appointment.getId())
                .removeValue((error, ref) -> {
                    if (error == null) {
                        Toast.makeText(this, "Appointment removed", Toast.LENGTH_SHORT).show();
                        fetchUserAppointments(currentUserId);
                    } else {
                        Toast.makeText(this, "Error removing appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchUserAppointments(String userId) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("appointments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointmentsList.clear();
                        for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                            Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                // Save the Firebase key to the appointment for future edits/deletes.
                                appointment.setId(appointmentSnapshot.getKey());
                                appointmentsList.add(appointment);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentsActivity.this, "Error loading appointments: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
