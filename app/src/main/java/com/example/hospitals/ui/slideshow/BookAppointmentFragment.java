package com.example.hospitals.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hospitals.R;
import com.example.hospitals.ResultsActivity;

/**
 * BookAppointmentFragment provides a grid-like list of categories (e.g., Brain, Stomach, Eye, etc.)
 * that the user can choose from. When a category is selected, a Toast is shown and the user is
 * directed to the ResultsActivity with the chosen category type.
 */
public class BookAppointmentFragment extends Fragment {

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

    // Additional categories (Row 3)
    private LinearLayout boneLayout;
    private LinearLayout womenLayout;
    private LinearLayout childLayout;
    private LinearLayout mentalHealthLayout;

    public BookAppointmentFragment() {
        // Required empty public constructor.
    }

    public static BookAppointmentFragment newInstance() {
        return new BookAppointmentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout from XML.
        return inflater.inflate(R.layout.fragment_book_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 1. Find all views.
        initViews(view);
        // 2. Set up click listeners for each category.
        setupListeners();
    }

    /**
     * Initialize all category views by their IDs.
     *
     * @param view the root view of the fragment.
     */
    private void initViews(View view) {
        // Categories (Row 1)
        brainLayout = view.findViewById(R.id.brainLayout);
        stomachLayout = view.findViewById(R.id.stomachLayout);
        eyeLayout = view.findViewById(R.id.eyeLayout);
        heartLayout = view.findViewById(R.id.heartLayout);
        // Categories (Row 2)
        lungsLayout = view.findViewById(R.id.lungsLayout);
        kidneysLayout = view.findViewById(R.id.kidneysLayout);
        liverLayout = view.findViewById(R.id.liverLayout);
        skinLayout = view.findViewById(R.id.skinLayout);
        // Additional Categories (Row 3)
        boneLayout = view.findViewById(R.id.boneLayout);
        womenLayout = view.findViewById(R.id.womenLayout);
        childLayout = view.findViewById(R.id.childLayout);
        mentalHealthLayout = view.findViewById(R.id.mentalHealthLayout);
    }

    /**
     * Set up click listeners for each category layout.
     */
    private void setupListeners() {
        // Row 1 category listeners
        brainLayout.setOnClickListener(v -> {
            showToast("Brain category selected");
            goToResultActivity("brain");
        });
        stomachLayout.setOnClickListener(v -> {
            showToast("Stomach category selected");
            goToResultActivity("stomach");
        });
        eyeLayout.setOnClickListener(v -> {
            showToast("Eye category selected");
            goToResultActivity("eye");
        });
        heartLayout.setOnClickListener(v -> {
            showToast("Heart category selected");
            goToResultActivity("heart");
        });
        // Row 2 category listeners
        lungsLayout.setOnClickListener(v -> {
            showToast("Lungs category selected");
            goToResultActivity("lungs");
        });
        kidneysLayout.setOnClickListener(v -> {
            showToast("Kidneys category selected");
            goToResultActivity("kidneys");
        });
        liverLayout.setOnClickListener(v -> {
            showToast("Liver category selected");
            goToResultActivity("liver");
        });
        skinLayout.setOnClickListener(v -> {
            showToast("Skin category selected");
            goToResultActivity("skin");
        });
        // Row 3 category listeners
        boneLayout.setOnClickListener(v -> {
            showToast("Bone category selected");
            goToResultActivity("bone");
        });
        womenLayout.setOnClickListener(v -> {
            showToast("Women category selected");
            goToResultActivity("women");
        });
        childLayout.setOnClickListener(v -> {
            showToast("Child category selected");
            goToResultActivity("child");
        });
        mentalHealthLayout.setOnClickListener(v -> {
            showToast("Mental Health category selected");
            goToResultActivity("mentalHealth");
        });
    }

    /**
     * Display a short Toast message.
     *
     * @param message the message to display.
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Launch ResultsActivity, passing the selected category type.
     *
     * @param type the category type string.
     */
    private void goToResultActivity(String type) {
        Intent intent = new Intent(getActivity(), ResultsActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
