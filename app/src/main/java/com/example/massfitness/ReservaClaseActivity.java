package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ReservaClaseActivity extends AppCompatActivity {

    private CardView boxeoButton, pilatesButton, yogaButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_clase);

        boxeoButton = findViewById(R.id.boxeoButton);
        pilatesButton = findViewById(R.id.pilatesButton);
        yogaButton = findViewById(R.id.yogaButton);
        ivBack = findViewById(R.id.ivBack);

        boxeoButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.boxeo_key)));
        pilatesButton.setOnClickListener(v -> abrirDetalleReserva(getString(R.string.pilates_key)));
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