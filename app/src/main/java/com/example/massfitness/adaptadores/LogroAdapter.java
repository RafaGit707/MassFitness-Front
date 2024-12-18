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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LogroAdapter extends RecyclerView.Adapter<LogroAdapter.ViewHolder> {

    private List<Logro> logrosList;

    public LogroAdapter(List<Logro> logros) {
        this.logrosList = logros;
    }
    public void updateLogros(List<Logro> nuevosLogros) {
        this.logrosList = nuevosLogros;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Logro logro = logrosList.get(position);

        float progreso = (float) logro.getCantidadPuntos() / logro.getRequisitosPuntos() * 100;
        int progresoInt = Math.round(progreso);


        holder.tvLogroName.setText(logro.getNombre());
        holder.tvLogroStatus.setText("Progreso: " + progresoInt + "%");
        holder.progressBar.setProgress(progresoInt);

        if (logro.getFechaObtenido() != null) {
            SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            localDateFormat.setTimeZone(TimeZone.getDefault());

            String formattedDate = localDateFormat.format(logro.getFechaObtenido());
            holder.tvLogroFechaObtenido.setText("Obtenido: " + formattedDate);
            holder.tvLogroFechaObtenido.setVisibility(View.VISIBLE);
        } else {
            holder.tvLogroFechaObtenido.setVisibility(View.GONE);
        }

        holder.ivLogroIcon.setImageResource(logro.isDesbloqueado() ? R.drawable.ic_boxeo : R.drawable.ic_logros);
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
        TextView tvLogroName, tvLogroStatus,tvLogroFechaObtenido;
        ImageView ivLogroIcon;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLogroName = itemView.findViewById(R.id.tvLogroName);
            tvLogroStatus = itemView.findViewById(R.id.tvLogroStatus);
            ivLogroIcon = itemView.findViewById(R.id.ivLogroIcon);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvLogroFechaObtenido = itemView.findViewById(R.id.tvLogroFechaObtenido);
        }
    }
}
