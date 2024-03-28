package com.example.don8fy.ui.item;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemModel {
    private String itemId;
    private String name;
    private String description;
    private String imageUri;

    public ItemModel() {
        super();
    }
    public ItemModel(String itemId, String name, String description, String imageUri) {
        super();
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
    }

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

    public void saveToFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");

        if (itemId != null && !itemId.isEmpty()) {
            databaseReference.child(itemId).setValue(this);
        } else {
            String newItemId = databaseReference.push().getKey();
            this.itemId = newItemId;
            databaseReference.child(newItemId).setValue(this);
        }
    }

    public static void getItemById(String itemId, final ValueEventListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 ItemModel item = dataSnapshot.getValue(ItemModel.class);
                listener.onDataChange(item);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onDataChange(null);
            }
        });
    }
}