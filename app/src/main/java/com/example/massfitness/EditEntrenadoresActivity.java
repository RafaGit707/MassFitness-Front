package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.massfitness.adaptadores.EntrenadorAdapter;
import com.example.massfitness.entidades.Entrenador;
import com.example.massfitness.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditEntrenadoresActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EntrenadorAdapter entrenadorAdapter;
    private List<Entrenador> listEntrenadores = new ArrayList<>();
    private RecyclerView recyclerEntrenadores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entrenadores);

        ivBack = findViewById(R.id.ivBack);
        recyclerEntrenadores = findViewById(R.id.recyclerEntrenadores);

        recyclerEntrenadores.setLayoutManager(new LinearLayoutManager(this));
        entrenadorAdapter = new EntrenadorAdapter(listEntrenadores);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        obtenerEntrenadores(new EditEntrenadoresActivity.Callback<List<Entrenador>>() {
            @Override
            public void onSuccess(List<Entrenador> entrenadores) {
                entrenadorAdapter = new EntrenadorAdapter(entrenadores);
                recyclerEntrenadores.setAdapter(entrenadorAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditEntrenadoresActivity.this, "Error al cargar entrenadores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    private void obtenerEntrenadores(EditEntrenadoresActivity.Callback<List<Entrenador>> callback) {
        String urlEntrenadores = getResources().getString(R.string.url) + "entrenadores";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlEntrenadores, new ArrayList<>());

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception") || resultado.startsWith("error")) {
                    callback.onFailure(new Exception(resultado));
                } else {
                    try {
                        JSONArray entrenadoresJson = new JSONArray(resultado);
                        List<Entrenador> listaEntrenadores = new ArrayList<>();

                        for (int i = 0; i < entrenadoresJson.length(); i++) {
                            JSONObject obj = entrenadoresJson.getJSONObject(i);
                            Entrenador entrenador = new Entrenador();
                            entrenador.setIdEntrenador(obj.getInt("idEntrenador"));
                            entrenador.setNombre_entrenador(obj.getString("nombre_entrenador"));
                            entrenador.setEspecializacion(obj.getString("especializacion"));
                            listaEntrenadores.add(entrenador);
                        }

                        callback.onSuccess(listaEntrenadores);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                }
            });
        });
    }
    private void updateRecyclerView() {
        obtenerEntrenadores(new EditEntrenadoresActivity.Callback<List<Entrenador>>() {
            @Override
            public void onSuccess(List<Entrenador> entrenadores) {
                if (entrenadorAdapter != null) {
                    entrenadorAdapter.updateEntrenadores(entrenadores);
                } else {
                    entrenadorAdapter = new EntrenadorAdapter(entrenadores);
                    recyclerEntrenadores.setAdapter(entrenadorAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditEntrenadoresActivity.this, "Error al actualizar entrenadores", Toast.LENGTH_SHORT).show();
            }
        });
    }
}