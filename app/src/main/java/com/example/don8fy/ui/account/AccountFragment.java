package com.example.don8fy.ui.account;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.don8fy.LoginPage;
import com.example.don8fy.R;
import com.example.don8fy.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    EditText nameUser,  passUser;
    TextView emailUser;
    Button editUser, deleteUser, back;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel slideshowViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        nameUser = root.findViewById(R.id.nametxt);
        emailUser = root.findViewById(R.id.emailtxt);
        passUser = root.findViewById(R.id.passwordtxt);

        editUser = root.findViewById(R.id.edit);
        deleteUser = root.findViewById(R.id.delete);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        String userName = prefs.getString("name", "");
        String usermail = prefs.getString("email", "");
        String userpassword = prefs.getString("password", "");

        nameUser.setText(userName);
        emailUser.setText(usermail);
        passUser.setText(userpassword);

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount();
            }
        });

//        final TextView textView = binding.textAccount;
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    private void updateUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            String newName = nameUser.getText().toString();
            String newPassword = passUser.getText().toString();

            if(!TextUtils.isEmpty(newName)){
                usersRef.child(user.getUid()).child("name").setValue(newName);
            }

            if(!TextUtils.isEmpty(newPassword)){
                user.updatePassword(newPassword);
                usersRef.child(user.getUid()).child("password").setValue(newPassword);
            }

            SharedPreferences.Editor editor = requireActivity().getSharedPreferences("UserData", MODE_PRIVATE).edit();
            editor.putString("name", newName);
            editor.putString("password", newPassword);
            editor.apply();

            Toast.makeText(requireContext(), "User Updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Delete also from the database
                        usersRef.child(user.getUid()).removeValue();
                        Toast.makeText(requireContext(), "User account deleted", Toast.LENGTH_SHORT).show();

                        // Start the login activity after deleting the user
                        Intent intent = new Intent(requireContext(), LoginPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete the user", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}