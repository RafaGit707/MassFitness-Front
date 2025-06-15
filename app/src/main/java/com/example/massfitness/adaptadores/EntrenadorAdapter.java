package com.example.massfitness.adaptadores;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.massfitness.R;
import com.example.massfitness.entidades.Entrenador;

import java.util.List;
import java.util.Locale;

public class EntrenadorAdapter extends RecyclerView.Adapter<EntrenadorAdapter.EntrenadorViewHolder> {

    private List<Entrenador> listaEntrenadores;
    private OnEntrenadorActionClickListener actionListener;
    private Context context;
/*    private java.util.Map<String, Integer> trainerImageMap;*/

    public interface OnEntrenadorActionClickListener {
        void onEditClick(Entrenador entrenador, int position);
        void onDeleteClick(Entrenador entrenador, int position);
    }

    public EntrenadorAdapter(Context context, List<Entrenador> listaEntrenadores, OnEntrenadorActionClickListener listener) {
        this.context = context;
        this.listaEntrenadores = listaEntrenadores;
        this.actionListener = listener;
        /*initializeTrainerImageMap();*/
    }

    public void updateEntrenadores(List<Entrenador> nuevosEntrenadores) {
        this.listaEntrenadores.clear();
        this.listaEntrenadores.addAll(nuevosEntrenadores);
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
        holder.tvEspecializacion.setText(entrenador.getEspecializacion());

        /*holder.ivEntrenadorImage.setImageResource(getTrainerImageResource(entrenador.getNombre_entrenador()));*/

        Glide.with(context)
                .load(getTrainerImageResource(entrenador.getNombre_entrenador())) // Carga el ID del recurso
                .placeholder(R.drawable.ic_equipo) // Opcional: crea un drawable para esto
                .error(R.drawable.ic_equipo) // La imagen si algo falla
                .into(holder.ivEntrenadorImage); // El ImageView de destino

        holder.btnEditar.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(entrenador, holder.getAdapterPosition());
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(entrenador, holder.getAdapterPosition());
            }
        });
    }

    private int getTrainerImageResource(String trainerName) {
        if (trainerName == null || trainerName.isEmpty()) {
            return R.drawable.ic_equipo;
        }

        String normalizedName = trainerName.trim().toLowerCase();

        switch (normalizedName) {
            case "laura":
                return R.drawable.laura_image;
            case "maikel":
                return R.drawable.maikel_image;
            case "john":
                return R.drawable.john_image;
            case "jose":
                return R.drawable.jose_image;
            default:
                return R.drawable.ic_equipo;
        }
    }

/*    private void initializeTrainerImageMap() {
        trainerImageMap = new java.util.HashMap<>();
        trainerImageMap.put("laura", R.drawable.laura_image);
        trainerImageMap.put("maikel", R.drawable.maikel_image);
        trainerImageMap.put("john", R.drawable.john_image);
        trainerImageMap.put("jose", R.drawable.jose_image);
    }

    private int getTrainerImageResource(String trainerName) {
        if (trainerName == null || trainerName.isEmpty()) {
            return R.drawable.ic_equipo; // Imagen por defecto
        }

        String normalizedName = trainerName.trim().toLowerCase();

        // Busca el nombre en el Map. Si no lo encuentra, devuelve la imagen por defecto.
        Integer resourceId = trainerImageMap.get(normalizedName);

        return resourceId != null ? resourceId : R.drawable.ic_equipo;
    }*/

    @Override
    public int getItemCount() {
        return listaEntrenadores != null ? listaEntrenadores.size() : 0;
    }

    public class EntrenadorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEspecializacion;
        ImageView ivEntrenadorImage, btnEditar, btnEliminar;

        public EntrenadorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvEntrenadorName);
            tvEspecializacion = itemView.findViewById(R.id.tvEntrenadorSpecialization);
            ivEntrenadorImage = itemView.findViewById(R.id.ivEntrenadorImage);
            btnEditar = itemView.findViewById(R.id.btnEditarEntrenador);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEntrenador);
        }
    }
}