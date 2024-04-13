package com.example.don8fy.ui.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.don8fy.R;
import com.example.don8fy.ui.item.ItemModel;

import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final Context context;
    private final List<ItemModel> favoriteItemList;

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
        // Configure o ViewHolder com os detalhes do item favorito
        // Por exemplo:
        holder.nameTextView.setText(item.getName());
        holder.descriptionTextView.setText(item.getDescription());
        // Configure outras visualizações conforme necessário
    }

    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        // Adicione outras visualizações conforme necessário

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textProductNameFavorites);
           // descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            // Inicialize outras visualizações conforme necessário
        }
    }
}
