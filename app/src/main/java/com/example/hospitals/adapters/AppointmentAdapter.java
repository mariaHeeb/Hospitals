package com.example.hospitals.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospitals.AppointmentsActivity;
import com.example.hospitals.R;
import com.example.hospitals.data.Appointment;
import com.example.hospitals.db.UserDatabase;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private String userId;
    private Context mcontext;

    public interface OnAppointmentActionListener {
        void onEdit(Appointment appointment);
        void onDelete(Appointment appointment);
    }

    private List<Appointment> appointmentList;
    private OnAppointmentActionListener actionListener;

    public AppointmentAdapter(List<Appointment> appointmentList, OnAppointmentActionListener listener,Context mcontext) {
        this.appointmentList = appointmentList;
        this.actionListener = listener;
        this.mcontext= mcontext;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.dateTextView.setText("Date: " + appointment.getDate());
        holder.departmentTextView.setText("Department: " + appointment.getDepartment());

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(appointment);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(appointment);
            }
        });
        UserDatabase userDatabase = new UserDatabase(mcontext);
        userDatabase.open();
        // Fetch the user ID from SQLite
        String[] lastLogin = userDatabase.getLastLogin();
        userId = lastLogin[0];
        if(!appointment.getUserId().equals(userId)){
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, departmentTextView;
        Button btnEdit, btnDelete;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            departmentTextView = itemView.findViewById(R.id.textViewDepartment);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
