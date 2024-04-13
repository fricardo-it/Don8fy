package com.example.don8fy.ui.account;

import java.util.ArrayList;

public class UserModel {

    private String name, email, password;
    private ArrayList<String> favoriteItems;

    public UserModel(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.favoriteItems = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getFavoriteItems() {
        return favoriteItems;
    }

    public void setFavoriteItems(ArrayList<String> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    public void addFavoriteItem(String itemId) {
        favoriteItems.add(itemId);
    }

    public void removeFavoriteItem(String itemId) {
        favoriteItems.remove(itemId);
    }
}
