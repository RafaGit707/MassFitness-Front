package com.example.massfitness.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.R;
import com.example.massfitness.entidades.Reserva;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {
    private List<Reserva> reservaList;

    public ReservaAdapter(List<Reserva> reservaList) {
        this.reservaList = reservaList;
    }
    public void setReservas(List<Reserva> reservas) {
        this.reservaList = reservas;
        notifyDataSetChanged();
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

        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        localDateFormat.setTimeZone(TimeZone.getDefault());

        String formattedDate = localDateFormat.format(reserva.getHorarioReserva());

        holder.fechaTextView.setText(formattedDate);
        holder.tipoTextView.setText(reserva.getTipoReserva());
        holder.estadoTextView.setText(reserva.getEstadoReserva());
    }

    @Override
    public int getItemCount() {
        return reservaList.size();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView fechaTextView;
        TextView tipoTextView;
        TextView estadoTextView;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaTextView = itemView.findViewById(R.id.tvReservaFecha);
            tipoTextView = itemView.findViewById(R.id.tvTipoReserva);
            estadoTextView = itemView.findViewById(R.id.tvEstadoReserva);
        }
    }
}
