package com.example.massfitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    MaterialButton sendButton;
    TextInputLayout emailTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendButton = findViewById(R.id.sendButton);
        emailTextField = findViewById(R.id.emailTextField);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_click_animation);
                sendButton.startAnimation(anim);
                validate();
            }
        });
    }

    public void validate() {
        String email = emailTextField.getEditText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextField.setError("Correo invalido");
            return;
        }
        //sendEmail(email);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity .class);
        startActivity(intent);
        finish();
    }

//    public void sendEmail(String email) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String emailAddress = email;
//        auth.sendPasswordResetEmail(emailAddress)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ForgotPasswordActivity.this, "Correo enviado!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(ForgotPasswordActivity.this, "Correo invalido", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

}