package com.example.don8fy.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.don8fy.R;
import com.example.don8fy.ui.access.LoginPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpPage extends AppCompatActivity {

    Button btnregister;
    TextView txtSignIn;
    ImageButton btneye, btneye2;
    EditText txtname, txtemail, txtpassword, txtconfirmpassword;
    private FirebaseAuth mAuth;

    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        mAuth = FirebaseAuth.getInstance();

        btnregister = findViewById(R.id.registerbtn);
        txtSignIn = findViewById(R.id.signinbtn);
        btneye = findViewById(R.id.eyebtn);
        btneye2 = findViewById(R.id.eyebtn2);

        txtname = findViewById(R.id.fullnametxt);
        txtemail = findViewById(R.id.emailtxt);
        txtpassword = findViewById(R.id.passwordtxt);
        txtconfirmpassword = findViewById(R.id.confirmpasswordtxt);

        btneye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                txtpassword.setTransformationMethod(isPasswordVisible ? null : PasswordTransformationMethod.getInstance());
                btneye.setImageResource(isPasswordVisible ? R.drawable.ic_hide : R.drawable.ic_show);
            }
        });

        btneye2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                txtconfirmpassword.setTransformationMethod(isPasswordVisible ? null : PasswordTransformationMethod.getInstance());
                btneye2.setImageResource(isPasswordVisible ? R.drawable.ic_hide : R.drawable.ic_show);
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        String userName = txtname.getText().toString().trim();
        String userEmail = txtemail.getText().toString().trim();
        String userPassword = txtpassword.getText().toString().trim();
        String userConfirmPassword = txtconfirmpassword.getText().toString().trim();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userConfirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 8) {
            txtpassword.setError("Password should be at least 8 characters long");
            txtpassword.requestFocus();
            return;
        }
        if (!userPassword.equals(userConfirmPassword)) {
            txtconfirmpassword.setError("Password and confirmation do not match");
            txtconfirmpassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            txtemail.setError("Please enter a valid email address");
            txtemail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .build();

                                user.updateProfile(profileUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Nome do usuário atualizado com sucesso
                                                    Toast.makeText(SignUpPage.this, "User profile updated.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            UserModel userModel = new UserModel(userName, userEmail, userPassword);
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            databaseRef.child(userId).setValue(userModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                                            intent.putExtra("password", userPassword);
                                            intent.putExtra("email", userEmail);
                                            intent.putExtra("name", userName);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpPage.this, "Failed to save new User", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(SignUpPage.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
