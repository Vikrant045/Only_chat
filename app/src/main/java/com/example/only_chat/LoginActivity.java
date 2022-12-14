package com.example.only_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView cna,signin_btn;
    EditText login_email,login_password;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cna = findViewById(R.id.txt_cna);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        signin_btn = findViewById(R.id.signin_btn);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(" please wait!..");
        progressDialog.setCancelable(false);

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String email = login_email.getText().toString();
                 String password = login_password.getText().toString();
                 if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
                     progressDialog.dismiss();

                     Toast.makeText(LoginActivity.this, "Fill the correct data", Toast.LENGTH_SHORT).show();
                 }
                 else if(!email.matches(emailPattern)){
                     progressDialog.dismiss();

                     login_email.setError("Invalid Email");
                     Toast.makeText(LoginActivity.this, " Invalid Email", Toast.LENGTH_SHORT).show();
                 }
                 else if(password.length()>6  ){
                     progressDialog.dismiss();

                     login_password.setError("Invalid password");

                     Toast.makeText(LoginActivity.this, "password should have only 6 characters", Toast.LENGTH_SHORT).show();

                 }
                 else{
                     auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful()){
                                 progressDialog.dismiss();

                                 startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                             }
                             else{
                                 progressDialog.dismiss();

                                 Toast.makeText(LoginActivity.this, "Error in LogIn", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });


                 }
            }
        });
        cna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();

                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }
}