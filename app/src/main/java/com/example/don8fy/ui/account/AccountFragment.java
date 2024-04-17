package com.example.don8fy.ui.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.example.don8fy.databinding.FragmentAccountBinding;
import com.example.don8fy.ui.access.LoginPage;
import com.example.don8fy.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {
    public interface OnNameUpdateListener {
        void onNameUpdated(String newName);
    }
    private OnNameUpdateListener nameUpdateListener;

    private FragmentAccountBinding binding;

    EditText nameUser;
    TextView emailUser;
    Button editUser, deleteUser, changePassword;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editUser = root.findViewById(R.id.edit);
        deleteUser = root.findViewById(R.id.delete);
        changePassword = root.findViewById(R.id.changepassw);

        nameUser = root.findViewById(R.id.nametxt);
        emailUser = root.findViewById(R.id.emailtxt);

        // disable edit name btn
        editUser.setEnabled(false);

        nameUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enable btn
                editUser.setEnabled(true);
            }
        });
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

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("name").getValue(String.class);
                        String userEmail = snapshot.child("email").getValue(String.class);

                        nameUser.setText(userName);
                        emailUser.setText(userEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors here
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNameUpdateListener) {
            nameUpdateListener = (OnNameUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNameUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        nameUpdateListener = null;
    }

    private void updateUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String newName = nameUser.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(requireContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            usersRef.child(userId).child("name").setValue(newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "User name updated", Toast.LENGTH_SHORT).show();

                                if (nameUpdateListener != null) {
                                    nameUpdateListener.onNameUpdated(newName);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Failed to update user name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            editUser.setEnabled(false);
        }
    }

    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure you want to delete your account?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "User account deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(requireContext(), LoginPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete user account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Cancel Delete
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Password");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText oldPasswordEditText = dialogView.findViewById(R.id.old_password);
        EditText newPasswordEditText = dialogView.findViewById(R.id.new_password);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirm_password);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (validatePasswords(oldPassword, newPassword, confirmPassword)) {
                    reauthenticateUser(oldPassword, newPassword);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void reauthenticateUser(String oldPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updatePassword(newPassword);
                            } else {
                                Toast.makeText(requireContext(), "Failed to reauthenticate. Please check your current password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean validatePasswords(String oldPassword, String newPassword, String confirmPassword) {

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 8 || confirmPassword.length() < 8) {
            Toast.makeText(requireContext(), "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Password and confirmation do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}