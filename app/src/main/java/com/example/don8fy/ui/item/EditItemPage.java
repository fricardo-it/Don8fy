package com.example.don8fy.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditItemPage extends AppCompatActivity {

    EditText name, description;
    ImageView productImage;
    Button saveEditedItem, cancel;

    //references from the Firebase
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("items");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_page);

        name = findViewById(R.id.editProductName);
        description = findViewById(R.id.editProductDescription);
        productImage = findViewById(R.id.imageProduct);
        saveEditedItem = findViewById(R.id.btnSave);
        cancel = findViewById(R.id.btnCancel);

        //getting values from the intent
        String nameProduct = getIntent().getStringExtra("name");
        String descriptionProduct = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String itemId = getIntent().getStringExtra("itemId");

        name.setText(nameProduct);
        description.setText(descriptionProduct);
        //Load image from the Storage using the url received in the intent
        Glide.with(EditItemPage.this).load(imageUrl).into(productImage);

        saveEditedItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = name.getText().toString();
                String updatedDescription = description.getText().toString();

                if (updatedName.isEmpty() || updatedDescription.isEmpty()) {
                    Toast.makeText(EditItemPage.this, "Name and description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ensure nameProduct and descriptionProduct are not null
                if (nameProduct == null || descriptionProduct == null) {
                    Log.e("EditItemPage", "Name or description is null");
                    return;
                }
                //ItemModel item = getIntent().getParcelableExtra("item");
                if(!updatedName.equals(nameProduct) || !updatedDescription.equals(descriptionProduct)){
                    //get the key of the item. Using the link to the image as a key because it is unique
                    String itemKey = itemId;
                    if (itemKey == null) {
                        Log.e("EditItemPage", "Item key is null");
                        return;
                    }
                    // Ensure databaseRef is not null
                    if (databaseRef == null) {
                        Log.e("EditItemPage", "Database reference is null");
                        return;
                    }

                    databaseRef.child(itemKey).child("name").setValue(updatedName);
                    databaseRef.child(itemKey).child("description").setValue(updatedDescription);
                    Toast.makeText(EditItemPage.this, "Item Updated!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditItemPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(EditItemPage.this, "No changes to update", Toast.LENGTH_SHORT).show();

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText(nameProduct);
                description.setText(descriptionProduct);

                Intent intent = new Intent(EditItemPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}