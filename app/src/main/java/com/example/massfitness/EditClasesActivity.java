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

import com.example.massfitness.adaptadores.ClaseAdapter;
import com.example.massfitness.entidades.Clase;
import com.example.massfitness.entidades.Entrenador;
import com.example.massfitness.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditClasesActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ClaseAdapter claseAdapter;
    private List<Clase> listClases = new ArrayList<>();
    private RecyclerView recyclerClases;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clases);

        ivBack = findViewById(R.id.ivBack);
        recyclerClases = findViewById(R.id.recyclerClases);

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));
        claseAdapter = new ClaseAdapter(listClases);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        obtenerClases(new EditClasesActivity.Callback<List<Clase>>() {
            @Override
            public void onSuccess(List<Clase> clases) {
                claseAdapter = new ClaseAdapter(clases);
                recyclerClases.setAdapter(claseAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditClasesActivity.this, "Error al cargar clases", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    private void obtenerClases(EditClasesActivity.Callback<List<Clase>> callback) {
        String urlClases = getResources().getString(R.string.url) + "clases";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlClases, new ArrayList<>());

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception") || resultado.startsWith("error")) {
                    callback.onFailure(new Exception(resultado));
                } else {
                    try {
                        JSONArray clasesJson = new JSONArray(resultado);
                        List<Clase> listaClases = new ArrayList<>();

                        for (int i = 0; i < clasesJson.length(); i++) {
                            JSONObject obj = clasesJson.getJSONObject(i);
                            Clase clase = new Clase();
                            clase.setIdClase(obj.getInt("idClase"));
                            clase.setNombre(obj.getString("nombre"));
                            clase.setCapacidad_maxima(obj.getInt("capacidad_maxima"));

                            // Parseo del entrenador dentro de la clase
                            JSONObject entrenadorObj = obj.getJSONObject("entrenador");
                            Entrenador entrenador = new Entrenador();
                            entrenador.setIdEntrenador(entrenadorObj.getInt("idEntrenador"));
                            entrenador.setNombre_entrenador(entrenadorObj.getString("nombre_entrenador"));
                            entrenador.setEspecializacion(entrenadorObj.getString("especializacion"));
                            clase.setEntrenador(entrenador);

                            listaClases.add(clase);
                        }

                        callback.onSuccess(listaClases);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                }
            });
        });
    }

    private void updateRecyclerView() {

        obtenerClases(new EditClasesActivity.Callback<List<Clase>>() {
            @Override
            public void onSuccess(List<Clase> clases) {
                if (claseAdapter != null) {
                    claseAdapter.updateClases(clases);
                } else {
                    claseAdapter = new ClaseAdapter(clases);
                    recyclerClases.setAdapter(claseAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditClasesActivity.this, "Error al actualizar clases", Toast.LENGTH_SHORT).show();
            }
        });
    }
}