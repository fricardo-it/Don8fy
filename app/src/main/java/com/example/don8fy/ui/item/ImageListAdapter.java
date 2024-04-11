package com.example.don8fy.ui.item;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.don8fy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private Context context;
    private List<ItemModel> arrayList;
    private OnItemClickListener listener;
    private DatabaseReference databaseReference;
    private List<String> favoriteItems;

    public ImageListAdapter(Context context, List<ItemModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.favoriteItems = new ArrayList<>();
        getItems(); // Carrega os itens ao criar o adaptador
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewProductName);
            imageView = itemView.findViewById(R.id.imageViewProduct);
            btnFavorite = itemView.findViewById(R.id.fav_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = arrayList.get(position);
        holder.title.setText(item.getName());
        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            Glide.with(context).load(item.getImageUri()).into(holder.imageView);
        } else {
            loadImageFromStorage(item, holder.imageView);
            Glide.with(context).load(R.drawable.ic_android).into(holder.imageView);
        }

        // Verifica se o item está na lista de favoritos e atualiza a imagem do botão de favoritos
        if (favoriteItems.contains(item.getItemId())) {
            holder.btnFavorite.setImageResource(R.drawable.ic_menu_favorites);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.ic_no_favorite);
        }

        final int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(arrayList.get(itemPosition));
                }
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel item = arrayList.get(itemPosition);

                // Verifica se o item está na lista de favoritos
                boolean isFavorite = favoriteItems.contains(item.getItemId());

                // Se o item já for um favorito, remova-o da lista de favoritos; caso contrário, adicione-o
                if (isFavorite) {
                    removeItemFromFavorites(item.getItemId());
                } else {
                    addItemToFavorites(item.getItemId());
                }

                // Atualiza a imagem do botão de favoritos com base no novo status do item favorito
                if (isFavorite) {
                    holder.btnFavorite.setImageResource(R.drawable.ic_no_favorite);
                } else {
                    holder.btnFavorite.setImageResource(R.drawable.ic_menu_favorites);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void loadImageFromStorage(ItemModel item, ImageView imageView) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("images/");
        StorageReference imageRef = storageReference.child((item.getImageUri()));
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to load image for item: " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }

    public void getItems() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemModel item = snapshot.getValue(ItemModel.class);
                    arrayList.add(item);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para verificar se um item está na lista de favoritos
    private boolean isItemFavorite(String itemId) {
        return favoriteItems.contains(itemId);
    }

    // Método para adicionar um item à lista de favoritos
    private void addItemToFavorites(String itemId) {
        favoriteItems.add(itemId);
    }

    // Método para remover um item da lista de favoritos
    private void removeItemFromFavorites(String itemId) {
        favoriteItems.remove(itemId);
    }
}
