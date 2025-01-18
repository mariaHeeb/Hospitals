package com.example.hospitals.ui.slideshow;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hospitals.R;


public class BookAppointmentFragment extends Fragment {

    // Search bar
    private EditText searchEditText;

    // Category Layouts (Row 1)
    private LinearLayout brainLayout;
    private LinearLayout stomachLayout;
    private LinearLayout eyeLayout;
    private LinearLayout heartLayout;

    // Category Layouts (Row 2)
    private LinearLayout lungsLayout;
    private LinearLayout kidneysLayout;
    private LinearLayout liverLayout;
    private LinearLayout skinLayout;

    // Doctor Buttons
    private Button doctorOneButton;
    private Button doctorTwoButton;

    public BookAppointmentFragment() {
        // Required empty public constructor
    }

    public static BookAppointmentFragment newInstance() {
        return new BookAppointmentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Find all views
        initViews(view);

        // 2. Set up listeners
        setupListeners();
    }

    /**
     * Find all views by ID
     */
    private void initViews(View view) {
//        searchEditText = view.findViewById(R.id.searchEditText);
//
//        // Categories (Row 1)
//        brainLayout = view.findViewById(R.id.brainLayout);
//        stomachLayout = view.findViewById(R.id.stomachLayout);
//        eyeLayout = view.findViewById(R.id.eyeLayout);
//        heartLayout = view.findViewById(R.id.heartLayout);
//
//        // Categories (Row 2)
//        lungsLayout = view.findViewById(R.id.lungsLayout);
//        kidneysLayout = view.findViewById(R.id.kidneysLayout);
//        liverLayout = view.findViewById(R.id.liverLayout);
//        skinLayout = view.findViewById(R.id.skinLayout);
//
//        // Doctor Buttons
//        doctorOneButton = view.findViewById(R.id.doctorOneButton);
//        doctorTwoButton = view.findViewById(R.id.doctorTwoButton);
    }

    /**
     * Set up click listeners for categories and doctors
     */
    private void setupListeners() {
        // Category Clicks
        brainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Brain category selected", Toast.LENGTH_SHORT).show();
            }
        });

        stomachLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Stomach category selected", Toast.LENGTH_SHORT).show();
            }
        });

        eyeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Eye category selected", Toast.LENGTH_SHORT).show();
            }
        });

        heartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Heart category selected", Toast.LENGTH_SHORT).show();
            }
        });

        lungsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Lungs category selected", Toast.LENGTH_SHORT).show();
            }
        });

        kidneysLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Kidneys category selected", Toast.LENGTH_SHORT).show();
            }
        });

        liverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Liver category selected", Toast.LENGTH_SHORT).show();
            }
        });

        skinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Skin category selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Doctor Buttons
        doctorOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For example, you might launch an appointment screen
                Toast.makeText(getContext(), "Dr. Daryl Nehls is selected!", Toast.LENGTH_SHORT).show();
            }
        });

        doctorTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Dr. Sophia Patel is selected!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
