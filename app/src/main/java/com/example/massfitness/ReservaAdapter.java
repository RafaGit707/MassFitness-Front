package com.example.massfitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.Reserva;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {
    private List<Reserva> reservaList;

    public ReservaAdapter(List<Reserva> reservaList) {
        this.reservaList = reservaList;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservaList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(reserva.getHorarioReserva());
        holder.fechaTextView.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return reservaList.size();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView fechaTextView;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaTextView = itemView.findViewById(R.id.tvReservaFecha);
        }
    }
}
