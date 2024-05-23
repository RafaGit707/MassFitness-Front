package com.example.massfitness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    ImageView loginImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loginImageView = findViewById (R.id.logoImageView);
    }



}