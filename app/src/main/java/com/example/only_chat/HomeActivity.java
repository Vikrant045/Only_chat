package com.example.only_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
     RecyclerView Recycle_view;
     UserAdapter adapter;
     FirebaseDatabase database;
     ArrayList<Users> usersArrayList;
    ImageView img_logOut;
    ImageView img_Setting;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         auth = FirebaseAuth.getInstance();
         database = FirebaseDatabase.getInstance();

        usersArrayList = new ArrayList<>();

        DatabaseReference reference =  database.getReference().child("user1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        img_logOut = findViewById(R.id.img_logOut);
img_Setting = findViewById(R.id.img_Setting);
        Recycle_view = findViewById(R.id.Recycle_view);
        Recycle_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this,usersArrayList);

        Recycle_view.setAdapter(adapter);

img_logOut.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Dialog  dialog = new Dialog(HomeActivity.this,R.style.Dialoge);
         dialog.setContentView(R.layout.dialog_layout);
        TextView no_btn,yes_btn;
        yes_btn = dialog.findViewById(R.id.yes_btn);
        no_btn = dialog.findViewById(R.id.no_btn);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent(HomeActivity.this,RegistrationActivity.class));
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
});
img_Setting.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity( new Intent(HomeActivity.this,SettingActivity.class));
    }
});


        if(auth.getCurrentUser()==null){
startActivity(new Intent( HomeActivity.this,RegistrationActivity.class));
        }


    }
    @Override
public void onBackPressed(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit the app");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
}
}