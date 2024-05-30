package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

public class ReservaActivity extends AppCompatActivity {

    private CardView boxeoButton, pilatesButton, musculacionButton, abdominalesButton, yogaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        boxeoButton = findViewById(R.id.boxeoButton);
        pilatesButton = findViewById(R.id.pilatesButton);
        musculacionButton = findViewById(R.id.musculacionButton);
        abdominalesButton = findViewById(R.id.abdominalesButton);
        yogaButton = findViewById(R.id.yogaButton);

        boxeoButton.setOnClickListener(v -> abrirDetalleReserva("Boxeo"));
        pilatesButton.setOnClickListener(v -> abrirDetalleReserva("Pilates"));
        musculacionButton.setOnClickListener(v -> abrirDetalleReserva("Sala de Musculación"));
        abdominalesButton.setOnClickListener(v -> abrirDetalleReserva("Sala de Abdominales"));
        yogaButton.setOnClickListener(v -> abrirDetalleReserva("Yoga"));
    }

    private void abrirDetalleReserva(String sala) {
        Intent intent = new Intent(this, DetalleReservaActivity.class);
        intent.putExtra("SALA_NOMBRE", sala);
        startActivity(intent);
    }
}