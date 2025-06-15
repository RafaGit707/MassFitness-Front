package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.massfitness.entidades.Entrenador;
import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddClaseActivity extends AppCompatActivity {

    private EditText etNombre, etCapacidad;
    private Spinner spinnerEntrenadores;
    private Button btnGuardar;
    private ImageView ivBack;
    private ExecutorService executor;
    private Handler handler;
    private List<Entrenador> listaEntrenadores = new ArrayList<>();
    private ArrayAdapter<Entrenador> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clase);

        etNombre = findViewById(R.id.etNombreClase);
        etCapacidad = findViewById(R.id.etCapacidadClase);
        spinnerEntrenadores = findViewById(R.id.spinnerEntrenadores);
        btnGuardar = findViewById(R.id.btnGuardarClase);
        ivBack = findViewById(R.id.ivBack);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setupSpinner();
        cargarEntrenadores();

        ivBack.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarClase());
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaEntrenadores);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntrenadores.setAdapter(spinnerAdapter);
    }

    private void cargarEntrenadores() {
        String url = getResources().getString(R.string.url) + "entrenadores";
        executor.execute(() -> {
            String resultado = Internetop.getInstance().getText(url, new ArrayList<>());
            handler.post(() -> {
                if (resultado != null && !resultado.startsWith("Error")) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultado);
                        listaEntrenadores.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            listaEntrenadores.add(new Entrenador(
                                    obj.getInt("idEntrenador"),
                                    obj.getString("nombre_entrenador"),
                                    obj.getString("especializacion")
                            ));
                        }
                        spinnerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error procesando entrenadores", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error cargando entrenadores", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

// En AddClaseActivity.java

    private void guardarClase() {
        String nombre = etNombre.getText().toString().trim();
        String capacidadStr = etCapacidad.getText().toString().trim();
        Entrenador entrenadorSeleccionado = (Entrenador) spinnerEntrenadores.getSelectedItem();

        if (nombre.isEmpty() || capacidadStr.isEmpty() || entrenadorSeleccionado == null) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);

        // --- CONSTRUCCIÓN MANUAL Y CORRECTA DEL JSON ---
        JSONObject claseJson = new JSONObject();
        try {
            claseJson.put("nombre", nombre);
            // El backend espera un int, no un String para la capacidad.
            claseJson.put("capacidad_maxima", Integer.parseInt(capacidadStr));

            // Creamos el objeto JSON anidado para el entrenador
            JSONObject entrenadorJsonAnidado = new JSONObject();
            // La propiedad en tu clase Java Entrenador se llama 'idEntrenador' (camelCase)
            entrenadorJsonAnidado.put("idEntrenador", entrenadorSeleccionado.getIdEntrenador());

            // Anidamos el objeto, NO un string
            claseJson.put("entrenador", entrenadorJsonAnidado);

        } catch (JSONException e) {
            e.printStackTrace();
            btnGuardar.setEnabled(true);
            Toast.makeText(this, "Error al crear la petición.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = getResources().getString(R.string.url) + "clases";
        String jsonParaEnviar = claseJson.toString(); // El JSON completo y bien formado

        executor.execute(() -> {
            // --- USAMOS EL NUEVO MÉTODO ---
            String resultado = Internetop.getInstance().postJsonString(url, jsonParaEnviar);

            handler.post(() -> {
                btnGuardar.setEnabled(true);
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    Toast.makeText(AddClaseActivity.this, "Clase añadida con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddClaseActivity.this, "Error al añadir la clase: " + resultado, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}