package com.example.don8fy.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.example.don8fy.databinding.FragmentFavoritesBinding;
import com.example.don8fy.ui.account.UserModel;
import com.example.don8fy.ui.item.ItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;

    RecyclerView recyclerView;
    FavoriteListAdapter adapter;
    List<ItemModel> favoriteItemList;

    private UserModel currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtendo uma referência ao MainActivity a partir do contexto do fragmento
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            // Obtendo o usuário atual do MainActivity
            currentUser = mainActivity.getCurrentUser();
        }

        // Verificando se o usuário atual é nulo
        if (currentUser == null) {}
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = rootView.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteItemList = new ArrayList<>();
        adapter = new FavoriteListAdapter(getActivity(), favoriteItemList);
        recyclerView.setAdapter(adapter);
        loadFavoriteItems(); // Método para carregar os itens favoritos do usuário
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadFavoriteItems() {
        // Limpa a lista antes de carregar os favoritos
        favoriteItemList.clear();

        // Obtém a lista de IDs dos favoritos do usuário atual
        List<String> favoriteItems = currentUser.getFavoriteItems();

        // Itera sobre os IDs dos favoritos e busca os detalhes de cada item no banco de dados
        for (String itemId : favoriteItems) {
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("items").child(itemId);
            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Converte o snapshot em um objeto ItemModel e adiciona à lista de favoritos
                        ItemModel item = snapshot.getValue(ItemModel.class);
                        favoriteItemList.add(item);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Trate o erro de carregamento dos favoritos, se necessário
                }
            });
        }
    }

}