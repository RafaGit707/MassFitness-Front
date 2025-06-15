package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEntrenadorActivity extends AppCompatActivity {

    private EditText etNombre, etEspecializacion;
    private Button btnGuardar;
    private ImageView ivBack;
    private ExecutorService executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entrenador);

        etNombre = findViewById(R.id.etNombreEntrenador);
        etEspecializacion = findViewById(R.id.etEspecializacionEntrenador);
        btnGuardar = findViewById(R.id.btnGuardarEntrenador);
        ivBack = findViewById(R.id.ivBack);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        ivBack.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarEntrenador());
    }

    private void guardarEntrenador() {
        String nombre = etNombre.getText().toString().trim();
        String especializacion = etEspecializacion.getText().toString().trim();

        if (nombre.isEmpty() || especializacion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false); // Deshabilitar botón para evitar doble clic

        List<Parametro> params = new ArrayList<>();
        // El backend espera 'nombre_entrenador', no 'nombreEntrenador'
        params.add(new Parametro("nombre_entrenador", nombre));
        params.add(new Parametro("especializacion", especializacion));

        String url = getResources().getString(R.string.url) + "entrenadores";

        executor.execute(() -> {
            String resultado = Internetop.getInstance().postText(url, params);
            handler.post(() -> {
                btnGuardar.setEnabled(true); // Volver a habilitar el botón
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    Toast.makeText(AddEntrenadorActivity.this, "Entrenador añadido con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la activity y volver a la anterior
                } else {
                    Toast.makeText(AddEntrenadorActivity.this, "Error al añadir entrenador", Toast.LENGTH_SHORT).show();
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

