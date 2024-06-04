package com.example.massfitness;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetalleReservaActivity extends AppCompatActivity {

    private TextView tvClassName, tvClassTime, tvClassDuration, tvClassIntensity;
    private TextView tvClassAvailability, tvClassLocation, tvClassInstructor, tvClassDescription;
    private ImageView ivClassImage, ivBack;
    private Button btnReservar;
    private String entrenador;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reserva);

        tvClassName = findViewById(R.id.tvClassName);
        tvClassTime = findViewById(R.id.tvClassTime);
        tvClassDuration = findViewById(R.id.tvClassDuration);
        tvClassIntensity = findViewById(R.id.tvClassIntensity);
        tvClassAvailability = findViewById(R.id.tvClassAvailability);
        tvClassLocation = findViewById(R.id.tvClassLocation);
        tvClassInstructor = findViewById(R.id.tvClassInstructor);
        tvClassDescription = findViewById(R.id.tvClassDescription);
        ivClassImage = findViewById(R.id.ivClassImage);
        ivBack = findViewById(R.id.ivBack);
        btnReservar = findViewById(R.id.btnReservar);

        Intent intent = getIntent();
        String salaNombre = intent.getStringExtra("SALA_NOMBRE");

        if (salaNombre != null) {
            switch (salaNombre) {
                case "Boxeo":
                    setClassDetails("BOXEO", "2024-06-05 10:00:00", "DURACIÓN: 60'", "INTENSIDAD: ALTA",
                            "12/31", "LUGAR: Pista Atletismo", "MONITOR: MAIKEL",
                            "Tonificación dirigida acompañada de soporte musical, donde se realizan ejercicios de fortalecimiento muscular global.",
                            R.drawable.boxeo_img_info, "Maikel");
                    break;
                case "Pilates":
                    setClassDetails("PILATES", "HORA: 18:00", "DURACIÓN: 50'", "INTENSIDAD: MEDIA",
                            "10/20", "LUGAR: Sala de Yoga", "MONITOR: LAURA",
                            "Clase de ejercicios controlados para fortalecer y flexibilizar el cuerpo.",
                            R.drawable.pilates_img_info, "Laura");
                    break;
                case "Sala de Musculación":
                    setClassDetails("MUSCULACIÓN", "HORA: 16:00", "DURACIÓN: 70'", "INTENSIDAD: ALTA",
                            "15/25", "LUGAR: Sala de Musculación", "MONITOR: JOHN",
                            "Sesión dedicada a ejercicios de fuerza para tonificar y ganar masa muscular.",
                            R.drawable.musculacion_img_info,"John");
                    break;
                case "Sala de Abdominales":
                    setClassDetails("ABDOMINALES", "HORA: 15:30", "DURACIÓN: 30'", "INTENSIDAD: MEDIA",
                            "8/15", "LUGAR: Sala de Abdominales", "MONITOR: LUCIA",
                            "Ejercicios enfocados en fortalecer el core y mejorar la postura.",
                            R.drawable.ic_abdominales, "Jose");
                    break;
                case "Yoga":
                    setClassDetails("YOGA", "HORA: 19:00", "DURACIÓN: 60'", "INTENSIDAD: BAJA",
                            "20/30", "LUGAR: Sala de Yoga", "MONITOR: LAURA",
                            "Práctica de posturas y respiración para mejorar el equilibrio y la flexibilidad.",
                            R.drawable.yoga_img_info, "Laura");
                    break;
            }
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvClassInstructor.setOnClickListener(v -> {
            Intent intent2 = new Intent(DetalleReservaActivity.this, EntrenadoresActivity.class);
            intent2.putExtra("ENTRENADOR", entrenador);
            startActivity(intent2);
        });
//
//        btnReservar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle reserve button click
//                Intent intent = new Intent(DetalleReservaActivity.this, ReservationActivity.class);
//                intent.putExtra("class_name", tvClassName.getText().toString());
//                intent.putExtra("class_time", tvClassTime.getText().toString().substring(6));
//                startActivity(intent);
//            }
//        });
    }

    private void abrirEntrenador(String entrenador) {
        Intent intent = new Intent(this, EntrenadoresActivity.class);
        intent.putExtra("ENTRENADOR", entrenador);

        startActivity(intent);
    }

    private void setClassDetails(String name, String time, String duration, String intensity,
                                 String availability, String location, String instructor, String description,
                                 int imageResource, String trainer) {
        tvClassName.setText(name);
        tvClassTime.setText(time);
        tvClassDuration.setText(duration);
        tvClassIntensity.setText(intensity);
        tvClassAvailability.setText(availability);
        tvClassLocation.setText(location);
        tvClassInstructor.setText(instructor);
        tvClassDescription.setText(description);
        ivClassImage.setImageResource(imageResource);
        entrenador = trainer;
    }
    public void onReservarClick(View view) {
        findViewById(R.id.confirmationDialog).setVisibility(View.VISIBLE);
    }

    public void onConfirmarClick(View view) {
        getUserIdAndReservas(view);
        findViewById(R.id.confirmationDialog).setVisibility(View.GONE);
    }
    private void getUserIdAndReservas(View view) {
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
                            Log.d("ID USUARIO", ""+idUsuario);
                            agregarReserva(view);
                        } catch (Exception e) {
                            showError("Error al obtener el ID de usuario: " + e.getMessage());
                            Log.e("ERROR_USUARIO", e.getMessage());
                        }
                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
    }
    private void agregarReserva(View view) {
        String tipoReserva = tvClassName.getText().toString();
        String horarioReserva = tvClassTime.getText().toString();
        String estadoReserva = "SI";

        if (tipoReserva.isEmpty() || horarioReserva.isEmpty() || estadoReserva.isEmpty()) {
            showError("Por favor, completa todos los campos.");
            return;
        }

        if (isNetworkAvailable()) {
            Resources res = getResources();
            String urlAgregarReserva = res.getString(R.string.url) + "reservas/addReserva";

            try {
                // Adjust the date format here if necessary
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = sdf.parse(horarioReserva);

                String idUser = ""+idUsuario;
                Log.e("AGREGAR RESERVA", String.format("%s %s %s %s %s", urlAgregarReserva, idUser, tipoReserva, parsedDate, estadoReserva));
                agregarReservaEnServidor(urlAgregarReserva, idUser, tipoReserva, parsedDate, estadoReserva);
            } catch (ParseException e) {
                showError("Error al parsear la fecha: " + e.getMessage());
            }
        } else {
            showError("No hay conexión a Internet.");
        }
    }

    private void agregarReservaEnServidor(String url, String idUsuario, String tipoReserva, Date horarioReserva, String estadoReserva) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            List<Parametro> params = new ArrayList<>();
            params.add(new Parametro("usuario_id", idUsuario));
            params.add(new Parametro("espacio_id", 1+""));
            params.add(new Parametro("tipo_reserva", tipoReserva));
            params.add(new Parametro("horario_reserva", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(horarioReserva))); // Use ISO 8601 format
            params.add(new Parametro("estado_reserva", estadoReserva));

            String resultado = interopera.postText(url, params);
            Log.e("RESULTADO", resultado);
            handler.post(() -> {
                try {
                    Integer idCreado = Integer.parseInt(resultado);
                    if (idCreado > 0) {
                        setResult(RESULT_OK);
                        showSuccess("Reserva agregada correctamente.");
                        finish();
                    } else {
                        showError("Error al agregar la reserva. Por favor, inténtalo de nuevo más tarde.");
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    showError("Error al agregar la reserva. ID creado -1");
                }
            });
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

    public void onCancelarClick(View view) {
        findViewById(R.id.confirmationDialog).setVisibility(View.GONE);
    }
}
