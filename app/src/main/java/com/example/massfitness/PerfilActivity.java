package com.example.massfitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.adaptadores.LogroAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.massfitness.entidades.Logro;
import com.example.massfitness.util.Internetop;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

public class PerfilActivity extends AppCompatActivity {

    private LineChart lineChart;
    private RecyclerView recyclerViewLogros;
    private LogroAdapter logrosAdapter;
    private List<Logro> listaLogros;
    private Button btnEditarPerfil;
    private ImageView ivBack;
    private SharedPreferences preferences;
    private ExecutorService executorService;
    private Handler handler;
    private int idUsuario;
    private RecyclerView rvUnlockedLogros, rvLockedLogros;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        rvUnlockedLogros = findViewById(R.id.rvUnlockedLogros);
        rvLockedLogros = findViewById(R.id.rvLockedLogros);

        ivBack = findViewById(R.id.ivBack);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);

        setupLineChart();
/*        loadLogros();*/

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

        getUserIdAndReservas();
    }

    private void getUserIdAndReservas() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("correo_electronico", null);

        if (email == null) {
            showError("No hay usuario conectado.");
            return;
        }

        if (isNetworkAvailable()) {
            String url = getResources().getString(R.string.url) + "usuarios/correo/" + email;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Internetop interopera = Internetop.getInstance();
                String resultado = interopera.getText(url, new ArrayList<>());

                handler.post(() -> {
                    if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                        showError(resultado);
                    } else {
                        try {
                            JSONObject usuarioJson = new JSONObject(resultado);
                            idUsuario = usuarioJson.getInt("idUsuario");
                        } catch (Exception e) {
                            showError("Error al conectar con el servidor");
                        }
                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
    }

    private void setupLineChart() {
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
    }

/*
    private void loadLogros() {
        // Supón que obtienes los logros desde la base de datos
        List<Logro> Logros = dbHelper.getUserLogros(idUsuario);

        List<Logro> unlockedLogros = new ArrayList<>();
        List<Logro> lockedLogros = new ArrayList<>();

        // Separar logros desbloqueados y bloqueados
        for (Logro Logro : Logros) {
            if (Logro.isDesbloqueado()) {
                unlockedLogros.add(Logro);
            } else {
                lockedLogros.add(Logro);
            }
        }

        // Actualizar RecyclerView
        LogroAdapter unlockedAdapter = new LogroAdapter(unlockedLogros);
        rvUnlockedLogros.setLayoutManager(new LinearLayoutManager(this));
        rvUnlockedLogros.setAdapter(unlockedAdapter);

        LogroAdapter lockedAdapter = new LogroAdapter(lockedLogros);
        rvLockedLogros.setLayoutManager(new LinearLayoutManager(this));
        rvLockedLogros.setAdapter(lockedAdapter);
    }

    private void loadChartData() {
        // Obtener logros desde la base de datos
        List<Logro> Logros = dbHelper.getUserLogros(idUsuario);

        List<Entry> entries = new ArrayList<>();

        for (Logro Logro : Logros) {
            // Calcular el porcentaje de progreso
            float progressPercentage = (float) Logro.getCantidadPuntos() / Logro.getRequisitosPuntos() * 100;
            entries.add(new Entry(Logro.getId(), progressPercentage));
        }

        // Crear un dataset para el gráfico
        LineDataSet dataSet = new LineDataSet(entries, "Progreso de Logros");
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.invalidate(); // Actualizar el gráfico
    }
*/

    private void mostrarNotificacionLogro(String titulo, String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "logrosChannel")
                .setSmallIcon(R.drawable.ic_logros)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.notify(1, builder.build());
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network nw = connectivityManager.getActiveNetwork();
                if (nw == null) {
                    return false;
                }
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                return (actNw != null) && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
                return nwInfo != null && nwInfo.isConnected();
            }
        }
        return false;
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
