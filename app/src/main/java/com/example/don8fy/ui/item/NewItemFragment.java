package com.example.don8fy.ui.item;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class NewItemFragment extends Fragment implements OnMapReadyCallback {
    ImageView productImage;
    EditText name, description;
    Button uploadPhoto, saveItem, cancelRegisterItem;
    Uri imageUri;
    String itemPosition;


    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap locationMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_item, container, false);

        productImage = view.findViewById(R.id.imageProduct);
        name = view.findViewById(R.id.productName);
        description = view.findViewById(R.id.productDescription);
        uploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        saveItem = view.findViewById(R.id.btnSaveNewItem);
        cancelRegisterItem = view.findViewById(R.id.btnCancelNewItem);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLastLocation();


        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

            // hide title
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_action_bar);

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

        //request for camera runtime permission
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 100);
        }

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(productImage);
            }
        });

        cancelRegisterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Register canceled.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takePhoto();
                        break;
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 102);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;

                    SupportMapFragment mapFragment = SupportMapFragment.newInstance();

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

                    mapFragment.getMapAsync(NewItemFragment.this);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(requireContext(), "Location permission is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK && data != null){
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            productImage.setImageBitmap(bitmap);
//        }

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 100 && data != null) {
                // Imagem capturada pela câmera
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                productImage.setImageBitmap(bitmap);
            } else if (requestCode == 102 && data != null) {
                // Imagem selecionada da galeria
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                    productImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void uploadImage(ImageView image){
        //Get the Bitmap from the ImageView
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        if (drawable == null){
            Toast.makeText(requireContext(), "No Image Uploaded", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = drawable.getBitmap();

        //convert Bitmap into an Uri using a function
        imageUri = getImageUri(bitmap);

        //Upload the image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+UUID.randomUUID().toString());

        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Get the Url of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String itemName = name.getText().toString();
                        String itemDescription = description.getText().toString();
                        String uriImage = uri.toString();
                        uploadItem(itemName, itemDescription, uriImage, itemPosition);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(requireContext(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Fail on Upload Image", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadItem(String name, String description, String imageUrl, String map){

        //Initialize Firebase RealTime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("items");

        //upload the itemModel object to the Database
        String itemId = databaseRef.push().getKey(); //generate a unique ID for the item
        if (itemId != null){
            //instantiate the ItemModel class
            ItemModel itemModel = new ItemModel(itemId, name, description, imageUrl, map);
            databaseRef.child(itemId).setValue(itemModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(requireContext(), "New Item Saved!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Error: New Item NOT Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        locationMap = googleMap;
        float zoom = 16.0f;

        if (currentLocation != null) {
            LatLng mapPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            locationMap.addMarker(new MarkerOptions().position(mapPosition).title("Object Location"));
            locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPosition, zoom));

            itemPosition = Double.toString(mapPosition.latitude) + "," + Double.toString(mapPosition.longitude);
        } else {
            // Handle the case when currentLocation is null
            Toast.makeText(requireContext(), "Failed to get current location", Toast.LENGTH_SHORT).show();
        }
    }

}












//
//
//    private void uploadItemToDatabase(String name, String description, String imageUrl, String map){
//
//        //Initialize Firebase RealTime Database
//        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("items");
//
//        //upload the itemModel object to the Database
//        String itemId = databaseRef.push().getKey(); //generate a unique ID for the item
//        if (itemId != null){
//            //instantiate the ItemModel class
//            ItemModel itemModel = new ItemModel(itemId, name, description, imageUrl, map);
//            databaseRef.child(itemId).setValue(itemModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void unused) {
//                    Toast.makeText(requireContext(), "New Item Saved!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(requireContext(), MainActivity.class);
//                    startActivity(intent);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(requireContext(), "Error: New Item NOT Saved!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//    }

