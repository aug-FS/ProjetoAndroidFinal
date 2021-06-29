package com.example.recuperacaofinal.model;

public class UserImage {
    String user;
    String image;

    public UserImage(String user, String image) {
        this.user = user;
        this.image = image;
    }

    public UserImage() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
