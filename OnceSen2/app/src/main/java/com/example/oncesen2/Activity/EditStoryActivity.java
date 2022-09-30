package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.oncesen2.Adapters.EditStoryAdapter;
import com.example.oncesen2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EditStoryActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    EditStoryAdapter adapter;
    List<String> myStoryKeyList;
    RecyclerView editStoryRecyclerView;
    ImageView editStoryBackImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);
        tanimla();
        action();
    }
    public void tanimla(){
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        userId=user.getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        myStoryKeyList=new ArrayList<>();
        editStoryRecyclerView=findViewById(R.id.editStoryRecyclerView);
        editStoryBackImageView=findViewById(R.id.editStoryBackImageView);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        editStoryRecyclerView.setLayoutManager(layoutManager);
        adapter=new EditStoryAdapter(myStoryKeyList,this,getApplicationContext());
        editStoryRecyclerView.setAdapter(adapter);

    }
    public void action(){
        databaseReference.child("Hikayeler").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(myStoryKeyList.indexOf(snapshot.getKey())== -1){
                    myStoryKeyList.add(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }
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

        editStoryBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnaActivity.previousPost("story");
                Intent intent=new Intent(EditStoryActivity.this,AnaActivity.class);
                startActivity(intent);
            }
        });
    }
}