package com.example.only_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

//import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverUID,RName,ReceiverImage,SenderUID;
    CircleImageView profile_image;
    TextView receiverName;
    FirebaseDatabase database;
    FirebaseAuth auth;
 public static String sImage;
    public static String rImage;
    EditText edtMessage;
    CardView sendBtn;
    String senderRoom,receiverRoom;

    RecyclerView msg_adapter;
     ArrayList<Messages> messagesArrayList;

    MessagesAdapter aAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        profile_image = findViewById(R.id.profile_image);
        receiverName = findViewById(R.id.r);
        database = FirebaseDatabase.getInstance();
auth = FirebaseAuth.getInstance();
        RName = getIntent().getStringExtra("name");
        ReceiverUID = getIntent().getStringExtra("uid");
        ReceiverImage = getIntent().getStringExtra("ReceiverImage");

        profile_image = findViewById(R.id.profile_image);
        edtMessage = findViewById(R.id.edtMessage);
        sendBtn = findViewById(R.id.sendBtn);
        messagesArrayList = new ArrayList<>();

        Picasso.get().load(ReceiverImage).into(profile_image); // receiver image
        receiverName.setText(""+RName);
        SenderUID = auth.getUid();

        senderRoom = SenderUID+ReceiverUID;
        receiverRoom = ReceiverUID+SenderUID;

        msg_adapter = findViewById(R.id.msg_adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msg_adapter.setLayoutManager(linearLayoutManager);
        aAdapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        msg_adapter.setAdapter(aAdapter);

        DatabaseReference reference = database.getReference().child("user1").child(Objects.requireNonNull(auth.getUid()));

        DatabaseReference chat_reference = database.getReference().child("chats").child(senderRoom).child("messages");



chat_reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        messagesArrayList.clear();
        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
            Messages messages = dataSnapshot.getValue(Messages.class);
            messagesArrayList.add(messages);
        }
        aAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

        reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
      sImage =  Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
      rImage = ReceiverImage;
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
 sendBtn.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         String message = edtMessage.getText().toString();
         if(message.isEmpty()){
             Toast.makeText(ChatActivity.this, "Please enter some message", Toast.LENGTH_SHORT).show();
             return;
         }
         edtMessage.setText("");
         Date date = new Date();

         Messages messages = new Messages(message,SenderUID,date.getTime());

         database = FirebaseDatabase.getInstance();
         database.getReference().child("chats").child(senderRoom).child("messages").push()
                 .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         database.getReference().child("chats").child(receiverRoom).child("messages").push()
                                 .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {

                                     }
                                 });


                     }
                 });
     }
 });


    }
}