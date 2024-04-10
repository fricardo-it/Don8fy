package com.example.don8fy.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailPage extends AppCompatActivity implements OnMapReadyCallback {

    ImageView productImage;
    TextView productName;
    TextView productDescription;

    Button editItem, removeItem;

    //references from the Firebase
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("items");
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/");

    //google maps
    private GoogleMap map;
    public String positionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        //getting values from the intent
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String imageUri = getIntent().getStringExtra("url");
        Log.d("imageUri", imageUri);
        String itemId = getIntent().getStringExtra("itemId");
        // Retrieve positionMap from intent
        positionMap = getIntent().getStringExtra("positionMap");
        Toast.makeText(DetailPage.this, "Position: "+positionMap, Toast.LENGTH_SHORT).show();
        productName = findViewById(R.id.detailProductName);
        productDescription = findViewById(R.id.detailProductDescription);
        productImage = findViewById(R.id.imageProduct);
        editItem = findViewById(R.id.btnEdit);
        removeItem = findViewById(R.id.btnDelete);

        productName.setText(name);
        productDescription.setText(description);

        // Initialize the map
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.maps, mapFragment).commit();

        mapFragment.getMapAsync(DetailPage.this);


        Glide.with(DetailPage.this).load(imageUri).into(productImage);

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPage.this, EditItemPage.class);

                //send the item informations to the EditPage
                intent.putExtra("name", productName.getText().toString());
                Log.d("intentName", "nameTodisplay: " + productName);
                intent.putExtra("description", productDescription.getText().toString());
                intent.putExtra("imageUrl", imageUri);
                intent.putExtra("itemId", itemId);

                startActivity(intent);
                finish();
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //delete the image from storage
                //use the download URL (imageUri) to create the full path for the image
                String[] parts = imageUri.split("\\?")[0].split("%2F");
                String imagePath = "";
                for(int i=0; i<parts.length-1; i++){
                    imagePath += parts[i]+"/";
                }
                imagePath = parts[parts.length-1];

                StorageReference imageRef = storageRef.child(imagePath);

                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailPage.this, "Thank you for the status update!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailPage.this, "Failed to remove the image from storage!", Toast.LENGTH_SHORT).show();
                    }
                });

                //remove the item from database
                databaseRef.child(itemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailPage.this, "Item already picked Up!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetailPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailPage.this, "Failed to remove the item!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DetailPage.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;

        if (positionMap != null) {
            Log.d("position", positionMap);
            float zoom = 12.0f;
            String[] latLng = positionMap.split(",");
            double latitude = Double.parseDouble(latLng[0]);
            double longitude = Double.parseDouble(latLng[1]);

            LatLng location = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(location).title("Object Location"));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        }

    }

}