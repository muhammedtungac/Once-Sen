package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oncesen2.Adapters.InboxAdapter;
import com.example.oncesen2.Adapters.MesajIstekAdapter;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MesajIstekActivity extends AppCompatActivity {
    RecyclerView mesajIstekRecyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String userId, myAnonId;
    List<String> requestKeyList,anonimIstekList;
    MesajIstekAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj_istek);
        tanimla();
    }
    public void tanimla() {
        mesajIstekRecyclerView = (RecyclerView) findViewById(R.id.mesajIstekRecyclerView);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        requestKeyList = new ArrayList<>();
        anonimIstekList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        mesajIstekRecyclerView.setLayoutManager(layoutManager);
        adapter=new MesajIstekAdapter(requestKeyList,anonimIstekList,this,getApplicationContext());
        mesajIstekRecyclerView.setAdapter(adapter);

        databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myAnonId = snapshot.child("kullaniciIsim").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("MesajIzinleri").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                databaseReference.child("MesajIzinleri").child(userId).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                        if(!snapshot2.getKey().equals(myAnonId)){
                            databaseReference.child("MesajIzinleri").child(userId).child(snapshot1.getKey()).child(snapshot2.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                    if(snapshot3.child("mesajDurum").getValue().toString().equals("istek")){
                                        if (requestKeyList.indexOf(snapshot1.getKey()) == -1) {
                                            requestKeyList.add(snapshot1.getKey());
                                            anonimIstekList.add(snapshot2.getKey());
                                            adapter.notifyDataSetChanged();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
    }
}