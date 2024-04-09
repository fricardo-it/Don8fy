package com.example.don8fy.ui.access;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.don8fy.MainActivity;
import com.example.don8fy.R;
import com.example.don8fy.ui.account.SignUpPage;
import com.example.don8fy.ui.account.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {
    Button btnSignIn;
    TextView txtSignUp;
    EditText email, password;
    ImageButton btnEye;
    String userEmail, userPassword;
    boolean isPasswordVisible = false;

    private FirebaseAuth mAuth;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.loginbtn);
        txtSignUp = findViewById(R.id.signupbtn);
        btnEye = findViewById(R.id.eyebtn);
        email = findViewById(R.id.emailtxt);
        password = findViewById(R.id.passwordtxt);

        userEmail = getIntent().getStringExtra("email");
        userPassword = getIntent().getStringExtra("password");
        if (userEmail != null && userPassword != null) {
            email.setText(userEmail);
            password.setText(userPassword);
        }

        btnEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                password.setTransformationMethod(isPasswordVisible ? null : PasswordTransformationMethod.getInstance());
                btnEye.setImageResource(isPasswordVisible ? R.drawable.ic_hide : R.drawable.ic_show);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        userEmail = email.getText().toString().trim();
        userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
            Toast.makeText(LoginPage.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Recupere os dados do usuário do banco de dados
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String userName = snapshot.child("name").getValue(String.class);
                                    String userEmail = snapshot.child("email").getValue(String.class);
                                    String userPassword = snapshot.child("password").getValue(String.class);

                                    // Atualize as preferências compartilhadas com os dados do usuário
                                    SharedPreferences.Editor editor = getSharedPreferences("UserData", MODE_PRIVATE).edit();
                                    editor.putString("email", userEmail);
                                    editor.putString("name", userName);
                                    editor.putString("password", userPassword);
                                    editor.apply();

                                    startActivity(new Intent(LoginPage.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginPage.this, "User data not found in database", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginPage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}