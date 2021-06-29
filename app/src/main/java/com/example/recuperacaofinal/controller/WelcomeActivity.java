package com.example.recuperacaofinal.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.recuperacaofinal.R;

public class WelcomeActivity extends AppCompatActivity {

    Button gotoTakePicture, gotoGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        gotoTakePicture = findViewById(R.id.buttonWelcomePhoto);
        gotoGallery = findViewById(R.id.buttonWelcomeGallery);

        gotoTakePicture.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
            startActivity(intent);
        });

        gotoGallery.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
        });
    }
}