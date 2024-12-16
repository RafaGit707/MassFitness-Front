package com.example.massfitness.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.R;
import com.example.massfitness.entidades.Logro;

import java.util.List;

public class LogroAdapter extends RecyclerView.Adapter<LogroAdapter.ViewHolder> {

    private final List<Logro> logrosList;

    public LogroAdapter(List<Logro> logros) {
        this.logrosList = logros;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Logro logro = logrosList.get(position);

        // Calcular el progreso como porcentaje
        float progreso = (float) logro.getCantidadPuntos() / logro.getRequisitosPuntos() * 100;
        int progresoInt = Math.round(progreso); // Aseguramos que es un entero para la ProgressBar

        // Asignar datos a las vistas
        holder.tvLogroName.setText(logro.getNombre());
        holder.tvLogroStatus.setText("Progreso: " + progresoInt + "%");
        holder.progressBar.setProgress(progresoInt);  // Establecer progreso en la barra
        holder.ivLogroIcon.setImageResource(logro.isDesbloqueado() ? R.drawable.ic_logros : R.drawable.ic_logros);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logros, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return logrosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogroName, tvLogroStatus;
        ImageView ivLogroIcon;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLogroName = itemView.findViewById(R.id.tvLogroName);
            tvLogroStatus = itemView.findViewById(R.id.tvLogroStatus);
            ivLogroIcon = itemView.findViewById(R.id.ivLogroIcon);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
