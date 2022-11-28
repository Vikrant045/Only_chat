package com.example.only_chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
TextView txt_signIn,reg_signUp;
EditText _name,reg_password,reg_email,reg_confirmPassword;
CircleImageView profile_image;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri image_uri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
ProgressDialog progressDialog;
FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();


        txt_signIn = findViewById(R.id.txt_signIn);
        reg_signUp = findViewById(R.id.reg_singUp);
        _name = findViewById(R.id.reg_name);
        reg_password = findViewById(R.id.reg_password);
        reg_confirmPassword = findViewById(R.id.reg_confirmPassword);
        reg_email = findViewById(R.id.reg_email);
        profile_image = findViewById(R.id.profile_image);

         progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(" please wait!..");
        progressDialog.setCancelable(false);


        reg_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = _name.getText().toString();

                String email = reg_email.getText().toString();
                String password = reg_password.getText().toString();
                String confirm_password = reg_confirmPassword.getText().toString();
                String status = "hey i am using this application for chat";
                if(TextUtils.isEmpty(name)|| TextUtils.isEmpty(email) || TextUtils.isEmpty(password)|| TextUtils.isEmpty(confirm_password))
                {
                    progressDialog.dismiss();

                    Toast.makeText(RegistrationActivity.this, " Please enter valid data ", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern)){
                    progressDialog.dismiss();

                    reg_email.setError("Invalid Email");

                    Toast.makeText(RegistrationActivity.this, " Please enter valid email ", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirm_password)){
                    progressDialog.dismiss();

                    Toast.makeText(RegistrationActivity.this, " password does not match ", Toast.LENGTH_SHORT).show();

                }
                else if( password.length()>6){
                    progressDialog.dismiss();

                    Toast.makeText(RegistrationActivity.this, " password should have only 6 characters ", Toast.LENGTH_SHORT).show();


                }
                else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                DatabaseReference reference = database.getReference().child("user1").child(Objects.requireNonNull(auth.getUid()));

                                StorageReference  storageReference = storage.getReference().child("upload").child(auth.getUid());
                                Toast.makeText(RegistrationActivity.this, "User signed Up successfully ", Toast.LENGTH_SHORT).show();
                                if(image_uri != null){
                                    /* for upload image data(image is stored in stor age and link of image is in  fire baseDB)*/
                                    storageReference.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                      imageURI =  uri.toString();
                                                      Users users = new Users(auth.getUid(),name,email,imageURI,status);
                                                      reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {
                                                              if(task.isSuccessful()){
                                                                  progressDialog.dismiss();

                                                                  startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                              }
                                                              else{

                                                                  Toast.makeText(RegistrationActivity.this, " there is some problem! in creating new user", Toast.LENGTH_SHORT).show();
                                                              }

                                                          }
                                                      });
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                                else{
                                    String status = "hey i am using this application for chat";

                                    imageURI =  "https://firebasestorage.googleapis.com/v0/b/only-chat-6158b.appspot.com/o/profile_image.png?alt=media&token=54e806b6-9d6f-4911-aa89-8866987aae8c";
                                    Users users = new Users(auth.getUid(),name,email,imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                            }
                                            else{
                                                Toast.makeText(RegistrationActivity.this, " there is some problem! in creating new user", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            }
                            else{
                                progressDialog.dismiss();

                                Toast.makeText(RegistrationActivity.this, "Something went wrong ", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivities();
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),10);
            }
        });


        txt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(data != null){
                 image_uri = data.getData();
                profile_image.setImageURI(image_uri);
            }
        }
    }
}