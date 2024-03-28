package com.example.don8fy;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

        // Inicialize o FirebaseApp
        FirebaseApp.initializeApp(this);

        btnregister = findViewById(R.id.registerbtn);
        txtSignIn = findViewById(R.id.signinbtn);
        btneye = findViewById(R.id.eyebtn);
        btneye2 = findViewById(R.id.eyebtn2);

        mAuth = FirebaseAuth.getInstance();

        txtname = findViewById(R.id.fullnametxt);
        txtemail = findViewById(R.id.emailtxt);
        txtpassword = findViewById(R.id.passwordtxt);
        txtconfirmpassword = findViewById(R.id.confirmpasswordtxt);


        //configurate the toggle button to see the password
        btneye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPasswordVisible){
                    txtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btneye.setImageResource(R.drawable.eyeopen);
                }else{
                    txtpassword.setTransformationMethod(null);
                    btneye.setImageResource(R.drawable.eyeclosed);
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });

        btneye2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPasswordVisible){
                    txtconfirmpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btneye2.setImageResource(R.drawable.eyeopen);
                }else{
                    txtconfirmpassword.setTransformationMethod(null);
                    btneye2.setImageResource(R.drawable.eyeclosed);
                }
                isPasswordVisible = !isPasswordVisible;
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


        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All the field should be filled!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            txtpassword.setError("Password should be more than 8 char");
            txtpassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            txtconfirmpassword.setError("Password and confirmation is not match");
            txtconfirmpassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtemail.setError("Please Put the valid Email address");
            txtemail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpPage.this,
                                    "Register Success :) ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpPage.this,LoginPage.class);
                            intent.putExtra("email",email);
                            intent.putExtra("password",password);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignUpPage.this, "Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}