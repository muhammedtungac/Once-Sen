package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.oncesen2.Adapters.AllCommentAdapter;
import com.example.oncesen2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCommentActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseDatabase database;
    String userId,userName;
    FirebaseUser user;
    FirebaseAuth auth;
    public static AllCommentAdapter adapter;
    RecyclerView allCommentRecyclerView;
    List<String> commentKeyList;
    ImageView commentActivityBackButton;
    TextInputEditText sendPostAllCommentEditText;
    Button postCommentAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);
        tanimla();
        action();
    }

    public void tanimla() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        commentKeyList = new ArrayList<>();
        allCommentRecyclerView = findViewById(R.id.allCommentRecyclerView);
        commentActivityBackButton = findViewById(R.id.commentActivityBackButton);
        sendPostAllCommentEditText = findViewById(R.id.sendPostAllCommentEditText);
        postCommentAllButton = findViewById(R.id.postCommentAllButton);
        adapter = new AllCommentAdapter(commentKeyList, AllCommentActivity.this, getApplicationContext(), getGelenId(),getGonderiId(),getGonderiKey());
        allCommentRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        allCommentRecyclerView.setLayoutManager(layoutManager);
    }



    public String getGelenId() {
        Bundle bundle = getIntent().getExtras();
        String gelenId = bundle.getString("userId");
        return gelenId;
    }

    public String getGonderiKey() {
        Bundle bundle = getIntent().getExtras();
        String gonderiId = bundle.getString("gonderiKey");
        return gonderiId;
    }

    public String getGonderiId() {
        Bundle bundle = getIntent().getExtras();
        String gonderiId = bundle.getString("gonderi");
        return gonderiId;
    }

    public void action() {
        databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("kullaniciIsim").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if (commentKeyList.indexOf(snapshot1.getKey()) == -1) {
                    commentKeyList.add(snapshot1.getKey());
                    allCommentRecyclerView.scrollToPosition(commentKeyList.size() - 1);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentActivityBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllCommentActivity.this, PostActivity.class);
                intent.putExtra("userId", getGelenId());
                intent.putExtra("gonderiKey", getGonderiKey());
                intent.putExtra("gonderi", getGonderiId());
                startActivity(intent);
            }
        });
        postCommentAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key=databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").push().getKey();
                Map map = new HashMap();
                map.put("text", sendPostAllCommentEditText.getText().toString());
                map.put("userId", userId);
                map.put("userName", userName);
                databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendPostAllCommentEditText.setText("");
                        }
                    }
                });
            }
        });
    }
}