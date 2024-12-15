package com.example.massfitness.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.R;
import com.example.massfitness.entidades.Logro;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LogroAdapter extends RecyclerView.Adapter<LogroAdapter.ViewHolder> {

    private final List<Logro> logrosList;

    public LogroAdapter(List<Logro> logros) {
        this.logrosList = logros;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<Entry> entries = new ArrayList<>();
        float progressPercentage = (float) Logro.getCantidadPuntos() / Logro.getRequisitosPuntos() * 100;
        entries.add(new Entry(Logro.getId(), progressPercentage));

        LineDataSet dataSet = new LineDataSet(entries, "Progreso de Logros");
        LineData data = new LineData(dataSet);

        Logro logro = logrosList.get(position);
        holder.tvLogroName.setText(logro.getNombre());
        holder.tvLogroStatus.setText(Logro.getRequisitosPuntos());
        holder.lineChart.setData(data);
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
        LineChart lineChart;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLogroName = itemView.findViewById(R.id.tvLogroName);
            tvLogroStatus = itemView.findViewById(R.id.tvLogroName);
            ivLogroIcon = itemView.findViewById(R.id.ivLogroIcon);
            lineChart = itemView.findViewById(R.id.lineChart);
        }
    }
}
