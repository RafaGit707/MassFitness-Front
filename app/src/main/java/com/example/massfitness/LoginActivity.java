package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

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
        String email = usuarioTextField.getEditText().getText().toString();
        String contrasena = contrasenaTextField.getEditText().getText().toString();

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

            loginUsuarioEnServidor(urlLogin, parametros);
        } else {
            showError("No hay conexión a Internet.");
        }
    }

    private void loginUsuarioEnServidor(String url, List<Parametro> params) {
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
                        progressBar.setVisibility(View.GONE);
                        System.out.println("Resultado del servidor: " + resultado);
                        if (resultado.equals("true")) {
                            showSuccess("Usuario logueado correctamente.");
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showError("Error al loguear el usuario. Por favor, inténtalo de nuevo más tarde.");
                        }
                    }
                });
            }
        });
    }


    private void registrarUsuarioEnServidor(String url, String nombre, String email, String contrasena) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Internetop interopera = Internetop.getInstance();
                List<Parametro> params = new ArrayList<>();
                params.add(new Parametro("nombre", nombre));
                params.add(new Parametro("correo_electronico", email));
                params.add(new Parametro("contrasena", contrasena));

                String resultado = interopera.postText(url,params);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Button btAceptar = findViewById(R.id.inicioSesion);
                        ProgressBar pbAceptar = findViewById(R.id.pb_register);

                        pbAceptar.setVisibility(View.GONE);
                        btAceptar.setEnabled(true);
                        btAceptar.setClickable(true);

                        try {
                            Integer idCreado = Integer.parseInt(resultado);
                            if (idCreado > 0) {
                                setResult(RESULT_OK);
                                showSuccess("Usuario registrado correctamente.");
                                finish();
                            } else {
                                showError("Error al registrar el usuario. Por favor, inténtalo de nuevo más tarde.");
                            }
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            showError("Error al registrar el usuario. ID creado -1");
                        }
                    }
                });
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
        Context context = this.getApplicationContext();
        Toast toast = Toast.makeText(context, error, Toast.LENGTH_SHORT);
        toast.show();
    }
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT);
    }
}