package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

public class MenuActivity extends AppCompatActivity {

    ImageView loginImageView, userImageView;
    CardView reservaButton, logrosButton, acercaButton, equipoButton, misReservasButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loginImageView = findViewById (R.id.logoImageView);
        userImageView = findViewById(R.id.userImageView);
        reservaButton = findViewById(R.id.reservaButton);
        misReservasButton = findViewById(R.id.misReservasButton);
        logrosButton = findViewById(R.id.logrosButton);
        acercaButton = findViewById(R.id.acercaButton);
        equipoButton = findViewById(R.id.equipoButton);

        reservaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                reservaButton.startAnimation(anim);
                startActivity(new Intent(MenuActivity.this, ReservaActivity.class));
            }
        });
        misReservasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                misReservasButton.startAnimation(anim);
                startActivity(new Intent(MenuActivity.this, MisReservasActivity.class));
            }
        });

//        logrosButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
//                reservaButton.startAnimation(anim);
//                startActivity(new Intent(MenuActivity.this, LogrosActivity.class));
//            }
//        });
//
//        acercaButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
//                reservaButton.startAnimation(anim);
//                startActivity(new Intent(MenuActivity.this, AcercaActivity.class));
//            }
//        });
//
        equipoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                equipoButton.startAnimation(anim);
                startActivity(new Intent(MenuActivity.this, EntrenadoresActivity.class));
            }
        });
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, PerfilActivity.class));
            }
        });
    }

}