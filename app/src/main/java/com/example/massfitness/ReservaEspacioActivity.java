package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ReservaEspacioActivity extends AppCompatActivity {

    private CardView musculacionButton, abdominalesButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_espacio);

        musculacionButton = findViewById(R.id.musculacionButton);
        abdominalesButton = findViewById(R.id.abdominalesButton);
        ivBack = findViewById(R.id.ivBack);

        musculacionButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.musculacion_key)));
        abdominalesButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.abdominales_key)));

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