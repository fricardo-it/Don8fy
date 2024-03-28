package com.example.don8fy.ui.item;

import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemViewModel extends ViewModel {

    // Método para salvar um novo item no Firebase Realtime Database
    public void addItem(ItemModel item) {
        item.saveToFirebase();
    }

    // Método para buscar um item pelo ID no Firebase Realtime Database
    public void getItemById(String itemId, ValueEventListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items").child(itemId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifique se o dataSnapshot é nulo ou não
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    // Converta o dataSnapshot em um objeto ItemModel
                    ItemModel item = dataSnapshot.getValue(ItemModel.class);
                    // Passe o objeto ItemModel para o ouvinte
                    listener.onDataChange(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Caso ocorra um erro, notifique o ouvinte
                listener.onDataChange(null);
            }
        });
    }
}
