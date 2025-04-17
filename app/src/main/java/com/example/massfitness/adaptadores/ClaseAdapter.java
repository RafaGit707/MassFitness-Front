package com.example.massfitness.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.R;
import com.example.massfitness.entidades.Clase;

import java.util.List;

public class ClaseAdapter extends RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder> {

    private List<Clase> listaClases;

    public ClaseAdapter(List<Clase> listaClases) {
        this.listaClases = listaClases;
    }
    public void updateClases(List<Clase> nuevosClases) {
        this.listaClases = nuevosClases;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Clase clase = listaClases.get(position);
        holder.tvNombreClase.setText(String.valueOf(clase.getNombre()));
        holder.tvNombreDescripcion.setText("Capacidad: " + String.valueOf(clase.getCapacidad_maxima()));
        holder.tvNombreEntrenador.setText("Entrenador: " + String.valueOf(clase.getEntrenador().getNombre_entrenador()));
        holder.tvCapacidad.setText(String.valueOf(clase.getCapacidad_maxima()) + " Personas");
    }

    @Override
    public int getItemCount() {
        return listaClases.size();
    }

    public class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreClase, tvNombreDescripcion, tvNombreEntrenador, tvCapacidad;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreClase = itemView.findViewById(R.id.tvNombreClase);
            tvNombreDescripcion = itemView.findViewById(R.id.tvNombreDescripcion);
            tvNombreEntrenador = itemView.findViewById(R.id.tvNombreEntrenador);
            tvCapacidad = itemView.findViewById(R.id.tvCapacidad);
        }
    }
}
