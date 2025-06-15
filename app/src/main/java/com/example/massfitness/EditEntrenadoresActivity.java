package com.example.massfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.massfitness.adaptadores.EntrenadorAdapter;
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


public class EditEntrenadoresActivity extends AppCompatActivity implements EntrenadorAdapter.OnEntrenadorActionClickListener {

    private ImageView ivBack;
    private EntrenadorAdapter entrenadorAdapter;
    private List<Entrenador> listEntrenadores = new ArrayList<>();
    private RecyclerView recyclerEntrenadores;
    private ExecutorService executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entrenadores);

        ivBack = findViewById(R.id.ivBack);
        recyclerEntrenadores = findViewById(R.id.recyclerEntrenadores);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setupRecyclerView();

        ivBack.setOnClickListener(v -> finish());

        refreshEntrenadoresList();
    }

    private void setupRecyclerView() {
        recyclerEntrenadores.setLayoutManager(new LinearLayoutManager(this));
        entrenadorAdapter = new EntrenadorAdapter(this, listEntrenadores, this);
        recyclerEntrenadores.setAdapter(entrenadorAdapter);
    }

    private void refreshEntrenadoresList() {
        String urlEntrenadores = getResources().getString(R.string.url) + "entrenadores";
        executor.execute(() -> {
            String resultado = Internetop.getInstance().getText(urlEntrenadores, new ArrayList<>());
            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.equals("false")) {
                    Toast.makeText(EditEntrenadoresActivity.this, "Error al cargar entrenadores", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(resultado);
                        List<Entrenador> tempList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            tempList.add(new Entrenador(
                                    obj.getInt("idEntrenador"),
                                    obj.getString("nombre_entrenador"),
                                    obj.getString("especializacion")
                            ));
                        }
                        entrenadorAdapter.updateEntrenadores(tempList);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar los datos de entrenadores", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    public void onEditClick(Entrenador entrenador, int position) {
        mostrarDialogoEditarOAgregar(entrenador);
    }

    @Override
    public void onDeleteClick(Entrenador entrenador, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Entrenador")
                .setMessage("¿Estás seguro de que quieres eliminar a " + entrenador.getNombre_entrenador() + "?\n\n(Esto fallará si el entrenador está asignado a alguna clase)")
                .setPositiveButton("Sí", (dialog, which) -> eliminarEntrenadorBackend(entrenador.getIdEntrenador()))
                .setNegativeButton("No", null)
                .show();
    }

    private void mostrarDialogoEditarOAgregar(final Entrenador entrenador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_editar_entrenador, null); // Debes crear este layout
        builder.setView(view);

        final EditText etNombre = view.findViewById(R.id.etNombreEntrenador);
        final EditText etEspecializacion = view.findViewById(R.id.etEspecializacionEntrenador);

        if (entrenador != null) {
            builder.setTitle("Editar Entrenador");
            etNombre.setText(entrenador.getNombre_entrenador());
            etEspecializacion.setText(entrenador.getEspecializacion());
        } else {
            builder.setTitle("Añadir Entrenador");
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String especializacion = etEspecializacion.getText().toString().trim();

            if (nombre.isEmpty() || especializacion.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (entrenador != null) { // Editando
                editarOAgregarEntrenadorBackend(nombre, especializacion, entrenador.getIdEntrenador());
            } else { // Creando
                editarOAgregarEntrenadorBackend(nombre, especializacion, -1);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void editarOAgregarEntrenadorBackend(String nombre, String especializacion, int id) {
        String url;
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre_entrenador", nombre)); // Asegúrate que las llaves coinciden con tu backend
        params.add(new Parametro("especializacion", especializacion));

        if (id != -1) { // Editando (PUT)
            url = getResources().getString(R.string.url) + "entrenadores/" + id;
            executor.execute(() -> {
                String resultado = Internetop.getInstance().putText(url, params);
                handler.post(() -> {
                    if (resultado != null && !resultado.toLowerCase().contains("error")) {
                        Toast.makeText(this, "Entrenador actualizado", Toast.LENGTH_SHORT).show();
                        refreshEntrenadoresList();
                    } else {
                        Toast.makeText(this, "Error al actualizar entrenador", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else { // Creando (POST)
            url = getResources().getString(R.string.url) + "entrenadores";
            executor.execute(() -> {
                String resultado = Internetop.getInstance().postText(url, params);
                handler.post(() -> {
                    if (resultado != null && !resultado.toLowerCase().contains("error")) {
                        Toast.makeText(this, "Entrenador añadido", Toast.LENGTH_SHORT).show();
                        refreshEntrenadoresList();
                    } else {
                        Toast.makeText(this, "Error al añadir entrenador", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void eliminarEntrenadorBackend(int idEntrenador) {
        String url = getResources().getString(R.string.url) + "entrenadores/" + idEntrenador;
        executor.execute(() -> {
            String resultado = Internetop.getInstance().deleteTask(url);
            handler.post(() -> {
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    Toast.makeText(this, "Entrenador eliminado", Toast.LENGTH_SHORT).show();
                    refreshEntrenadoresList();
                } else {
                    Toast.makeText(this, "Error al eliminar. ¿Está asignado a alguna clase?", Toast.LENGTH_LONG).show();
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