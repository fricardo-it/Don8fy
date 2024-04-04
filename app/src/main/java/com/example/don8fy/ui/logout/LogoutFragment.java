package com.example.don8fy.ui.logout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.don8fy.LoginPage;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Fazer logout
        FirebaseAuth.getInstance().signOut();

        // Remover os dados do usuário do SharedPreferences
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.clear(); // Limpa todos os dados salvos
        editor.apply();

        // Redirecionar para a tela de login
        Intent intent = new Intent(getActivity(), LoginPage.class);
        startActivity(intent);
        requireActivity().finish(); // Encerra a atividade atual

        // Retornar null, pois não precisamos de uma view neste fragmento
        return null;
    }
}
