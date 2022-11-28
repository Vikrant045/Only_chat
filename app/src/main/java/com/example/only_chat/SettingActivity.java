package com.example.only_chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView setting_image;
    ImageView save;
    TextView setting_name, setting_status;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri setImageURI;
    ProgressDialog progressDialog;
    String email;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(" profile updating!..");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        save = findViewById(R.id.save);
        setting_status = findViewById(R.id.setting_status);
        setting_name = findViewById(R.id.setting_name);
        setting_image = findViewById(R.id.setting_image);

        DatabaseReference reference = database.getReference().child("user1").child(Objects.requireNonNull(auth.getUid()));
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue().toString();
                String imageUri = snapshot.child("imageUri").getValue().toString();

                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                setting_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(imageUri).into(setting_image);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivities();
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = setting_name.getText().toString();
                String status = setting_status.getText().toString();
                if (setImageURI != null){

                    storageReference.putFile(setImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                     String finalImage = uri.toString();
                                    Users users = new Users(auth.getUid(), name, email, finalImage, status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "data successfully updated", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                            } else {
                                                progressDialog.dismiss();

                                                Toast.makeText(SettingActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                                }
                            });
                        }
                    });


                }
                else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImage = uri.toString();
                            Users users = new Users(auth.getUid(), name, email, finalImage, status);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();

                                        Toast.makeText(SettingActivity.this, "data successfully updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(SettingActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                        }
                    });

                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                setImageURI = data.getData();
                setting_image.setImageURI(setImageURI);
            }
        }
    }
}