package com.example.recuperacaofinal.controller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recuperacaofinal.R;
import com.example.recuperacaofinal.model.UserImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class TakePhotoActivity extends AppCompatActivity {

    Button takePictureButton, savePictureButton;
    ImageView takePictureImageView;

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        takePictureButton = findViewById(R.id.buttonTakePicture);
        savePictureButton = findViewById(R.id.buttonSavePicture);
        takePictureImageView = findViewById(R.id.imageViewTakePicture);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        takePictureButton.setOnClickListener(v -> dispatchTakePictureIntent());
        savePictureButton.setOnClickListener(v -> uploadImage());
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            takePictureImageView.setImageBitmap(imageBitmap);
        }
    }

    private void uploadImage() {
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        takePictureImageView.setDrawingCacheEnabled(true);
        takePictureImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) takePictureImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String fileName = System.currentTimeMillis() + ".JPEG";

        StorageReference fileRef = storage
                .getReference()
                .child("images")
                .child(fileName);

        UploadTask uploadTask = fileRef.putBytes(data);

        uploadTask.addOnFailureListener(exception -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            assert firebaseUser != null;
            String uid = firebaseUser.getUid();
            UserImage userImage = new UserImage(uid, fileName);

            firebaseFirestore.collection("userImages").document().set(userImage).addOnSuccessListener(unused -> {
                Toast.makeText(this, "User image saved!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }).addOnFailureListener(e -> Toast.makeText(this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show());
            progressDialog.dismiss();
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
        });
    }
}