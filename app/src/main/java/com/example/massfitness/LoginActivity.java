package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
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
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    TextView loginLabel, nuevoUsuario, olvidasteContrasena;
    ImageView loginImageView;
    TextInputLayout usuarioTextField, contrasenaTextField;
    MaterialButton inicioSesion;
    ProgressBar progressBar;
    String email = "", contrasena = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginImageView = findViewById (R.id.loginImageView);
        loginLabel = findViewById(R.id.loginLabel);
        usuarioTextField = findViewById(R.id.emailTextField);
        contrasenaTextField = findViewById(R.id.contrasenaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        olvidasteContrasena = findViewById(R.id.olvidasteContra);
        progressBar = findViewById(R.id.pb_login);

        MaterialButton inicioSesionButton = findViewById(R.id.inicioSesion);
        inicioSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                inicioSesionButton.startAnimation(anim);

                loginUsuario();
            }
        });

        nuevoUsuario.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(loginImageView, "loginImageTrans");
                pairs[1] = new Pair<View, String>(loginLabel, "iniciaSesionTextTrans");
                pairs[2] = new Pair<View, String>(usuarioTextField, "emailInputTextTrans");
                pairs[3] = new Pair<View, String>(contrasenaTextField, "passwordInputTextTrans");
                pairs[4] = new Pair<View, String>(inicioSesion, "buttonLoginTrans");
                pairs[5] = new Pair<View, String>(nuevoUsuario, "newUserTrans");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                    finish();
                }
            }
        });
        olvidasteContrasena.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void loginUsuario() {
        email = usuarioTextField.getEditText().getText().toString();
        contrasena = contrasenaTextField.getEditText().getText().toString();

        if (email.isEmpty() || contrasena.isEmpty()) {
            showError("Por favor, completa todos los campos.");
            return;
        }

        if (isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Resources res = getResources();
            String urlLogin = res.getString(R.string.url) + "usuarios/existe";

            List<Parametro> parametros = new ArrayList<>();
            parametros.add(new Parametro("correo_electronico", email));
            parametros.add(new Parametro("contrasena", contrasena));

            verificarUsuarioExistente(urlLogin, parametros);
        } else {
            showError("No hay conexión a Internet.");
        }
    }
    /*    public void verificarUsuarioExistente(String urlLogin, List<Parametro> params) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Internetop interopera = Internetop.getInstance();
                    String resultado = interopera.getText(urlLogin, params);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonResponse = new JSONObject(resultado);
                                Log.e("jsonResponse", String.valueOf(jsonResponse));
                                Log.e("jsonResponse", resultado);
                                if (resultado.equals("true")) {
                                    obtenerRol();
                                } else {
                                    showError("Correo o contraseña incorrectos.");
                                }
                            } catch (JSONException e) {
                                showError("Error al verificar el usuario.");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }*/
    public void verificarUsuarioExistente(String urlLogin, List<Parametro> params) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlLogin, params).trim(); // Elimina espacios en blanco por seguridad

            Log.e("resultado", resultado);
            handler.post(() -> {
                if (resultado.equals("true")) {
                    obtenerDatosUsuario();  // Llamar a la función para obtener el usuario y su rol
                } else {
                    showError("Correo o contraseña incorrectos.");
                }
            });
        });
    }

/*    public void obtenerRol() {
        String rol = "ADMIN";

        if (isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Resources res = getResources();
            String urlLogin = res.getString(R.string.url) + "usuarios/correo/" + email;

            List<Parametro> parametros = new ArrayList<>();
            parametros.add(new Parametro("rol", rol));
            loginUsuarioEnServidor(urlLogin, parametros);
        } else {
            showError("No hay conexión a Internet.");
        }
    }*/

    /* private void loginUsuarioEnServidor(String url, List<Parametro> params) {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         Handler handler = new Handler(Looper.getMainLooper());
         executor.execute(new Runnable() {
             @Override
             public void run() {
                 Internetop interopera = Internetop.getInstance();
                 String resultado = interopera.getText(url, params);

                 handler.post(new Runnable() {
                     @Override
                     public void run() {
                         try {
                             JSONObject jsonResponse = new JSONObject(resultado);

                             if (jsonResponse.has("rol") && jsonResponse.has("correo_electronico")) {
                                 String rol = jsonResponse.getString("rol");

                                 showSuccess("Usuario logueado correctamente.");

                                 SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                 SharedPreferences.Editor editor = sharedPreferences.edit();
                                 editor.putBoolean("isLoggedIn", true);
                                 editor.putString("correo_electronico", email);
                                 editor.putString("rol", rol);
                                 editor.apply();

                                 Intent intent = new Intent(LoginActivity.this, MenuActivity.class);

                                 startActivity(intent);
                                 finish();
                             } else {
                                 showError("Error al loguear el usuario. Vuelva a intentarlo más tarde");
                             }
                         } catch (JSONException e) {
                             showError("Error en la respuesta del servidor");
                             e.printStackTrace();
                         }
                        *//* progressBar.setVisibility(View.GONE);
                        if (resultado.equals("true")) {
                            showSuccess("Usuario logueado correctamente.");

                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("correo_electronico", params.get(0).getValor());
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showError("Error al loguear el usuario. Vuelva a intentarlo más tarde");
                        }*//*
                    }
                });
            }
        });
    }*/
    public void obtenerDatosUsuario() {
        if (isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Resources res = getResources();
            String urlUsuario = res.getString(R.string.url) + "usuarios/correo/" + email;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Internetop interopera = Internetop.getInstance();
                String resultado = interopera.getText(urlUsuario, new ArrayList<>());

                handler.post(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(resultado);
                        Log.e("jsonResponse", resultado);

                        if (jsonResponse.has("rol") && jsonResponse.has("correo_electronico")) {
                            String rol = jsonResponse.getString("rol");
                            String correo = jsonResponse.getString("correo_electronico");

                            // Guardar en SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("correo_electronico", correo);
                            editor.putString("rol", rol);
                            editor.apply();

                            showSuccess("Usuario logueado correctamente.");

                            // Redirigir a la pantalla principal
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showError("Error al obtener los datos del usuario.");
                        }
                    } catch (JSONException e) {
                        showError("Error en la respuesta del servidor.");
                        e.printStackTrace();
                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
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
        Context context = this.getApplicationContext();
        Toast toast = Toast.makeText(context, error, Toast.LENGTH_SHORT);
        toast.show();
    }
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT);
    }
}