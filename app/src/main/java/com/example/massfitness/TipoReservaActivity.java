package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class TipoReservaActivity extends AppCompatActivity {

    ImageView loginImageView;
    CardView reservaClaseButton, reservaEspacioButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_reserva);

        loginImageView = findViewById (R.id.logoImageView);
        reservaClaseButton = findViewById(R.id.reservaClaseButton);
        reservaEspacioButton = findViewById(R.id.reservaEspacioButton);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reservaClaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                reservaClaseButton.startAnimation(anim);
                startActivity(new Intent(TipoReservaActivity.this, ReservaClaseActivity.class));
            }
        });

        reservaEspacioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                reservaEspacioButton.startAnimation(anim);
                startActivity(new Intent(TipoReservaActivity.this, ReservaEspacioActivity.class));
            }
        });
    }
}