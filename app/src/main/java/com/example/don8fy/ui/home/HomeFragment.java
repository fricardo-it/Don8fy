package com.example.don8fy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.don8fy.R;
import com.example.don8fy.ui.item.ImageListAdapter;
import com.example.don8fy.ui.item.ItemModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ImageListAdapter(getContext(), new ArrayList<ItemModel>());
        recyclerView.setAdapter(adapter);

        ImageView searchImageView = root.findViewById(R.id.imageViewSearch);
        EditText searchText = root.findViewById(R.id.txtSearch);
        ImageView filterImageView = root.findViewById(R.id.filter);

        return root;
    }
}
