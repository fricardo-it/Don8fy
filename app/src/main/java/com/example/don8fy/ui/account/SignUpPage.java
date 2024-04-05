package com.example.don8fy.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.don8fy.ui.access.LoginPage;
import com.example.don8fy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        String name = txtname.getText().toString().trim();
        String email = txtemail.getText().toString().trim();
        String password = txtpassword.getText().toString().trim();
        String confirmPassword = txtconfirmpassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            txtpassword.setError("Password must be at least 8 characters long");
            txtpassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            txtconfirmpassword.setError("Password and confirmation do not match");
            txtconfirmpassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtemail.setError("Please enter a valid email address");
            txtemail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                UserModel user = new UserModel(name, email, password);

                                Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                                intent.putExtra("password", password);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUpPage.this, "Failed to get current user", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpPage.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
