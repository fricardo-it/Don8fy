package com.example.don8fy.ui.favorites;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.example.don8fy.ui.account.UserModel;
import com.example.don8fy.ui.item.ImageListAdapter;
import com.example.don8fy.ui.item.ItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteListAdapter.OnFavoriteClickListener {

    private FavoriteListAdapter adapter;
    private List<ItemModel> favoriteItemList;
    private UserModel currentUser;
    View rootView;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = rootView.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteItemList = new ArrayList<>();
        adapter = new FavoriteListAdapter(getActivity(), favoriteItemList);
        recyclerView.setAdapter(adapter);

        // Set the click listener for the adapter
        adapter.setOnFavoriteClickListener(this);

        // Initialize currentUser
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            currentUser = mainActivity.getCurrentUser();
        }

        loadFavoriteItems(); // Call loadFavoriteItems() after currentUser initialization

        return rootView;
    }


    @Override
    public void onFavoriteClick(ItemModel item, int position) {
        // Abrir a tela de detalhes do item quando um item favorito for clicado
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putString("name", item.getName());
        bundle.putString("description", item.getDescription());
        bundle.putString("url", item.getImageUri());
        bundle.putString("itemId", item.getItemId());
        bundle.putString("positionMap", item.getPositionMap());
        navController.navigate(R.id.nav_detail_item, bundle);
    }


    private void loadFavoriteItems() {
        favoriteItemList.clear();

        if (currentUser != null) {
            List<String> favoriteItems = currentUser.getFavoriteItems();

            for (String itemId : favoriteItems) {
                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("items").child(itemId);
                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ItemModel item = snapshot.getValue(ItemModel.class);
                            favoriteItemList.add(item);
                            adapter.notifyDataSetChanged();

                            TextView emptyFavoritesMessage = rootView.findViewById(R.id.emptyFavoritesMessage);
                            // Check if the favoriteItemList is empty
                            if (favoriteItemList.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyFavoritesMessage.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyFavoritesMessage.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

        }
    }
}
