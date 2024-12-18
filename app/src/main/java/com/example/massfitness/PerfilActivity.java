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

import com.example.massfitness.adaptadores.ReservaAdapter;
import com.example.massfitness.entidades.Logro;
import com.example.massfitness.entidades.Reserva;
import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PerfilActivity extends AppCompatActivity {

    private LogroAdapter logroAdapter;
    private LogroAdapter unlockedAdapter, lockedAdapter;
    private List<Logro> logrosList;
    private Button btnEditarPerfil;
    private ImageView ivBack;
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
        rvLockedLogros.setLayoutManager(new LinearLayoutManager(this));
        rvUnlockedLogros.setLayoutManager(new LinearLayoutManager(this));

        logroAdapter = new LogroAdapter(new ArrayList<>());
        rvLockedLogros.setAdapter(logroAdapter);
        rvUnlockedLogros.setAdapter(logroAdapter);

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
                            Log.d("ID USUARIO", ""+idUsuario);
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
            Log.e("AGREGAR LOGRO", String.format("%s %s", resultado, urlLogros));

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

                Log.e("AGREGAR LOGRO", String.format("%s %s %s %s %s", jsonObject.getInt("id_usuario_logro"), jsonObject.getInt("id_logro"), jsonObject.getInt("id_usuario"), fechaObtenidoStr));

                Timestamp fechaObtenido;
                try {
                    fechaObtenido = parseDateTime(fechaObtenidoStr);
                } catch (ParseException e) {
                    showError("Error al analizar la fecha y hora");
                    continue;
                }

                logro.setFechaObtenido(fechaObtenido);

                if (fechaObtenido != null) {
                    unlockedLogros.add(logro);
                    findViewById(R.id.tvLogroFechaObtenido).setVisibility(View.VISIBLE);
                } else {
                    lockedLogros.add(logro);
                }
            }
            updateRecyclerViews(logrosList);
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
            Log.e("AGREGAR LOGRO1", String.format("%s %s", resultado, urlLogros));

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    showError(resultado);
                } else {
                    try {
                        Log.e("AGREGAR LOGRO2", String.format("%s %s", resultado, urlLogros));
                        JSONArray logrosJson = new JSONArray(resultado);
                        if (logrosJson.length() == 0) {
                            showError("No se han encontrado logros");
                            return;
                        }

                        logrosList = new ArrayList<>();
                        for (int i = 0; i < logrosJson.length(); i++) {
                            JSONObject logroJson = logrosJson.getJSONObject(i);
                            Log.e("LOGRO", String.format("Logro %d: %s", i, logroJson.toString()));
                            Logro logro = new Logro(
                                    logroJson.getInt("idLogro"),
                                    logroJson.getString("nombre_logro"),
                                    logroJson.getString("descripcion"),
                                    logroJson.getInt("requisitos_puntos"),
                                    logroJson.getString("recompensa")
                            );
                            logrosList.add(logro);
                        }
                        updateRecyclerViews(logrosList);
                    } catch (JSONException e) {
                        Log.e("ERROR JSON", "Error al parsear JSON", e);
                        showError("Error al obtener los logros");
                    }
                }
            });
        });
    }
    private void obtenerPuntosUsuario(int idUsuario) {
        String url = getResources().getString(R.string.url) + "usuarios/" + idUsuario + "/cantidad_puntos";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(url, new ArrayList<>());
            Log.e("tvLogroStatus", String.format("%s %s", resultado, url));
            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    showError(resultado);
                } else {
                    try {
                        JSONObject puntosJson = new JSONObject(resultado);
                        int puntosUsuario = puntosJson.getInt("cantidad_puntos");
                        TextView tvLogroStatus = findViewById(R.id.tvLogroStatus);
                        tvLogroStatus.setText(String.valueOf(puntosUsuario));
                        for (Logro logro : logrosList) {
                            int progreso = (puntosUsuario * 100) / logro.getRequisitosPuntos();
                            logro.setCantidadPuntos(puntosUsuario);

                            if (progreso >= 100 && logro.getFechaObtenido() == null) {
                                Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
                                logro.setFechaObtenido(fechaActual);
                                guardarFechaObtencionLogro(logro);
                                mostrarNotificacionLogro("¡Logro desbloqueado!", logro.getNombre());
                            } else if (progreso < 100 && logro.getFechaObtenido() != null) {
                                logro.setFechaObtenido(null);
                                eliminarFechaObtencionLogro(logro);
                            }
                        }

                        updateRecyclerViews(logrosList);
                    } catch (JSONException e) {
                        Log.e("tvLogroStatus", String.format("%s", resultado));
                        showError("Error al obtener los puntos");
                    }
                }
            });
        });
    }

    private void guardarFechaObtencionLogro(Logro logro) {
        String url = getResources().getString(R.string.url) + "logros/addLogro/" + idUsuario + "/logro/" + logro.getId_logro();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            List<Parametro> parametros = new ArrayList<>();
            parametros.add(new Parametro("id_usuario", String.valueOf(logro.getId_usuario())));
            parametros.add(new Parametro("id_logro", String.valueOf(logro.getId_logro())));
            parametros.add(new Parametro("fecha_obtenido", logro.getFechaObtenido().toString()));

            String resultado = interopera.postText(url, parametros);
            if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                Log.e("guardarFechaLogro", "Error al guardar logro: " + resultado);
            } else {
                Log.d("guardarFechaLogro", "Logro registrado: " + logro.getNombre());
            }
        });
    }

    private void eliminarFechaObtencionLogro(Logro logro) {
        String url = getResources().getString(R.string.url) + "logros/eliminarLogro/" + idUsuario + "/logro/" + logro.getId_logro();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.deleteTask(url);
            if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                Log.e("eliminarFechaLogro", "Error al eliminar logro: " + resultado);
            } else {
                Log.d("eliminarFechaLogro", "Logro eliminado: " + logro.getNombre());
            }
        });
    }

    private void updateRecyclerViews(List<Logro> logros) {
        List<Logro> unlockedLogros = new ArrayList<>();
        List<Logro> lockedLogros = new ArrayList<>();

        for (Logro logro : logros) {
            if (logro.getFechaObtenido() != null) {
                unlockedLogros.add(logro);  // Si tiene fecha obtenida, es un logro desbloqueado
            } else {
                lockedLogros.add(logro);    // Si no tiene fecha obtenida, es un logro bloqueado
            }
        }

        if (unlockedAdapter == null) {
            unlockedAdapter = new LogroAdapter(unlockedLogros);
            rvUnlockedLogros.setLayoutManager(new LinearLayoutManager(this));
            rvUnlockedLogros.setAdapter(unlockedAdapter);
        } else {
            unlockedAdapter.updateLogros(unlockedLogros);
        }

        if (lockedAdapter == null) {
            lockedAdapter = new LogroAdapter(lockedLogros);
            rvLockedLogros.setLayoutManager(new LinearLayoutManager(this));
            rvLockedLogros.setAdapter(lockedAdapter);
        } else {
            lockedAdapter.updateLogros(lockedLogros);
        }
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
