package com.example.don8fy.ui.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ItemFragment extends Fragment {
    private EditText itemNameEditText;
    private EditText itemDescriptionEditText;
    private Button saveItemButton;

    private ItemViewModel itemViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item, container, false);

        itemNameEditText = rootView.findViewById(R.id.edit_text_item_name);
        itemDescriptionEditText = rootView.findViewById(R.id.edit_text_item_description);
        saveItemButton = rootView.findViewById(R.id.button_save_item);

        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemNameEditText.getText().toString();
                String itemDescription = itemDescriptionEditText.getText().toString();
                // Crie um novo item
                Item newItem = new Item(null, itemName, itemDescription);
                // Salve o item usando o ViewModel
                itemViewModel.addItem(newItem);
            }
        });

        return rootView;
    }
}
