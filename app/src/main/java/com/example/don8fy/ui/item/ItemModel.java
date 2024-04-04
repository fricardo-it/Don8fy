package com.example.don8fy.ui.item;

//class for the item added
public class ItemModel {

    String itemId, name, description, imageUri, positionMap;


    //constructor

    public ItemModel() {
    }

    public ItemModel(String itemId, String name, String description, String imageUri, String positionMap) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
        this.positionMap = positionMap;
    }

    //getters and setters

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getPositionMap() {
        return positionMap;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setPositionMap(String positionMap) {
        this.positionMap = positionMap;
    }
}
