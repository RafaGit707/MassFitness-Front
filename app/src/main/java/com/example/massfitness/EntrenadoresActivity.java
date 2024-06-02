package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class EntrenadoresActivity extends AppCompatActivity {

    private LinearLayout trainerContainer;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenadores);

        trainerContainer = findViewById(R.id.trainerContainer);
        ivBack = findViewById(R.id.ivBack);

        String trainer = getIntent().getStringExtra("ENTRENADOR");

        if (trainer != null) {
            showTrainerInfo(trainer);
        } else {
            showAllTrainers();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showTrainerInfo(String trainer) {
        if (trainer.equals("Maikel")) {
            addTrainerView("MAIKEL", "JOHNSON RAMIREZ ", "Certificación en Entrenamiento de Boxeo, Curso de Primeros Auxilios, Licenciatura en Educación Física", R.drawable.maikel_image);
        } else if (trainer.equals("Jose")) {
            addTrainerView("JOSE", "GOMEZ GARCIA", "Licenciatura en Ciencias del Deporte, Certificación en Entrenamiento Personal, Certificación en Nutrición Deportiva", R.drawable.jose_image);
        } else if (trainer.equals("Laura")) {
            addTrainerView("LAURA", "MARTINEZ RODRIGUEZ", "Certificación Internacional en Yoga (200 horas), Curso de Meditación y Mindfulness, Taller de Yoga Terapéutico", R.drawable.laura_image);
        } else if (trainer.equals("John")) {
            addTrainerView("JOHN", "NOSE", "AAAAAAAAAA", R.drawable.john_image);
        }
    }

    private void showAllTrainers() {
        addTrainerView("MAIKEL", "JOHNSON RAMIREZ ", "Certificación en Entrenamiento de Boxeo, Curso de Primeros Auxilios, Licenciatura en Educación Física", R.drawable.maikel_image);
        addTrainerView("JOSE", "GOMEZ GARCIA", "Licenciatura en Ciencias del Deporte, Certificación en Entrenamiento Personal, Certificación en Nutrición Deportiva", R.drawable.jose_image);
        addTrainerView("LAURA", "MARTINEZ RODRIGUEZ", "Certificación Internacional en Yoga (200 horas), Curso de Meditación y Mindfulness, Taller de Yoga Terapéutico", R.drawable.laura_image);
        addTrainerView("JOHN", "NOSE", "AAAAAAAAAA", R.drawable.john_image);
    }

    private void addTrainerView(String firstName, String lastName, String description, int imageResId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout trainerLayout = (LinearLayout) inflater.inflate(R.layout.item_trainer, trainerContainer, false);

        ImageView ivTrainerImage = trainerLayout.findViewById(R.id.ivTrainerImage);
        TextView tvTrainerName = trainerLayout.findViewById(R.id.tvTrainerName);
        TextView tvTrainerDescription = trainerLayout.findViewById(R.id.tvTrainerDescription);

        Glide.with(this)
                .load(imageResId)
                .override(140, 140)
                .into(ivTrainerImage);

        tvTrainerName.setText(firstName + " " + lastName);
        tvTrainerDescription.setText(description);

        trainerContainer.addView(trainerLayout);
    }
}