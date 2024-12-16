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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.adaptadores.LogroAdapter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.massfitness.entidades.Logro;
import com.example.massfitness.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PerfilActivity extends AppCompatActivity {

    private LogroAdapter logroAdapter;
    private List<Logro> logrosList;
    private Button btnEditarPerfil;
    private ImageView ivBack;
    private SharedPreferences preferences;
    private ExecutorService executorService;
    private Handler handler;
    private int idUsuario;
    private RecyclerView rvUnlockedLogros, rvLockedLogros;
    private List<Logro> unlockedLogros = new ArrayList<>();
    private List<Logro> lockedLogros = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        rvUnlockedLogros = findViewById(R.id.rvUnlockedLogros);
        rvLockedLogros = findViewById(R.id.rvLockedLogros);

        ivBack = findViewById(R.id.ivBack);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

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

        getUserIdAndLogros();
    }

    private void getUserIdAndLogros() {
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
                            JSONObject logrosJson = new JSONObject(resultado);
                            idUsuario = logrosJson.getInt("idUsuario");
                            fetchLogros(idUsuario);
                            obtenerLogros();
                            obtenerPuntosUsuario(idUsuario);
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

    private void fetchLogros(int userId) {
        String urlLogros = getResources().getString(R.string.url) + "logros/" + userId;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlLogros, new ArrayList<>());

            handler.post(() -> {
                try {
                    if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                        showError(resultado);
                    } else {
                        JSONArray jsonArray = new JSONArray(resultado);
                        parseLogros(jsonArray);
                    }
                } catch (Exception e) {
                    showError("Error al procesar los logros");
                }
            });
        });
    }
    private void parseLogros(JSONArray jsonArray) {
        if (jsonArray.length() == 0) {
            showError("No hay logros para este usuario.");
            return;
        }
        unlockedLogros.clear();
        lockedLogros.clear();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Logro logro = new Logro();
                logro.setId_usuario_logro(jsonObject.getInt("id_usuario_logro"));
                logro.setId_logro(jsonObject.getInt("id_logro"));
                logro.setId_usuario(jsonObject.getInt("id_usuario"));
                String fechaObtenidoStr = jsonObject.getString("fecha_obtenido");
                logrosList.add(logro);

                Timestamp fechaObtenido;
                try {
                    fechaObtenido = parseDateTime(fechaObtenidoStr);
                } catch (ParseException e) {
                    showError("Error al analizar la fecha y hora");
                    continue;
                }

                logro.setFechaObtenido(fechaObtenido);
                logrosList.add(logro);

                if (fechaObtenido != null) {
                    unlockedLogros.add(logro);  // Add unlocked logros
                } else {
                    lockedLogros.add(logro);  // Add locked logros
                }
            }
            updateRecyclerViews();
        } catch (Exception e) {
            showError("Error al procesar la respuesta del servidor");
        }
    }
    private Timestamp parseDateTime(String dateTimeStr) throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss.S";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = simpleDateFormat.parse(dateTimeStr);

        SimpleDateFormat localFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        localFormat.setTimeZone(TimeZone.getDefault());
        String localDateTimeStr = localFormat.format(date);

        Date localDate = localFormat.parse(localDateTimeStr);

        // Obtener la zona horaria actual
        TimeZone timeZone = TimeZone.getDefault();
        String zoneID = timeZone.getID();

        // Verificar si la zona horaria es la correcta
        Log.d("TimeZone", "Current TimeZone: " + zoneID);

        return new Timestamp(localDate.getTime());
    }
    private void obtenerLogros() {
        String urlLogros = getResources().getString(R.string.url) + "logros";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlLogros, new ArrayList<>());
            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    showError(resultado);
                } else {
                    try {
                        JSONArray logrosJson = new JSONArray(resultado);
                        List<Logro> logros = new ArrayList<>();
                        for (int i = 0; i < logrosJson.length(); i++) {
                            JSONObject logroJson = logrosJson.getJSONObject(i);
                            Logro logro = new Logro(
                                    logroJson.getInt("idLogro"),
                                    logroJson.getString("nombre_logro"),
                                    logroJson.getString("descripcion"),
                                    logroJson.getInt("requisitos_puntos"),
                                    logroJson.getString("recompensa")
                            );
                            logro.setNombre(logroJson.getString("nombre_logro"));
                            logro.setDescripcion(logroJson.getString("descripcion"));
                            logro.setRequisitosPuntos(logroJson.getInt("requisitos_puntos"));
                            logros.add(logro);
                        }
                        updateRecyclerViews();
                    } catch (JSONException e) {
                        showError("Error al obtener los logros");
                    }
                }
            });
        });
    }
    private void obtenerPuntosUsuario(int idUsuario) {
        String url = getResources().getString(R.string.url) + idUsuario + "/cantidad-puntos";
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
                        JSONObject puntosJson = new JSONObject(resultado);
                        int puntosUsuario = puntosJson.getInt("cantidad_puntos");
                        for (Logro logro : logrosList) {
                            int puntosRequeridos = logro.getRequisitosPuntos();
                            int progreso = (puntosUsuario * 100) / puntosRequeridos;
                        }
                    } catch (JSONException e) {
                        showError("Error al obtener los puntos");
                    }
                }
            });
        });
    }

    private void updateRecyclerViews() {
        LogroAdapter unlockedAdapter = new LogroAdapter(unlockedLogros);
        rvUnlockedLogros.setLayoutManager(new LinearLayoutManager(this));
        rvUnlockedLogros.setAdapter(unlockedAdapter);

        LogroAdapter lockedAdapter = new LogroAdapter(lockedLogros);
        rvLockedLogros.setLayoutManager(new LinearLayoutManager(this));
        rvLockedLogros.setAdapter(lockedAdapter);

        unlockedAdapter.notifyDataSetChanged();
        lockedAdapter.notifyDataSetChanged();
    }

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
