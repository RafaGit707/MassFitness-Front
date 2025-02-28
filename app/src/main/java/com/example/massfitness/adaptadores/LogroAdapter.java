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
        /*notifyItemRangeChanged(0, nuevosLogros.size());*/
/*        logrosList.clear();
        logrosList.addAll(nuevosLogros);
        notifyDataSetChanged();*/
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Logro logro = logrosList.get(position);

        int progresoInt = 0;
        if (logro.getRequisitosPuntos() > 0) {
            float progreso = (float) logro.getCantidadPuntos() / logro.getRequisitosPuntos() * 100;
            progresoInt = Math.round(progreso);
            if (progresoInt > 100) progresoInt = 100;
        }

        holder.tvLogroName.setText(logro.getNombre());
        holder.tvLogroStatus.setText("Progreso: " + progresoInt + "%");
        holder.tvLogroDescription.setText(logro.getDescripcion());
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
        return (logrosList != null) ? logrosList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogroName, tvLogroStatus, tvLogroFechaObtenido, tvLogroDescription;
        ImageView ivLogroIcon;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLogroName = itemView.findViewById(R.id.tvLogroName);
            tvLogroDescription = itemView.findViewById(R.id.tvLogroDescription);
            tvLogroStatus = itemView.findViewById(R.id.tvLogroStatus);
            ivLogroIcon = itemView.findViewById(R.id.ivLogroIcon);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvLogroFechaObtenido = itemView.findViewById(R.id.tvLogroFechaObtenido);
        }
    }
}
