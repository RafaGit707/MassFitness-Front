package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.massfitness.adaptadores.ReservaAdapter;
import com.example.massfitness.entidades.Reserva;
import com.example.massfitness.util.Internetop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MisReservasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReservaAdapter reservaAdapter;
    private ImageView ivBack;
    private List<Reserva> reservaList;
    private int idUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        recyclerView = findViewById(R.id.rvReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reservaAdapter = new ReservaAdapter(new ArrayList<>());
        recyclerView.setAdapter(reservaAdapter);

        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getUserIdAndReservas();
    }
    private void getUserIdAndReservas() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("correo_electronico", null);

        if (email == null) {
            showError("No hay usuario conectado.");
            return;
        }

        if (isNetworkAvailable()) {
            String url = getResources().getString(R.string.url) + "usuarios/correo/" + email;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Internetop interopera = Internetop.getInstance();
                String resultado = interopera.getText(url, new ArrayList<>());

                handler.post(() -> {
                    if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                        showError(resultado);
                    } else {
                        try {
                            JSONObject usuarioJson = new JSONObject(resultado);
                            idUsuario = usuarioJson.getInt("idUsuario");
                            fetchReservas(idUsuario);
                        } catch (Exception e) {
                            showError("Error al conectar con el servidor");
                        }
                    }
                });
            });
        } else {
            showError("No hay conexión a Internet.");
        }
    }
    private void fetchReservas(int userId) {
        String urlReservas = getResources().getString(R.string.url) + "reservas/usuario/" + userId;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.getText(urlReservas, new ArrayList<>());

            handler.post(() -> {
                try {
                    if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                        showError(resultado);
                    } else {
                        JSONArray jsonArray = new JSONArray(resultado);
                        parseReservas(jsonArray);
                    }
                } catch (Exception e) {
                    showError("Error al procesar la respuesta del servidor");
                }
            });
        });
    }
    private Timestamp parseDateTime(String dateTimeStr) throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss.S";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = simpleDateFormat.parse(dateTimeStr);
        return new Timestamp(date.getTime());
    }

    private void parseReservas(JSONArray jsonArray) {
        Log.d("JSON_RESPONSE", jsonArray.toString());

        if (jsonArray.length() == 0) {
            showError("No hay reservas para este usuario");
            return;
        }

        reservaList = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Reserva reserva = new Reserva();
                reserva.setIdReserva(jsonObject.getInt("idReserva"));
                reserva.setIdUsuario(jsonObject.getInt("usuario_id"));
                reserva.setEspacio_id(jsonObject.getInt("espacio_id"));
                String horarioReservaStr = jsonObject.getString("horario_reserva");
                Log.d("HORARIO_RESERVA", horarioReservaStr);
                String tipoSala =  jsonObject.getString("tipo_reserva");

                switch (tipoSala) {
                    case "BOXEO":
                        break;
                    case "PILATES":
                        break;
                    case "YOGA":
                        break;
                    case "MUSCULACIÓN":
                        break;
                    case "ABDOMINALES":
                        break;
                    default:
                        continue;
                }

                Timestamp horarioReserva;
                try {
                    horarioReserva = parseDateTime(horarioReservaStr);
                } catch (ParseException e) {
                    showError("Error al analizar la fecha y hora");
                    continue;
                }

                if (horarioReserva.after(now)) {
                    reserva.setHorarioReserva(horarioReserva);
                    reserva.setTipoReserva(jsonObject.getString("tipo_reserva"));
                    reserva.setEstadoReserva(jsonObject.getString("estado_reserva"));
                    reservaList.add(reserva);
                }
            }
            updateRecyclerView(reservaList);
        } catch (Exception e) {
            showError("Error al analizar las reservas");
        }
    }
    public void onCancelarReservaClick(View view) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(view);
        if (viewHolder != null) {
            int position = viewHolder.getAdapterPosition();
            Reserva reserva = reservaList.get(position);
            if (reserva != null) {
                TextView tvReservaId = findViewById(R.id.tvReservaId);
                TextView tvClassDetailsHorario = findViewById(R.id.tvClassDetailsHorario);
                TextView tvClassDetailsLugar = findViewById(R.id.tvClassDetailsLugar);

                String pattern = "yyyy-MM-dd HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                tvReservaId.setText(String.valueOf(reserva.getIdReserva()));
                tvClassDetailsHorario.setText(simpleDateFormat.format(reserva.getHorarioReserva()));
                tvClassDetailsLugar.setText(obtenerTipoReserva(reserva.getEspacio_id()));

                findViewById(R.id.confirmationDialog).setVisibility(View.VISIBLE);
            } else {
                showError("Error: No se pudo obtener la reserva.");
            }
        } else {
            showError("Error al obtener la reserva.");
        }

    }
    private String obtenerTipoReserva(int espacio) {
        switch (espacio) {
            case 1:
                return "Boxeo";
            case 2:
                return "Pilates";
            case 3:
                return "Sala de Musculación";
            case 4:
                return "Sala de Abdominales";
            case 5:
                return "Yoga";
            default:
                return "";
        }
    }
    public void onConfirmarClick(View view) {
        TextView tvReservaId = findViewById(R.id.tvReservaId);
        int idReserva = Integer.parseInt(tvReservaId.getText().toString());
        cancelarReserva(idReserva);
    }

    public void onCancelarClick(View view) {
        findViewById(R.id.confirmationDialog).setVisibility(View.GONE);
    }
    private void cancelarReserva(int idReserva) {
        String url = getResources().getString(R.string.url) + "reservas/eliminar/" + idReserva;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Internetop interopera = Internetop.getInstance();
            String resultado = interopera.deleteTask(url);

            handler.post(() -> {
                if (resultado.startsWith("Error") || resultado.startsWith("Exception")) {
                    showError("Error al cancelar la reserva");
                } else {
                    showSuccess("Reserva cancelada exitosamente");
                    fetchReservas(idUsuario);
                    findViewById(R.id.confirmationDialog).setVisibility(View.GONE);
                }
            });
        });
    }

    private void updateRecyclerView(List<Reserva> reservas) {
        reservaAdapter = new ReservaAdapter(reservas);
        recyclerView.setAdapter(reservaAdapter);
        reservaAdapter.notifyDataSetChanged();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network nw = connectivityManager.getActiveNetwork();
                if (nw == null) {
                    return false;
                }
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                return (actNw != null) && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
                return nwInfo != null && nwInfo.isConnected();
            }
        }
        return false;
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
