package com.example.massfitness.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.R;
import com.example.massfitness.entidades.Clase;

import java.util.List;

public class ClaseAdapter extends RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder> {

    private List<Clase> listaClases;
    private OnClaseActionClickListener actionListener;

    public interface OnClaseActionClickListener {
        void onEditClick(Clase clase, int position);
        void onDeleteClick(Clase clase, int position);
        // Opcional: si también necesitas un clic general en el ítem
        // void onItemClick(Clase clase, int position);
    }
    // Constructor principal que ACEPTA el listener
    public ClaseAdapter(List<Clase> listaClases, OnClaseActionClickListener listener) {
        this.listaClases = listaClases;
        this.actionListener = listener; // Asigna el listener recibido
    }

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
        if (clase.getEntrenador() != null) {
            holder.tvNombreEntrenador.setText("Entrenador: " + clase.getEntrenador().getNombre_entrenador());
        } else {
            holder.tvNombreEntrenador.setText("Entrenador: No asignado");
        }
        holder.tvCapacidad.setText(String.valueOf(clase.getCapacidad_maxima()) + " Personas");

        if (actionListener != null) {
            holder.btnEditarClase.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition(); // Obtener posición actual
                if (currentPosition != RecyclerView.NO_POSITION) { // Verificar posición válida
                    actionListener.onEditClick(listaClases.get(currentPosition), currentPosition);
                }
            });

            holder.btnEliminarClase.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition(); // Obtener posición actual
                if (currentPosition != RecyclerView.NO_POSITION) { // Verificar posición válida
                    actionListener.onDeleteClick(listaClases.get(currentPosition), currentPosition);
                }
            });

            // Opcional: Si quieres un clic general en el item
            /*
            holder.itemView.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    actionListener.onItemClick(listaClases.get(currentPosition), currentPosition);
                }
            });
            */

        }

    }
    public Clase getClase(int position) {
        if (position >= 0 && position < listaClases.size()) {
            return listaClases.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return listaClases.size();
    }

    public class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreClase, tvNombreDescripcion, tvNombreEntrenador, tvCapacidad;
        ImageView btnEliminarClase, btnEditarClase;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreClase = itemView.findViewById(R.id.tvNombreClase);
            tvNombreDescripcion = itemView.findViewById(R.id.tvNombreDescripcion);
            tvNombreEntrenador = itemView.findViewById(R.id.tvNombreEntrenador);
            tvCapacidad = itemView.findViewById(R.id.tvCapacidad);
            btnEliminarClase = itemView.findViewById(R.id.btnEliminarClase);
            btnEditarClase = itemView.findViewById(R.id.btnEditarClase);
        }
    }
}
