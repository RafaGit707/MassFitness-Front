package com.example.massfitness.adaptadores;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.R;
import com.example.massfitness.entidades.Entrenador;

import java.util.List;

public class EntrenadorAdapter extends RecyclerView.Adapter<EntrenadorAdapter.EntrenadorViewHolder> {

    private List<Entrenador> listaEntrenadores;

    public EntrenadorAdapter(List<Entrenador> listaEntrenadores) {
        this.listaEntrenadores = listaEntrenadores;
    }

    public void updateEntrenadores(List<Entrenador> nuevosEntrenadores) {
        this.listaEntrenadores = nuevosEntrenadores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntrenadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrenador, parent, false);
        return new EntrenadorViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrenadorViewHolder holder, int position) {
        Entrenador entrenador = listaEntrenadores.get(position);
        holder.tvNombre.setText(entrenador.getNombre_entrenador());
        holder.tvEspecializacion.setText("Especialización: " + entrenador.getEspecializacion());
    }

    @Override
    public int getItemCount() {
        return listaEntrenadores.size();
    }

    public class EntrenadorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEspecializacion;

        public EntrenadorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvEntrenadorName);
            tvEspecializacion = itemView.findViewById(R.id.tvEntrenadorSpecialization);
        }
    }
}