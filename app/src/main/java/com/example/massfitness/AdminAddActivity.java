package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.massfitness.adaptadores.ClaseAdapter;
import com.example.massfitness.adaptadores.EntrenadorAdapter;
import com.example.massfitness.adaptadores.LogroAdapter;
import com.example.massfitness.entidades.Clase;
import com.example.massfitness.entidades.Entrenador;
import com.example.massfitness.entidades.Logro;
import com.example.massfitness.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminAddActivity extends AppCompatActivity {
    private ImageView ivBack, addClase, addEntrenador;
    private CardView cvClases, cvEntrenadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);

        ivBack = findViewById(R.id.ivBack);
        addEntrenador = findViewById(R.id.addEntrenador);
        addClase = findViewById(R.id.addClase);
        cvClases = findViewById(R.id.cvClases);
        cvEntrenadores = findViewById(R.id.cvEntrenadores);

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

        cvClases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminAddActivity.this, EditClasesActivity.class));
            }
        });

        cvEntrenadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminAddActivity.this, EditEntrenadoresActivity.class));
            }
        });

        addEntrenador.setOnClickListener(v -> startActivity(new Intent(AdminAddActivity.this, AddEntrenadorActivity.class)));

        addClase.setOnClickListener(v -> startActivity(new Intent(AdminAddActivity.this, AddClaseActivity.class)));

    }
    public void abrirClases(View view) {
        startActivity(new Intent(AdminAddActivity.this, EditClasesActivity.class));
    }
    public void abrirEntrenadores(View view) {
        startActivity(new Intent(AdminAddActivity.this, EditEntrenadoresActivity.class));
    }
}