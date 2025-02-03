package com.example.massfitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.adaptadores.LogroAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
    private ImageView ivBack, btnActivarNotificaciones;
    private TextView tvNombre;
    private int idUsuario;
    private RecyclerView rvUnlockedLogros, rvLockedLogros;
    private List<Logro> unlockedLogros = new ArrayList<>();
    private List<Logro> lockedLogros = new ArrayList<>();
    boolean yaDesbloqueado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        rvUnlockedLogros = findViewById(R.id.rvUnlockedLogros);
        rvLockedLogros = findViewById(R.id.rvLockedLogros);
        rvLockedLogros.setLayoutManager(new LinearLayoutManager(this));
        rvUnlockedLogros.setLayoutManager(new LinearLayoutManager(this));

        logroAdapter = new LogroAdapter(new ArrayList<>());
        rvLockedLogros.setAdapter(logroAdapter);
        rvUnlockedLogros.setAdapter(logroAdapter);

        tvNombre = findViewById(R.id.tvNombre);

        ivBack = findViewById(R.id.ivBack);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);

        btnActivarNotificaciones = findViewById(R.id.btnActivarNotificaciones);
        btnActivarNotificaciones.setImageResource(R.drawable.ic_notification_off);
        btnActivarNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnActivarNotificaciones.getTag() != null && btnActivarNotificaciones.getTag().equals("on")) {
                    abrirConfiguracionApp();
                    btnActivarNotificaciones.setImageResource(R.drawable.ic_notification_off);
                    btnActivarNotificaciones.setTag("off");
                    guardarEstadoNotificaciones(false);
                } else {
                    solicitarPermisoNotificaciones();
                    btnActivarNotificaciones.setImageResource(R.drawable.ic_notification_on);
                    btnActivarNotificaciones.setTag("on");
                    guardarEstadoNotificaciones(true);
                }
            }
        });

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
        obtenerLogros();
        getUserIdAndLogros();
    }

    private void getUserIdAndLogros() {
        Log.e("LOGRO", "Entra en getUserIdAndLogros");
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
                            obtenerPuntosUsuario(idUsuario);
                            cargarDatosUsuario();
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
    private void cargarDatosUsuario() {
        Log.e("LOGRO", "Entra en cargarDatosUsuario");
        if (isNetworkAvailable()) {
            String url = getResources().getString(R.string.url) + "usuarios/" + idUsuario;

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
                            if (resultado.trim().equalsIgnoreCase("false")) {
                                showError("El servidor devolvió un error: usuario no encontrado.");
                                return;
                            }

                            if (!resultado.trim().startsWith("{")) {
                                showError("Respuesta inesperada del servidor: " + resultado);
                                return;
                            }

                            JSONObject usuarioJson = new JSONObject(resultado);
                            if (usuarioJson.has("nombre")) {
                                tvNombre.setText(usuarioJson.getString("nombre"));
                            } else {
                                showError("El campo 'nombre' no existe en la respuesta.");
                            }
                        } catch (JSONException e) {
                            Log.e("cargarDatosUsuario", "JSONException: " + e.getMessage());
                            showError("Error al procesar los datos del servidor.");
                        } catch (Exception e) {
                            Log.e("cargarDatosUsuario", "Exception: " + e.getMessage());
                            showError("Ocurrió un error inesperado.");
                        }

                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
    }


    private void fetchLogros(int userId) {
        Log.e("LOGRO", "Entra en fetchLogros");
        String urlLogros = getResources().getString(R.string.url) + "logros/" + userId;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlLogros, new ArrayList<>());
            Log.e("GET LOGRO", String.format("%s %s", resultado, urlLogros));

            handler.post(() -> {
                try {
                    if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                        showError(resultado);
                        Log.e("GET LOGRO2", String.format("%s %s", resultado, urlLogros));
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
        Log.e("LOGRO", "Entra en parseLogros");
        if (jsonArray.length() < 0) {
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

                Log.e("GET LOGRO", String.format("%s %s %s %s", jsonObject.getInt("id_usuario_logro"), jsonObject.getInt("id_logro"), jsonObject.getInt("id_usuario"), fechaObtenidoStr));

                Timestamp fechaObtenido = null;
                try {
                    if (!fechaObtenidoStr.equals("null")) {
                        fechaObtenido = parseDateTime(fechaObtenidoStr);
                    }
                } catch (ParseException e) {
                    showError("Error al analizar la fecha y hora");
                    continue;
                }

                logro.setFechaObtenido(fechaObtenido);

                if (fechaObtenido != null) {
                    yaDesbloqueado = true;
                    unlockedLogros.add(logro);
                    findViewById(R.id.tvLogroFechaObtenido).setVisibility(View.VISIBLE);
                } else {
                    lockedLogros.add(logro);
                }
                logrosList.add(logro);
            }
            updateRecyclerViews(logrosList);
        } catch (Exception e) {
            showError("Error al procesar la respuesta del servidor");
        }
    }
    private Timestamp parseDateTime(String dateTimeStr) throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss.S";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = simpleDateFormat.parse(dateTimeStr);

        SimpleDateFormat localFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        localFormat.setTimeZone(TimeZone.getDefault());
        String localDateTimeStr = localFormat.format(date);

        Date localDate = localFormat.parse(localDateTimeStr);

        TimeZone timeZone = TimeZone.getDefault();
        String zoneID = timeZone.getID();

        Log.d("TimeZone", "Current TimeZone: " + zoneID);

        return new Timestamp(localDate.getTime());
    }
    private void obtenerLogros() {
        Log.e("LOGRO", "Entra en obtenerLogros");
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
        Log.e("LOGRO", "Entra en obtenerPuntosUsuario");
        String url = getResources().getString(R.string.url) + "usuarios/" + idUsuario + "/cantidad_puntos";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(url, new ArrayList<>());
            Log.e("tvLogroStatus1", String.format("%s %s", resultado, url));

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    Log.e("tvLogroStatus2", String.format("%s", resultado));
                    showError(resultado);
                } else {
                    try {
                        int puntosUsuario;
                        if (resultado.startsWith("[")) {
                            JSONObject puntosJson = new JSONObject(resultado);
                            puntosUsuario = puntosJson.getInt("cantidad_puntos");
                        } else {
                            puntosUsuario = Integer.parseInt(resultado);
                        }

                        Log.e("tvLogroStatus3", String.format("%s", puntosUsuario));

                        for (Logro logro : logrosList) {
                            int progreso = (puntosUsuario * 100) / logro.getRequisitosPuntos();
                            logro.setCantidadPuntos(puntosUsuario);
                            Log.e("tvLogroStatus4", String.format("%s", puntosUsuario));

                            if (progreso >= 100 && logro.getFechaObtenido() == null) {
                                Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
                                String fechaActualStr = fechaActual.toString();

                                Timestamp fechaObtenido;
                                try {
                                    fechaObtenido = parseDateTime(fechaActualStr);
                                } catch (ParseException e) {
                                    showError("Error al analizar la fecha y hora");
                                    continue;
                                }

                                if (!yaDesbloqueado) {
                                    logro.setFechaObtenido(fechaObtenido);
                                    guardarFechaObtencionLogro(logro);
                                    mostrarNotificacionLogro("¡Logro desbloqueado!", logro.getNombre());
                                }
                            } else if (progreso < 100 && logro.getFechaObtenido() != null) {
                                logro.setFechaObtenido(null);
                                eliminarFechaObtencionLogro(logro);
                            }
                        }
                        updateRecyclerViews(logrosList);
                    } catch (JSONException | NumberFormatException e) {
                        Log.e("tvLogroStatus5", String.format("%s", resultado));
                        showError("Error al obtener los puntos");
                    }
                }
            });
        });
    }


    private void guardarFechaObtencionLogro(Logro logro) {
        Log.e("LOGRO", "Entra en guardarFechaObtencionLogro");
        if (!yaDesbloqueado) {
            Log.d("Logro Desbloqueado", "Este logro ya está desbloqueado y guardado.");
            return;
        }
        String url = getResources().getString(R.string.url) + "logros/addLogro/" + idUsuario + "/logro/" + logro.getId_logro();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Internetop interopera = Internetop.getInstance();
                List<Parametro> params = new ArrayList<>();
                params.add(new Parametro("usuario_id", idUsuario+""));
                params.add(new Parametro("logro_id", String.valueOf(logro.getId_logro())));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String fechaFormateada = simpleDateFormat.format(logro.getFechaObtenido());
                params.add(new Parametro("fecha_obtenido", fechaFormateada));

                Log.e("guardarFechaObtencionLogro", "usuario_id: " + idUsuario+"");
                Log.e("guardarFechaObtencionLogro", "logro_id: " + logro.getId_logro());
                Log.e("guardarFechaObtencionLogro", "fecha_obtenido: " + fechaFormateada);

                String resultado = interopera.postText(url,params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Integer idCreado = Integer.parseInt(resultado);
                            if (idCreado > 0) {
                                setResult(RESULT_OK);
                                showSuccess("Logro registrado correctamente.");
                                finish();
                            } else {
                                showError("Error al registrar el logro. Por favor, inténtalo de nuevo más tarde.");
                            }
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            Log.e("guardarFechaObtencionLogro", "Respuesta del servidor: " + resultado);

                            showError("Error al registrar el logro.");
                        }
                    }
                });
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
                .setSmallIcon(R.drawable.ic_notification_on)
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
    private void abrirConfiguracionApp() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    private void guardarEstadoNotificaciones(boolean estado) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificaciones_activadas", estado);
        editor.apply();
    }
    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnActivarNotificaciones.setImageResource(R.drawable.ic_notification_on);
                btnActivarNotificaciones.setTag("on");
                Log.d("Permisos", "Permiso para notificaciones concedido.");
            } else {
                Log.d("Permisos", "Permiso para notificaciones denegado.");
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
