package com.example.hospitals;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitals.adapters.HospitalAdapter;
import com.example.hospitals.data.Hospitals;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * ResultsActivity retrieves and displays a list of hospitals from the Firebase Realtime Database.
 * The hospitals are shown in a RecyclerView using the HospitalAdapter.
 */
public class ResultsActivity extends AppCompatActivity {

    // RecyclerView for displaying hospital list
    private RecyclerView hospitalsRecyclerView;
    // Adapter for binding hospital data to the RecyclerView
    private HospitalAdapter hospitalAdapter;
    // List to hold the hospital data objects
    private ArrayList<Hospitals> hospitalsList;
    // Reference to the Firebase "hospitals" node
    private DatabaseReference hospitalsRef;
    // Log tag for debugging
    private static final String TAG = "ResultsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        hospitalsRef = database.getReference("hospitals");

        // Initialize the RecyclerView and the data list
        hospitalsRecyclerView = findViewById(R.id.hospitals_recycler_view);
        hospitalsList = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(this, hospitalsList);
        hospitalsRecyclerView.setAdapter(hospitalAdapter);

        // Fetch hospital data from Firebase
        fetchHospitalsData();
    }

    /**
     * Retrieves hospital data from Firebase and updates the RecyclerView.
     */
    private void fetchHospitalsData() {
        hospitalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list to avoid duplicates.
                hospitalsList.clear();

                // Iterate over each hospital node in the snapshot.
                for (DataSnapshot hospitalSnapshot : snapshot.getChildren()) {
                    Hospitals hospital = hospitalSnapshot.getValue(Hospitals.class);
                    if (hospital != null) {
                        hospitalsList.add(hospital);
                    }
                }
                // Notify the adapter that the data has changed.
                hospitalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error and display a Toast to inform the user.
                Log.w(TAG, "Failed to read hospitals.", error.toException());
                Toast.makeText(ResultsActivity.this, "Failed to load hospitals.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
