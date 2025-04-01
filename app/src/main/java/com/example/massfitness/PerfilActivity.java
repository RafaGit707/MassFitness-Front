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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massfitness.adaptadores.LogroAdapter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.massfitness.entidades.Logro;
import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PerfilActivity extends AppCompatActivity {

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

        /*Crear adaptadores separados*/
        unlockedAdapter = new LogroAdapter(unlockedLogros);
        lockedAdapter = new LogroAdapter(lockedLogros);
        rvUnlockedLogros.setAdapter(unlockedAdapter);
        rvLockedLogros.setAdapter(lockedAdapter);

/*        logroAdapter = new LogroAdapter(new ArrayList<>());
        rvLockedLogros.setAdapter(logroAdapter);
        rvUnlockedLogros.setAdapter(logroAdapter);*/

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
        getUserIdAndLogros();
    }
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
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

                            cargarDatosUsuario();
                            // Primero obtenemos los logros disponibles
                            obtenerLogros(new Callback<List<Logro>>() {

                                @Override
                                public void onSuccess(List<Logro> logrosDisponibles) {
                                    obtenerPuntosUsuario(idUsuario);

                                    // Luego obtenemos los logros desbloqueados
                                    fetchLogros(new Callback<List<Logro>>() {
                                        @Override
                                        public void onSuccess(List<Logro> logrosDesbloqueados) {
                                            List<Logro> logrosFinales = combinarLogros(logrosDisponibles, logrosDesbloqueados);
                                            updateRecyclerViews(logrosFinales);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("LOGRO", "Error al obtener logros desbloqueados", e);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("LOGRO", "Error al obtener logros disponibles", e);
                                }
                            });
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
    private void fetchLogros(Callback<List<Logro>> callback) {
        Log.e("LOGRO", "Entra en fetchLogros");

        String urlLogros = getResources().getString(R.string.url) + "logros/" + idUsuario;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlLogros, new ArrayList<>());
            Log.e("GET LOGRO", String.format("%s %s", resultado, urlLogros));

            handler.post(() -> {
                if (resultado == null || resultado.trim().isEmpty()) {
                    callback.onFailure(new Exception("La respuesta del servidor está vacía."));
                    return;
                }

                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    callback.onFailure(new Exception(resultado));
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(resultado);
                    List<Logro> logrosDesbloqueados = parseLogrosDesbloqueados(jsonArray);
                    callback.onSuccess(logrosDesbloqueados);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            });
        });
    }

    private void obtenerLogros(Callback<List<Logro>> callback) {
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
                    callback.onFailure(new Exception(resultado));
                } else {
                    try {
                        Log.e("AGREGAR LOGRO2", String.format("%s %s", resultado, urlLogros));
                        JSONArray logrosJson = new JSONArray(resultado);
                        List<Logro> logrosDisponibles = parseLogrosDisponibles(logrosJson);
                        callback.onSuccess(logrosDisponibles);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                }
            });
        });
    }
    private List<Logro> parseLogrosDisponibles(JSONArray jsonArray) throws JSONException {
        logrosList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Logro logro = new Logro();
            logro.setId_logro(jsonObject.optInt("idLogro", -1));
            logro.setNombre(jsonObject.optString("nombre_logro", "Desconocido"));
            logro.setDescripcion(jsonObject.optString("descripcion"));
            logro.setRequisitosPuntos(jsonObject.optInt("requisitos_puntos"));
            logro.setRecompensa(jsonObject.optString("recompensa"));
            logrosList.add(logro);
        }
        return logrosList;
    }

    private List<Logro> parseLogrosDesbloqueados(JSONArray jsonArray) throws JSONException {
        logrosList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Logro logro = new Logro();
            logro.setId_usuario_logro(jsonObject.optInt("id_usuario_logro", -1));
            logro.setId_logro(jsonObject.optInt("logro_id", -1));
            logro.setId_usuario(jsonObject.optInt("usuario_id", -1));
            String fechaObtenidoStr = jsonObject.optString("fecha_obtenido", "null");

            Timestamp fechaObtenido = null;
            if (fechaObtenidoStr != null && !fechaObtenidoStr.equals("null") && !fechaObtenidoStr.isEmpty()) {
                try {
                    fechaObtenido = parseDateTime(fechaObtenidoStr);
                } catch (ParseException e) {
                    Log.e("ERROR", "Error al analizar la fecha y hora: " + e.getMessage());
                    continue;  // Salta este logro si hay error en la fecha
                }
            }
            logro.setFechaObtenido(fechaObtenido);
            logrosList.add(logro);

            if (logro.getFechaObtenido() != null) {
                yaDesbloqueado = true;
            }
            Log.e("Logro Desbloqueado?", yaDesbloqueado+"");
        }

        return logrosList;
    }
    private List<Logro> combinarLogros(List<Logro> logrosDisponibles, List<Logro> logrosDesbloqueados) {
        if (logrosDisponibles == null || logrosDisponibles.isEmpty()) {
            Log.e("ERROR", "Lista de logros disponibles está vacía o nula.");
            return new ArrayList<>(); // Evita null
        }

        Map<Integer, Logro> logrosMap = new HashMap<>();

        // Agregar todos los logros disponibles al mapa
        for (Logro logro : logrosDisponibles) {
            logrosMap.put(logro.getId_logro(), logro);
        }

        // Marcar como desbloqueados los logros obtenidos por el usuario
        if (logrosDesbloqueados != null) {
            for (Logro logroDesbloqueado : logrosDesbloqueados) {
                if (logrosMap.containsKey(logroDesbloqueado.getId_logro())) {
                    logrosMap.get(logroDesbloqueado.getId_logro()).setFechaObtenido(logroDesbloqueado.getFechaObtenido());
                }
            }
        }
        return new ArrayList<>(logrosMap.values());
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

    private void obtenerPuntosUsuario(int idUsuario) {
        Log.e("LOGRO", "Entra en obtenerPuntosUsuario");
        if (logrosList == null || logrosList.isEmpty()) {
            Log.e("ERROR", "logrosList sigue vacía antes de obtener puntos.");
        }

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
                        if (logrosList == null || logrosList.isEmpty()) {
                            Log.e("ERROR", "logrosList sigue vacía al entrar en el try catch de obtenerpuntos.");
                        }

                        int puntosUsuario;
                        if (resultado.startsWith("[")) {
                            JSONObject puntosJson = new JSONObject(resultado);
                            puntosUsuario = puntosJson.getInt("cantidad_puntos");
                        } else {
                            puntosUsuario = Integer.parseInt(resultado);
                        }
                        Log.e("tvLogroStatus3", String.format("%s", puntosUsuario));

                        if (logrosList != null) {
                            AtomicInteger pendientes = new AtomicInteger(logrosList.size());
                            for (Logro logro : logrosList) {
                                int requisitosPuntos = logro.getRequisitosPuntos();
                                int progreso;
                                if (requisitosPuntos > 0) {
                                    progreso = (puntosUsuario * 100) / requisitosPuntos;
                                } else {
                                    progreso = 0;
                                }
                                Log.e("Progreso Calculado", "Puntos: " + puntosUsuario + ", Requisitos: " + requisitosPuntos + ", Progreso: " + progreso);

                                logro.setCantidadPuntos(puntosUsuario);
                                Log.e("tvLogroStatus4", String.format("%s", puntosUsuario));
                                isLogroAlreadySaved(idUsuario, logro.getId_logro(), new Callback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean yaDesbloqueado) {
                                        if (puntosUsuario >= logro.getRequisitosPuntos() && progreso >= 100 && !yaDesbloqueado) {
                                            Timestamp fechaActual = new Timestamp(System.currentTimeMillis());

                                            Log.e("tvLogroStatus5", "Desbloqueando logro: " + logro.getNombre());

                                            logro.setFechaObtenido(fechaActual);
                                            guardarFechaObtencionLogro(logro);
                                            mostrarNotificacionLogro("¡Logro desbloqueado!", logro.getNombre());
                                        } else if (puntosUsuario < logro.getRequisitosPuntos() && progreso < 100 && logro.getFechaObtenido() != null && yaDesbloqueado) {
                                            logro.setFechaObtenido(null);
                                            eliminarFechaObtencionLogro(logro);
                                        }

                                        if (pendientes.decrementAndGet() == 0) {
                                            Log.e("RecyclerView Update", "Actualizando RecyclerView después de validar todos los logros");
                                            lockedAdapter.notifyDataSetChanged();
                                            unlockedAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("obtenerPuntosUsuario", "Error al verificar si el logro ya está guardado: " + e.getMessage());
                                        showError("Error al verificar el logro.");
                                    }
                                });
                            }
                        } else {
                            Log.e("ERROR", "La lista de logros está vacía o nula.");
                        }
                    } catch (JSONException | NumberFormatException e) {
                        Log.e("tvLogroStatus5", String.format("%s", resultado));
                        showError("Error al obtener los puntos");
                    }
                }
                lockedAdapter.notifyDataSetChanged();
                unlockedAdapter.notifyDataSetChanged();
            });
        });
    }

    private void guardarFechaObtencionLogro(Logro logro) {
        Log.e("LOGRO", "Entra en guardarFechaObtencionLogro");

        isLogroAlreadySaved(idUsuario, logro.getId_logro(), new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean yaDesbloqueado) {
                if (yaDesbloqueado) {
                    Log.d("Logro Desbloqueado", "Este logro ya está desbloqueado y guardado.");
                    return;
                }

                String url = getResources().getString(R.string.url) + "logros/addLogro/" + idUsuario + "/logro/" + logro.getId_logro();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    try {
                        Internetop interopera = Internetop.getInstance();
                        List<Parametro> params = new ArrayList<>();
                        params.add(new Parametro("usuario_id", idUsuario + ""));
                        params.add(new Parametro("logro_id", String.valueOf(logro.getId_logro())));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String fechaFormateada = simpleDateFormat.format(logro.getFechaObtenido());
                        params.add(new Parametro("fecha_obtenido", fechaFormateada));

                        Log.e("guardarFechaObtencionLogro", "usuario_id: " + idUsuario);
                        Log.e("guardarFechaObtencionLogro", "logro_id: " + logro.getId_logro());
                        Log.e("guardarFechaObtencionLogro", "fecha_obtenido: " + fechaFormateada);

                        String resultado = interopera.postText(url, params);

                        handler.post(() -> {
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
                                Log.e("guardarFechaObtencionLogro", "Respuesta del servidor: " + resultado);
                                showError("Error al registrar el logro.");
                            }
                        });
                    } catch (Exception e) {
                        handler.post(() -> showError("Error al registrar el logro: " + e.getMessage()));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("guardarFechaObtencionLogro", "Error al verificar si el logro ya está guardado: " + e.getMessage());
                showError("Error al verificar el logro.");
            }
        });
    }

    private void isLogroAlreadySaved(int idUsuario, int logroId, Callback<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = getResources().getString(R.string.url) + "logros/usuario/" + idUsuario + "/logro/" + logroId;
                Internetop interopera = Internetop.getInstance();
                String resultado = interopera.getText(url, new ArrayList<>());

                Log.d("LOGRO", "Respuesta del servidor: " + resultado);

                boolean logroGuardado = false;
                if (resultado != null && !resultado.startsWith("Error") && !resultado.startsWith("Exception")) {
                    logroGuardado = Boolean.parseBoolean(resultado.trim());
                }

                boolean finalLogroGuardado = logroGuardado;
                handler.post(() -> callback.onSuccess(finalLogroGuardado));
            } catch (Exception e) {
                handler.post(() -> callback.onFailure(e));
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
                yaDesbloqueado = false;
            }
        });
    }

    private void updateRecyclerViews(List<Logro> logros) {
        if (logros == null) return;
        List<Logro> unlockedLogros = new ArrayList<>();
        List<Logro> lockedLogros = new ArrayList<>();

        for (Logro logro : logros) {
            Log.e("DEBUG", "Logro: " + logro.getNombre() +
                    " | Puntos: " + logro.getCantidadPuntos() +
                    " | Fecha Obtención: " + logro.getFechaObtenido());
            if (logro.getFechaObtenido() != null) {
                unlockedLogros.add(logro);  // Si tiene fecha obtenida, es un logro desbloqueado
            } else {
                lockedLogros.add(logro);    // Si no tiene fecha obtenida, es un logro bloqueado
            }
        }

        if (unlockedAdapter == null) {
            unlockedAdapter = new LogroAdapter(unlockedLogros);
            rvUnlockedLogros.setLayoutManager(new LinearLayoutManager(this));
            rvUnlockedLogros.setHasFixedSize(true); // Mejora rendimiento
            rvUnlockedLogros.setAdapter(unlockedAdapter);
        } else {
            unlockedAdapter.updateLogros(unlockedLogros);
            unlockedAdapter.notifyDataSetChanged();
        }

        if (lockedAdapter == null) {
            lockedAdapter = new LogroAdapter(lockedLogros);
            rvLockedLogros.setLayoutManager(new LinearLayoutManager(this));
            rvLockedLogros.setHasFixedSize(true); // Mejora rendimiento
            rvLockedLogros.setAdapter(lockedAdapter);
        } else {
            lockedAdapter.updateLogros(lockedLogros);
            lockedAdapter.notifyDataSetChanged();
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
