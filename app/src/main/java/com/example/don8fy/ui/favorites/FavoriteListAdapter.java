package com.example.don8fy.ui.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.don8fy.R;
import com.example.don8fy.ui.item.ItemModel;
import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final Context context;
    private final List<ItemModel> favoriteItemList;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(ItemModel item, int position);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }

    public FavoriteListAdapter(Context context, List<ItemModel> favoriteItemList) {
        this.context = context;
        this.favoriteItemList = favoriteItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = favoriteItemList.get(position);
        holder.nameTextView.setText(item.getName());

        // Carrega a imagem do item usando Glide
        Glide.with(context).load(item.getImageUri()).into(holder.itemImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteClickListener != null) {
                    // Use holder.getAdapterPosition() para obter a posição atual do item
                    int clickedPosition = holder.getAdapterPosition();
                    favoriteClickListener.onFavoriteClick(item, clickedPosition);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView itemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textProductNameFavorites);
            itemImageView = itemView.findViewById(R.id.imageProductFavorites);
        }
    }
}
