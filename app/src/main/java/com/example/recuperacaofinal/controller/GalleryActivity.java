package com.example.recuperacaofinal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recuperacaofinal.GlideApp;
import com.example.recuperacaofinal.R;
import com.example.recuperacaofinal.model.UserImage;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

public class GalleryActivity extends AppCompatActivity {

    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirestoreRecyclerAdapter<UserImage, UserImagesViewHolder> adapter;

    Button addPhoto;
    RecyclerView recyclerViewGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        addPhoto = findViewById(R.id.buttonAddPhoto);
        recyclerViewGallery = findViewById(R.id.recyclerViewGallery);

        addPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
            startActivity(intent);
        });

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String user = firebaseUser.getUid();

        Query query = firebaseFirestore.collection("userImages").whereEqualTo("user", user);
        FirestoreRecyclerOptions<UserImage> options = new FirestoreRecyclerOptions.Builder<UserImage>().setQuery(query, UserImage.class).build();

        adapter = new FirestoreRecyclerAdapter<UserImage, UserImagesViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public UserImagesViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
                return new UserImagesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull GalleryActivity.UserImagesViewHolder holder, int position, @NonNull @NotNull UserImage model) {
                holder.user.setText(model.getImage());
                StorageReference pathReference = storageRef.child("images/" + model.getImage());
                GlideApp.with(GalleryActivity.this).load(pathReference).into(holder.image);
            }
        };

        recyclerViewGallery.setHasFixedSize(true);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewGallery.setAdapter(adapter);
        adapter.startListening();
    }

    private static class UserImagesViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        ImageView image;

        public UserImagesViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.textViewUser);
            image = itemView.findViewById(R.id.imageViewImage);
        }
    }
}