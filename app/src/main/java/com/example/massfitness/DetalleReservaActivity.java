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
                    tvClassName.setText("BOXEO");
                    tvClassTime.setText("HORA: 17:30");
                    tvClassDuration.setText("DURACIÓN: 60'");
                    tvClassIntensity.setText("INTENSIDAD: ALTA");
                    tvClassAvailability.setText("12/31");
                    tvClassLocation.setText("LUGAR: Pista Atletismo");
                    tvClassInstructor.setText("MONITOR: MAIKEL");
                    tvClassDescription.setText("Tonificación dirigida acompañada de soporte musical, donde se realizan ejercicios de fortalecimiento muscular global.");
                    ivClassImage.setImageResource(R.drawable.boxeo_img_info);
                    break;
            }
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                ivBack.startAnimation(anim);

                finish();
            }
        });

//        tvClassInstructor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ClassDetailActivity.this, EntrenadorDetailActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnReservar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ClassDetailActivity.this, ReservationActivity.class);
//                intent.putExtra("class_name", "BOXEO");
//                intent.putExtra("class_time", "17:30");
//                startActivity(intent);
//            }
//        });
    }
}
