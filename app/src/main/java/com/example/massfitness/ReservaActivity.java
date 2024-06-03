package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ReservaActivity extends AppCompatActivity {

    private CardView boxeoButton, pilatesButton, musculacionButton, abdominalesButton, yogaButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        boxeoButton = findViewById(R.id.boxeoButton);
        pilatesButton = findViewById(R.id.pilatesButton);
        musculacionButton = findViewById(R.id.musculacionButton);
        abdominalesButton = findViewById(R.id.abdominalesButton);
        yogaButton = findViewById(R.id.yogaButton);
        ivBack = findViewById(R.id.ivBack);

        boxeoButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.boxeo_key)));
        pilatesButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.pilates_key)));
        musculacionButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.musculacion_key)));
        abdominalesButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.abdominales_key)));
        yogaButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.yoga_key)));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void abrirDetalleReserva(String sala) {
        Intent intent = new Intent(this, DetalleReservaActivity.class);
        intent.putExtra("SALA_NOMBRE", sala);

        startActivity(intent);
    }
}