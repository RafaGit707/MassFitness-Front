package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminAddActivity extends AppCompatActivity {
    private ImageView ivBack, addClase, addEntrenador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);

        ivBack = findViewById(R.id.ivBack);
        addEntrenador = findViewById(R.id.addEntrenador);
        addClase = findViewById(R.id.addClase);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addEntrenador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminAddActivity.this, AddEntrenadorActivity.class));
            }
        });

        addClase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminAddActivity.this, AddClaseActivity.class));
            }
        });
    }
}