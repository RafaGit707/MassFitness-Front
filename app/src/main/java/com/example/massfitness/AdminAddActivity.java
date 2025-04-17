package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
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
    private EntrenadorAdapter entrenadorAdapter;
    private ClaseAdapter claseAdapter;
    private List<Clase> listClases = new ArrayList<>();
    private List<Entrenador> listEntrenadores = new ArrayList<>();
    private RecyclerView recyclerClases, recyclerEntrenadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);

        ivBack = findViewById(R.id.ivBack);
        addEntrenador = findViewById(R.id.addEntrenador);
        addClase = findViewById(R.id.addClase);
        recyclerClases = findViewById(R.id.recyclerClases);
        recyclerEntrenadores = findViewById(R.id.recyclerEntrenadores);

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));
        recyclerEntrenadores.setLayoutManager(new LinearLayoutManager(this));
        entrenadorAdapter = new EntrenadorAdapter(listEntrenadores);
        claseAdapter = new ClaseAdapter(listClases);

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

        addEntrenador.setOnClickListener(v -> startActivity(new Intent(AdminAddActivity.this, AddEntrenadorActivity.class)));

        addClase.setOnClickListener(v -> startActivity(new Intent(AdminAddActivity.this, AddClaseActivity.class)));

        // LLAMADAS CORRECTAS A LOS MÉTODOS
        obtenerEntrenadores(new Callback<List<Entrenador>>() {
            @Override
            public void onSuccess(List<Entrenador> entrenadores) {
                entrenadorAdapter = new EntrenadorAdapter(entrenadores);
                recyclerEntrenadores.setAdapter(entrenadorAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminAddActivity.this, "Error al cargar entrenadores", Toast.LENGTH_SHORT).show();
            }
        });

        obtenerClases(new Callback<List<Clase>>() {
            @Override
            public void onSuccess(List<Clase> clases) {
                claseAdapter = new ClaseAdapter(clases);
                recyclerClases.setAdapter(claseAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminAddActivity.this, "Error al cargar clases", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    private void obtenerEntrenadores(Callback<List<Entrenador>> callback) {
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
    private void obtenerClases(Callback<List<Clase>> callback) {
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
        obtenerEntrenadores(new Callback<List<Entrenador>>() {
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
                Toast.makeText(AdminAddActivity.this, "Error al actualizar entrenadores", Toast.LENGTH_SHORT).show();
            }
        });

        obtenerClases(new Callback<List<Clase>>() {
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
                Toast.makeText(AdminAddActivity.this, "Error al actualizar clases", Toast.LENGTH_SHORT).show();
            }
        });
    }


}