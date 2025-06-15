package com.example.massfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.massfitness.adaptadores.ClaseAdapter;
import com.example.massfitness.entidades.Clase;
import com.example.massfitness.entidades.Entrenador;
import com.example.massfitness.util.Internetop;
import com.example.massfitness.util.Parametro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*public class EditClasesActivity extends AppCompatActivity implements ClaseAdapter.OnClaseActionClickListener {

    private ImageView ivBack;
    private ClaseAdapter claseAdapter;
    private List<Clase> listClases = new ArrayList<>();
    private RecyclerView recyclerClases;
    private ExecutorService executor;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clases);

        ivBack = findViewById(R.id.ivBack);
        recyclerClases = findViewById(R.id.recyclerClases);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setupRecyclerView();

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));
        claseAdapter = new ClaseAdapter(listClases);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

*//*        obtenerClases(new EditClasesActivity.Callback<List<Clase>>() {
            @Override
            public void onSuccess(List<Clase> clases) {
                claseAdapter = new ClaseAdapter(clases);
                recyclerClases.setAdapter(claseAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditClasesActivity.this, "Error al cargar clases", Toast.LENGTH_SHORT).show();
            }
        });*//*
        refreshClasesList();
    }
    private void setupRecyclerView() {
        claseAdapter = new ClaseAdapter(listClases, this);
        recyclerClases.setAdapter(claseAdapter);
    }
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    // Método unificado para refrescar la lista desde el backend
    private void refreshClasesList() {
        obtenerClases(new EditClasesActivity.Callback<List<Clase>>() {
            @Override
            public void onSuccess(List<Clase> clases) {
                // Actualiza la lista existente en el adapter
                listClases.clear();
                listClases.addAll(clases);
                setupRecyclerView();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EditClasesActivity", "Error al refrescar clases", e);
                Toast.makeText(EditClasesActivity.this, "Error al cargar clases: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onEditClick(Clase clase, int position) {
        mostrarDialogoEditar(clase);
    }

    @Override
    public void onDeleteClick(Clase clase, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Clase")
                .setMessage("¿Estás seguro de que quieres eliminar la clase \"" + clase.getNombre() + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarClaseBackend(clase.getIdClase(), new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(EditClasesActivity.this, "Clase eliminada correctamente", Toast.LENGTH_SHORT).show();
                            // Refresca la lista desde el servidor para asegurar consistencia
                            refreshClasesList();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("EditClasesActivity", "Error al eliminar clase (callback)", e);
                            Toast.makeText(EditClasesActivity.this, "Error al eliminar clase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // --- Métodos para interactuar con el Backend ---

    public void eliminarClaseBackend(int idClase, Callback<Void> callback) {
        String url = getResources().getString(R.string.url) + "clases/" + idClase;
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.deleteTask(url);

            handler.post(() -> {
                // Mejor validación (considera chequear códigos HTTP si Internetop lo permite)
                if (resultado != null && (resultado.toLowerCase().contains("error") || resultado.toLowerCase().contains("exception"))) {
                    callback.onFailure(new Exception("Error del servidor: " + resultado));
                } else if (resultado == null) {
                    callback.onFailure(new Exception("Respuesta nula del servidor al eliminar"));
                } else {
                    // Asumiendo éxito si no hay error explícito y la respuesta no es nula
                    callback.onSuccess(null);
                }
            });
        });
    }

    public void mostrarDialogoEditar(Clase clase) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        // Asegúrate que R.layout.dialogo_editar_clase existe y tiene los IDs correctos
        View view = inflater.inflate(R.layout.dialogo_editar_clase, null);
        builder.setView(view);

        // Asegúrate que estos IDs existan en dialogo_editar_clase.xml
        EditText etNombre = view.findViewById(R.id.etNombreClase);
        EditText etCapacidad = view.findViewById(R.id.etCapacidadClase);

        etNombre.setText(clase.getNombre());
        etCapacidad.setText(String.valueOf(clase.getCapacidad_maxima()));

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevaCapacidadStr = etCapacidad.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevaCapacidadStr.isEmpty()) {
                Toast.makeText(this, "Nombre y capacidad no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int nuevaCapacidad = Integer.parseInt(nuevaCapacidadStr);
                if (nuevaCapacidad <= 0) {
                    Toast.makeText(this, "La capacidad debe ser un número positivo", Toast.LENGTH_SHORT).show();
                    return;
                }

                Clase claseEditada = new Clase();
                claseEditada.setIdClase(clase.getIdClase());
                claseEditada.setNombre(nuevoNombre);
                claseEditada.setCapacidad_maxima(nuevaCapacidad);
                claseEditada.setEntrenador(clase.getEntrenador());

                editarClaseBackend(claseEditada, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(EditClasesActivity.this, "Clase actualizada correctamente", Toast.LENGTH_SHORT).show();
                        // Refresca la lista desde el servidor
                        refreshClasesList();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("EditClasesActivity", "Error al editar clase (callback)", e);
                        Toast.makeText(EditClasesActivity.this, "Error al actualizar clase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "La capacidad debe ser un número válido", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    public void editarClaseBackend(Clase clase, Callback<Void> callback) {
        String url = getResources().getString(R.string.url) + "clases/" + clase.getIdClase();
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();

            List<Parametro> parametrosParaPut = new ArrayList<>();

            try {
                parametrosParaPut.add(new Parametro("nombre", clase.getNombre()));
                parametrosParaPut.add(new Parametro("capacidad_maxima", String.valueOf(clase.getCapacidad_maxima())));
                parametrosParaPut.add(new Parametro("entrenador_id", ""));
                if (clase.getEntrenador() != null) {
                    // Usa la clave que tu backend espere para el ID del entrenador en una actualización PUT
                    parametrosParaPut.add(new Parametro("entrenador_id", String.valueOf(clase.getEntrenador().getIdEntrenador())));
                    // ¡¡¡ NO PUEDES hacer esto con el putText actual: !!!
                    // JSONObject entrenadorRef = new JSONObject();
                    // entrenadorRef.put("idEntrenador", clase.getEntrenador().getIdEntrenador());
                    // parametrosParaPut.add(new Parametro("entrenador", entrenadorRef.toString()));
                    // Porque resultaría en: {"entrenador": "{\"idEntrenador\":123}"} (JSON como string)
                } else {
                    // Decide si necesitas enviar algo cuando no hay entrenador.
                    // Ejemplo: parametrosParaPut.add(new Parametro("idEntrenador", "0"));
                    parametrosParaPut.add(new Parametro("entrenador_id", ""));
                }

                Log.d("EditClasesActivity", "Enviando PUT a: " + url);
                StringBuilder paramsLog = new StringBuilder();
                for(Parametro p : parametrosParaPut) {
                    paramsLog.append(p.getValor()).append("=").append(p.getValor()).append("&");
                }
                Log.d("EditClasesActivity", "Parámetros: " + paramsLog.toString());


                String resultado = interopera.putText(url, parametrosParaPut);

                handler.post(() -> {
                    if (resultado != null && (resultado.toLowerCase().startsWith("error.") || resultado.toLowerCase().contains("exception"))) {
                        Log.e("EditClasesActivity", "Error del servidor al editar: " + resultado);
                        callback.onFailure(new Exception("Error del servidor: " + resultado));
                    } else if (resultado == null) {
                        Log.e("EditClasesActivity", "Respuesta nula del servidor al editar");
                        callback.onFailure(new Exception("Respuesta nula del servidor al editar"));
                    } else {
                        Log.d("EditClasesActivity", "Clase editada con éxito. Respuesta: " + resultado);
                        callback.onSuccess(null);
                    }
                });

            } catch (Exception e) {
                Log.e("EditClasesActivity", "Error preparando parámetros para editar", e);
                handler.post(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el executor
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // Método para eliminar una clase
*//*    public void eliminarClase(int idClase, Callback<Void> callback) {
        String url = getResources().getString(R.string.url) + "clases/" + idClase;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.deleteTask(url);

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    callback.onFailure(new Exception(resultado));
                } else {
                    callback.onSuccess(null);
                    updateRecyclerView(); // Actualizar lista tras eliminar
                    Toast.makeText(this, "Clase eliminada correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }*//*

    // Método para editar una clase
*//*    public void editarClase(Clase clase, Callback<Void> callback) {
        String url = getResources().getString(R.string.url) + "clases/" + clase.getIdClase();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();

            JSONObject claseJson = new JSONObject();
            try {
                claseJson.put("idClase", clase.getIdClase());
                claseJson.put("nombre", clase.getNombre());
                claseJson.put("capacidad_maxima", clase.getCapacidad_maxima());

                JSONObject entrenadorJson = new JSONObject();
                entrenadorJson.put("idEntrenador", clase.getEntrenador().getIdEntrenador());
                entrenadorJson.put("nombre_entrenador", clase.getEntrenador().getNombre_entrenador());
                entrenadorJson.put("especializacion", clase.getEntrenador().getEspecializacion());

                claseJson.put("entrenador", entrenadorJson);
            } catch (JSONException e) {
                handler.post(() -> callback.onFailure(e));
                return;
            }

            String resultado = interopera.putText(url, claseJson.toString());

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    callback.onFailure(new Exception(resultado));
                } else {
                    callback.onSuccess(null);
                    updateRecyclerView(); // Actualizar lista tras editar
                    Toast.makeText(this, "Clase editada correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }*//*

*//*    public void mostrarDialogoEditar(Clase clase) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialogo_editar_clase, null);
        builder.setView(view);

        EditText etNombre = view.findViewById(R.id.etNombreClase);
        EditText etCapacidad = view.findViewById(R.id.etCapacidad);
        etNombre.setText(clase.getNombre());
        etCapacidad.setText(String.valueOf(clase.getCapacidad_maxima()));

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = etNombre.getText().toString();
            int nuevaCapacidad = Integer.parseInt(etCapacidad.getText().toString());

            // Llamar al backend para actualizar
            actualizarClase(clase.getIdClase(), nuevoNombre, nuevaCapacidad);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void actualizarClase(int idClase, String nombre, int capacidad) {
        String url = getResources().getString(R.string.url) + "clases/" + idClase;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            JSONObject json = new JSONObject();
            try {
                json.put("nombre", nombre);
                json.put("capacidad_maxima", capacidad);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String respuesta = Internetop.getInstance().putText(url, json);
            runOnUiThread(() -> {
                if (respuesta.contains("success") || respuesta.isEmpty()) {
                    Toast.makeText(this, "Clase actualizada", Toast.LENGTH_SHORT).show();
                    updateRecyclerView();
                } else {
                    Toast.makeText(this, "Error al actualizar clase", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void eliminarClase(int idClase) {
        String url = getResources().getString(R.string.url) + "clases/" + idClase;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String respuesta = Internetop.getInstance().deleteTask(url);
            runOnUiThread(() -> {
                if (respuesta.contains("success") || respuesta.isEmpty()) {
                    Toast.makeText(this, "Clase eliminada", Toast.LENGTH_SHORT).show();
                    updateRecyclerView();
                } else {
                    Toast.makeText(this, "Error al eliminar clase", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }*//*

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
}*/





public class EditClasesActivity extends AppCompatActivity implements ClaseAdapter.OnClaseActionClickListener {

    private ImageView ivBack;
    private ClaseAdapter claseAdapter;
    private List<Clase> listClases = new ArrayList<>();
    private RecyclerView recyclerClases;
    private ExecutorService executor;
    private Handler handler;
    private List<Entrenador> listaDeTodosLosEntrenadores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clases); // Asegúrate de tener el FAB 'fabAddClase'

        ivBack = findViewById(R.id.ivBack);
        recyclerClases = findViewById(R.id.recyclerClases);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        setupRecyclerView();

        ivBack.setOnClickListener(v -> finish());

        cargarTodosLosEntrenadores(); // Primero cargamos los entrenadores para el spinner
    }

    private void setupRecyclerView() {
        recyclerClases.setLayoutManager(new LinearLayoutManager(this));
        claseAdapter = new ClaseAdapter(listClases, this);
        recyclerClases.setAdapter(claseAdapter);
    }

    // Tu método onDeleteClick ya está bien.

    @Override
    public void onEditClick(Clase clase, int position) {
        mostrarDialogoEditarOAgregarClase(clase);
    }

    private void cargarTodosLosEntrenadores() {
        String urlEntrenadores = getResources().getString(R.string.url) + "entrenadores";
        executor.execute(() -> {
            String resultado = Internetop.getInstance().getText(urlEntrenadores, new ArrayList<>());
            handler.post(() -> {
                if (resultado != null && !resultado.startsWith("Error") && !resultado.equals("false")) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultado);
                        listaDeTodosLosEntrenadores.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            listaDeTodosLosEntrenadores.add(new Entrenador(
                                    obj.getInt("idEntrenador"),
                                    obj.getString("nombre_entrenador"),
                                    obj.getString("especializacion")
                            ));
                        }
                        // Una vez cargados los entrenadores, cargamos las clases
                        refreshClasesList();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al procesar entrenadores", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error al cargar entrenadores para el diálogo", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    private void refreshClasesList() {
        // Tu método obtenerClases() se queda igual, pero lo llamamos desde aquí
        obtenerClases(new EditClasesActivity.Callback<List<Clase>>() {
            @Override
            public void onSuccess(List<Clase> clases) {
                listClases.clear();
                listClases.addAll(clases);
                claseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditClasesActivity.this, "Error al cargar clases: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void mostrarDialogoEditarOAgregarClase(final Clase clase) {
        if (listaDeTodosLosEntrenadores.isEmpty()) {
            Toast.makeText(this, "Aún no se han cargado los entrenadores. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_editar_clase, null);
        builder.setView(view);

        final EditText etNombre = view.findViewById(R.id.etNombreClase);
        final EditText etCapacidad = view.findViewById(R.id.etCapacidadClase);
        final Spinner spinnerEntrenadores = view.findViewById(R.id.spinnerEntrenadores);

        ArrayAdapter<Entrenador> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDeTodosLosEntrenadores);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntrenadores.setAdapter(spinnerAdapter);

        if (clase != null) { // Editando
            builder.setTitle("Editar Clase");
            etNombre.setText(clase.getNombre());
            etCapacidad.setText(String.valueOf(clase.getCapacidad_maxima()));
            if (clase.getEntrenador() != null) {
                for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                    if (spinnerAdapter.getItem(i).getIdEntrenador() == clase.getEntrenador().getIdEntrenador()) {
                        spinnerEntrenadores.setSelection(i);
                        break;
                    }
                }
            }
        } else { // Creando
            builder.setTitle("Añadir Clase");
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String capacidadStr = etCapacidad.getText().toString().trim();
            Entrenador entrenadorSeleccionado = (Entrenador) spinnerEntrenadores.getSelectedItem();

            if (nombre.isEmpty() || capacidadStr.isEmpty() || entrenadorSeleccionado == null) {
                Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacidad = Integer.parseInt(capacidadStr);

            if (clase != null) { // Editando
                editarClaseBackend(clase.getIdClase(), nombre, capacidad, entrenadorSeleccionado);
            } else { // Creando
                agregarClaseBackend(nombre, capacidad, entrenadorSeleccionado);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void agregarClaseBackend(String nombre, int capacidad, Entrenador entrenador) {
        String url = getResources().getString(R.string.url) + "clases";
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("capacidadMaxima", String.valueOf(capacidad)));

        // El backend espera el objeto entrenador completo
        try {
            JSONObject entrenadorJson = new JSONObject();
            entrenadorJson.put("idEntrenador", entrenador.getIdEntrenador());
            params.add(new Parametro("entrenador", entrenadorJson.toString())); // Ojo aquí, si Internetop no lo maneja bien, hay que ajustar
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        executor.execute(() -> {
            String resultado = Internetop.getInstance().postText(url, params);
            handler.post(() -> {
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    Toast.makeText(this, "Clase añadida", Toast.LENGTH_SHORT).show();
                    refreshClasesList();
                } else {
                    Toast.makeText(this, "Error al añadir clase", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

   /* private void editarClaseBackend(int idClase, String nombre, int capacidad, Entrenador entrenador) {
        // Tu backend de edición espera parámetros de formulario, no un JSON.
        // El método que ya tenías está bien para esto, solo tenemos que llamarlo.
        String url = getResources().getString(R.string.url) + "clases/" + idClase;
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("capacidad_maxima", String.valueOf(capacidad)));
        params.add(new Parametro("entrenador_id", String.valueOf(entrenador.getIdEntrenador())));

        executor.execute(() -> {
            // El método putText de Internetop crea un JSON, pero tu backend de PUT usa @RequestParam.
            // Necesitamos un método que envíe como 'form-urlencoded' o ajustar el backend para que acepte JSON en el PUT.
            // Asumamos por ahora que tu Internetop.putText funciona bien y el backend lo interpreta. Si no, este es el punto a revisar.
            String resultado = Internetop.getInstance().putText(url, params);
            handler.post(() -> {
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    Toast.makeText(this, "Clase actualizada", Toast.LENGTH_SHORT).show();
                    refreshClasesList();
                } else {
                    Toast.makeText(this, "Error al actualizar clase", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }*/

    private void editarClaseBackend(int idClase, String nombre, int capacidad, Entrenador entrenador) {
        String url = getResources().getString(R.string.url) + "clases/" + idClase;

        // --- CONSTRUIMOS EL JSON COMPLETO, IGUAL QUE EN 'AÑADIR' ---
        JSONObject claseJson = new JSONObject();
        try {
            claseJson.put("nombre", nombre);
            claseJson.put("capacidad_maxima", capacidad);

            // Si el entrenador no es nulo, lo añadimos al JSON
            if (entrenador != null) {
                JSONObject entrenadorJsonAnidado = new JSONObject();
                entrenadorJsonAnidado.put("idEntrenador", entrenador.getIdEntrenador());
                claseJson.put("entrenador", entrenadorJsonAnidado);
            } else {
                // Si es nulo, lo indicamos explícitamente para que el backend lo sepa
                claseJson.put("entrenador", JSONObject.NULL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear la petición de edición.", Toast.LENGTH_SHORT).show();
            return;
        }

        String jsonParaEnviar = claseJson.toString();

        executor.execute(() -> {
            // Necesitamos un método 'putJsonString' en Internetop, similar a 'postJsonString'
            String resultado = Internetop.getInstance().putJsonString(url, jsonParaEnviar);

            handler.post(() -> {
                if (resultado != null && !resultado.toLowerCase().contains("error")) {
                    Toast.makeText(this, "Clase actualizada con éxito.", Toast.LENGTH_SHORT).show();
                    refreshClasesList(); // Refrescar la lista para ver el cambio
                } else {
                    Toast.makeText(this, "Error al actualizar la clase: " + resultado, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onDeleteClick(Clase clase, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Clase")
                .setMessage("¿Estás seguro de que quieres eliminar la clase \"" + clase.getNombre() + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarClaseBackend(clase.getIdClase(), new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(EditClasesActivity.this, "Clase eliminada correctamente", Toast.LENGTH_SHORT).show();
                            // Refresca la lista desde el servidor para asegurar consistencia
                            refreshClasesList();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("EditClasesActivity", "Error al eliminar clase (callback)", e);
                            Toast.makeText(EditClasesActivity.this, "Error al eliminar clase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // --- Métodos para interactuar con el Backend ---

    public void eliminarClaseBackend(int idClase, Callback<Void> callback) {
        String url = getResources().getString(R.string.url) + "clases/" + idClase;
        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.deleteTask(url);

            handler.post(() -> {
                // Mejor validación (considera chequear códigos HTTP si Internetop lo permite)
                if (resultado != null && (resultado.toLowerCase().contains("error") || resultado.toLowerCase().contains("exception"))) {
                    callback.onFailure(new Exception("Error del servidor: " + resultado));
                } else if (resultado == null) {
                    callback.onFailure(new Exception("Respuesta nula del servidor al eliminar"));
                } else {
                    // Asumiendo éxito si no hay error explícito y la respuesta no es nula
                    callback.onSuccess(null);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el executor
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}