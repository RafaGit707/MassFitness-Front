package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    TextView nuevoUsuario, bienvenidoLabel, continuarLabel;
    ImageView registerImageView;
    TextInputLayout correoRegisterTextField, contrasenaTextField, confirmarContrasenaTextField, nombreTextField;
    MaterialButton inicioSesion;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerImageView = findViewById(R.id.registerImageView);
        bienvenidoLabel = findViewById(R.id.bienvenidoLabel);
        continuarLabel = findViewById (R.id.continuarLabel);
        nombreTextField = findViewById(R.id.nameTextField);
        correoRegisterTextField = findViewById(R.id.emailRegisterTextField);
        contrasenaTextField = findViewById(R.id.contrasenaTextField);
        confirmarContrasenaTextField = findViewById(R.id.confirmarContrasenaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R. id.nuevoUsuario);
        progressBar = findViewById(R.id.pb_register);

        MaterialButton inicioSesionButton = findViewById(R.id.inicioSesion);
        inicioSesionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                inicioSesionButton.startAnimation(anim);
                registrarUsuario(v);
            }
        });

        nuevoUsuario.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionBack();
            }
        }) ;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        transitionBack();
    }

    public void transitionBack() {

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

        Pair[] pairs = new Pair [7];
        pairs [0] = new Pair<View, String>(registerImageView, "loginImageTrans");
        pairs [1] = new Pair<View, String>(bienvenidoLabel, "textTrans");
        pairs [2] = new Pair<View, String>(continuarLabel, "iniciaSesionTextTrans");
        pairs [3] = new Pair<View, String>(correoRegisterTextField, "emailInputTextTrans");
        pairs [4] = new Pair<View, String>(contrasenaTextField, "passwordInputTextTrans");
        pairs [5] = new Pair<View, String>(inicioSesion, "buttonLoginTrans");
        pairs [6] = new Pair<View, String>(nuevoUsuario, "newUserTrans");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
            finish();
        }
    }
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public void registrarUsuario(View view) {
        String nombre = nombreTextField.getEditText().getText().toString();
        String email = correoRegisterTextField.getEditText().getText().toString();
        String contrasena = contrasenaTextField.getEditText().getText().toString();
        String contrasenaConfirmar = confirmarContrasenaTextField.getEditText().getText().toString();

        if (nombre.isEmpty() || contrasenaConfirmar.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            showError("Por favor, completa todos los campos.");
            return;
        }

        if (!contrasena.equals(contrasenaConfirmar)) {
            showError("La contraseña no corresponde.");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Por favor, ingresa un correo electrónico válido.");
            return;
        }

        if (contrasena.length() < 8) {
            showError("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        if (isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);

            Resources res = getResources();
            String urlRegistro = res.getString(R.string.url) + "usuarios/addUsuario";

            registrarUsuarioEnServidor(urlRegistro, nombre, email, contrasena);
        } else {
            showError("No hay conexión a Internet.");
        }
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