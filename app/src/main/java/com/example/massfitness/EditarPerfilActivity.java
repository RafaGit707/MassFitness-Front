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

public class EditarPerfilActivity extends AppCompatActivity {

    private TextView tvNombreLabel, tvEmailLabel;
    private EditText etNombre, etCorreo, etAntiguaContrasena, etNuevaContrasena, etConfirmarContrasena;
    private Button btnGuardar, btnSalir, btnBorrarCuenta;
    private ImageView ivBack;
    private SharedPreferences preferences;
    private ExecutorService executorService;
    private Handler handler;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        tvNombreLabel = findViewById(R.id.tvNombreLabel);
        tvEmailLabel = findViewById(R.id.tvEmailLabel);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etAntiguaContrasena = findViewById(R.id.etAntiguaContrasena);
        etNuevaContrasena = findViewById(R.id.etNuevaContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnSalir = findViewById(R.id.btnSalir);
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPerfil();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        btnBorrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarCuenta();
            }
        });
        getUserId();
    }

    private void getUserId() {
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
                            cargarDatosUsuario();
                        } catch (Exception e) {
                            showError("Error al obtener el ID de usuario: " + e.getMessage());
                        }
                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
    }
    private void cargarDatosUsuario() {
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
                            JSONObject usuarioJson = new JSONObject(resultado);
                            etNombre.setText(usuarioJson.getString("nombre"));
                            etCorreo.setText(usuarioJson.getString("correo_electronico"));
                        } catch (Exception e) {
                            showError("Error al obtener los datos de usuario");
                        }
                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
    }

    private void actualizarPerfil() {
        String nombre = etNombre.getText().toString();
        String correo = etCorreo.getText().toString();
        String antiguaContrasena = etAntiguaContrasena.getText().toString();
        String nuevaContrasena = etNuevaContrasena.getText().toString();
        String confirmarContrasena = etConfirmarContrasena.getText().toString();

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNetworkAvailable()) {
            verificarAntiguaContrasena(correo, antiguaContrasena, nombre, correo, nuevaContrasena);
        } else {
            Toast.makeText(this, "No hay conexión a Internet.", Toast.LENGTH_SHORT).show();
        }
    }
    private void verificarAntiguaContrasena(String correo, String antiguaContrasena, String nombre, String nuevoCorreo, String nuevaContrasena) {
        Resources res = getResources();
        String urlVerificarContrasena = res.getString(R.string.url) + "usuarios/existe";

        List<Parametro> parametros = new ArrayList<>();
        parametros.add(new Parametro("correo_electronico", correo));
        parametros.add(new Parametro("contrasena", antiguaContrasena));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlVerificarContrasena, parametros);

            handler.post(() -> {
                if (resultado.equals("true")) {
                    actualizarPerfilEnServidor(nombre, nuevoCorreo, nuevaContrasena);
                } else {
                    Toast.makeText(EditarPerfilActivity.this, "La contraseña antigua es incorrecta.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void actualizarPerfilEnServidor(String nombre, String correo, String nuevaContrasena) {
        Resources res = getResources();
        String urlActualizarPerfil = res.getString(R.string.url) + "usuarios/actualizar/" + idUsuario;

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", nombre);
            jsonObject.put("correo_electronico", correo);
            jsonObject.put("contrasena", nuevaContrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(urlActualizarPerfil)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(() -> Toast.makeText(EditarPerfilActivity.this, "Error en la conexión al servidor", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(() -> {
                    if (!response.isSuccessful()) {
                        try {
                            String errorMessage = response.body() != null ? response.body().string() : "No se pudo obtener la respuesta del servidor";
                            Toast.makeText(EditarPerfilActivity.this, "Error en la respuesta del servidor: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(EditarPerfilActivity.this, "Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado correctamente.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void cerrarSesion() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(EditarPerfilActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void borrarCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas borrar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (isNetworkAvailable()) {
                        Resources res = getResources();
                        String urlBorrarCuenta = res.getString(R.string.url) + "usuarios/eliminar/" + idUsuario;

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("correo_electronico", tvEmailLabel.getText().toString());

                            borrarCuentaEnServidor(urlBorrarCuenta, jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No hay conexión a Internet.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void borrarCuentaEnServidor(String url, JSONObject jsonObject) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(() -> Toast.makeText(EditarPerfilActivity.this, "Error en la conexión al servidor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    handler.post(() -> {
                        try {
                            Toast.makeText(EditarPerfilActivity.this, "Error en la respuesta del servidor: " + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(EditarPerfilActivity.this, "Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(EditarPerfilActivity.this, "Cuenta eliminada correctamente.", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(EditarPerfilActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }
            }
        });
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