package com.example.don8fy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    String userName;

    boolean isPasswordVisible = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        btnSignIn = findViewById(R.id.loginbtn);
        txtSignUp = findViewById(R.id.signupbtn);
        btnEye = findViewById(R.id.eyebtn);
        email = findViewById(R.id.emailtxt);
        password = findViewById(R.id.passwordtxt);
        mAuth =  FirebaseAuth.getInstance();

        //Retrieve email and password from the signup page
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String usermail = extras.getString("email");
            String userpassword = extras.getString("password");

            email.setText(usermail);
            password.setText(userpassword);
        }

        //configurate the toggle button to see the password
        btnEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPasswordVisible){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnEye.setImageResource(R.drawable.eyeopen);
                }else{
                    password.setTransformationMethod(null);
                    btnEye.setImageResource(R.drawable.eyeclosed);
                }
                isPasswordVisible = !isPasswordVisible;
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
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(LoginPage.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserDataToSharedPreferences(userEmail, userPassword);
                    startActivity(new Intent(LoginPage.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserDataToSharedPreferences(String userEmail, String userPassword) {
        SharedPreferences.Editor editor = getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.putString("email", userEmail);
        editor.putString("password", userPassword);
        editor.apply();
    }

}