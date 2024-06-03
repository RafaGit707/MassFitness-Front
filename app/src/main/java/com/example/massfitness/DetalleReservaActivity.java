package com.example.massfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleReservaActivity extends AppCompatActivity {

    private TextView tvClassName, tvClassTime, tvClassDuration, tvClassIntensity;
    private TextView tvClassAvailability, tvClassLocation, tvClassInstructor, tvClassDescription;
    private ImageView ivClassImage, ivBack;
    private Button btnReservar;
    private String entrenador;

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
                    setClassDetails("BOXEO", "HORA: 17:30", "DURACIÓN: 60'", "INTENSIDAD: ALTA",
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
//                    setClassDetails("ABDOMINALES", "HORA: 15:30", "DURACIÓN: 30'", "INTENSIDAD: MEDIA",
//                            "8/15", "LUGAR: Sala de Abdominales", "MONITOR: LUCIA",
//                            "Ejercicios enfocados en fortalecer el core y mejorar la postura.",
//                            R.drawable.abdominales_img_info);
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
        // Lógica para confirmar la reserva
        findViewById(R.id.confirmationDialog).setVisibility(View.GONE);
    }

    public void onCancelarClick(View view) {
        findViewById(R.id.confirmationDialog).setVisibility(View.GONE);
    }
}
