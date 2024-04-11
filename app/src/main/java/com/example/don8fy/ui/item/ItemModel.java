package com.example.don8fy.ui.item;

// Class for the item added
public class ItemModel {

    private String itemId, name, description, imageUri, positionMap;
    private Boolean isFavorite;

    // Constructor

    public ItemModel() {
    }

    public ItemModel(String itemId, String name, String description, String imageUri, String positionMap, Boolean isFavorite) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
        this.positionMap = positionMap;
    }

    // Getters and setters

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPositionMap() {
        return positionMap;
    }

    public void setPositionMap(String positionMap) {
        this.positionMap = positionMap;
    }

}
