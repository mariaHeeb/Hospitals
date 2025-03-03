package com.example.hospitals.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Import Glide for image loading
import com.bumptech.glide.Glide;
import com.example.hospitals.AppointmentsActivity;
import com.example.hospitals.R;
import com.example.hospitals.data.Hospitals;

import java.util.ArrayList;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private Context mContext;
    private ArrayList<Hospitals> hospitalsList;

    public HospitalAdapter(Context mContext, ArrayList<Hospitals> hospitalsList) {
        this.mContext = mContext;
        this.hospitalsList = hospitalsList;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.hospital_item, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospitals hospital = hospitalsList.get(position);
        holder.titleTextView.setText(hospital.getName());
        holder.subtitleTextView.setText(hospital.getSubtitle());
        holder.addressTextView.setText("Address: " + hospital.getAddress());

        // Load image using Glide
        Glide.with(mContext)
                .load(hospital.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.hospitalImageView);

        // Handle button click
        holder.learnMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AppointmentsActivity.class);
                intent.putExtra("hospitalName", hospital.getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitalsList.size();
    }

    public class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, subtitleTextView, addressTextView;
        ImageView hospitalImageView;
        Button learnMoreButton;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.hospital_title);
            subtitleTextView = itemView.findViewById(R.id.hospital_subtitle);
            addressTextView = itemView.findViewById(R.id.hospital_description);
            hospitalImageView = itemView.findViewById(R.id.hospital_image);
            learnMoreButton = itemView.findViewById(R.id.learn_more_button);
        }
    }
}
