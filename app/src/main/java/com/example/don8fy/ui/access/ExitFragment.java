package com.example.don8fy.ui.access;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;

public class ExitFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FirebaseAuth.getInstance().signOut();

        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // Fecha completamente
        System.exit(0);

        return null;
    }
}
