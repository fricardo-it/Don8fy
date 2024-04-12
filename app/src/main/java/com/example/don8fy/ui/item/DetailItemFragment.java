package com.example.don8fy.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.example.don8fy.ui.access.LoginPage;
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

public class DetailItemFragment extends Fragment implements OnMapReadyCallback {

    ImageView productImage;
    TextView productName;
    TextView productDescription;

    Button editItem, removeItem;

    // References to Firebase
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("items");
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/");

    // Google Maps
    private GoogleMap map;
    public String positionMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_item, container, false);

        // Initialize views
        productName = rootView.findViewById(R.id.detailProductName);
        productDescription = rootView.findViewById(R.id.detailProductDescription);
        productImage = rootView.findViewById(R.id.imageProduct);
        editItem = rootView.findViewById(R.id.btnEdit);
        removeItem = rootView.findViewById(R.id.btnDelete);

        // Getting values from the arguments
        String name = getArguments().getString("name");
        String description = getArguments().getString("description");
        String imageUri = getArguments().getString("url");
        String itemId = getArguments().getString("itemId");
        positionMap = getArguments().getString("positionMap");

        // Setting values to views
        productName.setText(name);
        productDescription.setText(description);
        Glide.with(requireActivity()).load(imageUri).into(productImage);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

            // hide title
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            // Adiciona um botão personalizado
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_action_bar);

            // Configura o clique no botão personalizado
            View customActionBarView = actionBar.getCustomView();
            ImageButton customButton = customActionBarView.findViewById(R.id.custom_button);
            customButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
            });
        }

        // Initializing the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Inicia uma nova Intent para a MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Edit button click listener
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), EditItemPage.class);
                intent.putExtra("name", productName.getText().toString());
                intent.putExtra("description", productDescription.getText().toString());
                intent.putExtra("imageUrl", imageUri);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Remove button click listener
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove image from storage
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
                        Toast.makeText(requireContext(), "Image removed from storage", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to remove image from storage", Toast.LENGTH_SHORT).show();
                    }
                });

                // Remove item from database
                databaseRef.child(itemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), "Item removed from database", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to remove item from database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (positionMap != null) {
            float zoom = 16.0f;
            String[] latLng = positionMap.split(",");
            double latitude = Double.parseDouble(latLng[0]);
            double longitude = Double.parseDouble(latLng[1]);
            LatLng location = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(location).title("Object Location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        }
    }
}
