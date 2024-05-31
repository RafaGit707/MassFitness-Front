package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntrenadoresActivity extends AppCompatActivity {

    private LinearLayout trainerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenadores);

        trainerContainer = findViewById(R.id.trainerContainer);

        String trainer = getIntent().getStringExtra("ENTRENADOR");

        if (trainer != null) {
            // Show specific trainer information
            showTrainerInfo(trainer);
        } else {
            // Show all trainers
            showAllTrainers();
        }
    }

    private void showTrainerInfo(String trainer) {
        if (trainer.equals("maikel")) {
            addTrainerView("MAIKEL", "LAST NAME", "Trainer description here", R.drawable.maikel_image);
        } // else if (trainer.equals("ana_id")) {
//            addTrainerView("ANA", "LAST NAME", "Trainer description here", R.drawable.ana_image);
//        }
    }

    private void showAllTrainers() {
        addTrainerView("MAIKEL", "LAST NAME", "Trainer description here", R.drawable.maikel_image);
    }

    private void addTrainerView(String firstName, String lastName, String description, int imageResId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout trainerLayout = (LinearLayout) inflater.inflate(R.layout.item_trainer, trainerContainer, false);

        ImageView ivTrainerImage = trainerLayout.findViewById(R.id.ivTrainerImage);
        TextView tvTrainerName = trainerLayout.findViewById(R.id.tvTrainerName);
        TextView tvTrainerDescription = trainerLayout.findViewById(R.id.tvTrainerDescription);

        ivTrainerImage.setImageResource(imageResId);
        tvTrainerName.setText(firstName + " " + lastName);
        tvTrainerDescription.setText(description);

        trainerContainer.addView(trainerLayout);
    }
}