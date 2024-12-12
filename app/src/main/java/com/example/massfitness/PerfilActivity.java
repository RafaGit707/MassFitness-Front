package com.example.massfitness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombreLabel, tvEmailLabel;
    private EditText etNombre, etCorreo, etAntiguaContrasena, etNuevaContrasena, etConfirmarContrasena;
    private Button btnEditarPerfil, btnGuardar, btnSalir, btnBorrarCuenta;
    private ImageView ivBack;
    private SharedPreferences preferences;
    private ExecutorService executorService;
    private Handler handler;
    private int idUsuario;

    private LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        lineChart = findViewById(R.id.lineChart);
        setupLineChart();
        loadChartData();

        ivBack = findViewById(R.id.ivBack);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilActivity.this, EditarPerfilActivity.class));
            }
        });

    }

    private void setupLineChart() {
        // Configuración básica del gráfico
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        // Configuración de ejes
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Intervalos en el eje X

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
    }

    private void loadChartData() {
        // Datos de ejemplo para el progreso de logros
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 20)); // Día 1, 20% progreso
        entries.add(new Entry(2, 40)); // Día 2, 40% progreso
        entries.add(new Entry(3, 60)); // Día 3, 60% progreso
        entries.add(new Entry(4, 80)); // Día 4, 80% progreso
        entries.add(new Entry(5, 100)); // Día 5, 100% progreso

        LineDataSet dataSet = new LineDataSet(entries, "Progreso");
        dataSet.setColor(getResources().getColor(R.color.massfitness));
        dataSet.setCircleColor(getResources().getColor(R.color.massfitness));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refrescar la gráfica
    }

}
