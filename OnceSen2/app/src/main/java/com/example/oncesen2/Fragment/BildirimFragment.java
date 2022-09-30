package com.example.oncesen2.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import com.example.oncesen2.Adapters.FriendRequestAdapter;

import com.example.oncesen2.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;


public class BildirimFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    String userId;
    View view;
    List<String> requestKeyList;
    RecyclerView friendRequestRecyclerView;
    FriendRequestAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bildirim, container, false);
        tanimla();
        istekler();
        return view;
    }

    public void tanimla() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        database = FirebaseDatabase.getInstance();
        requestKeyList = new ArrayList<>();
        reference = database.getReference().child("Arkadaslik_Istek");
        friendRequestRecyclerView = (RecyclerView) view.findViewById(R.id.friendRequestRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        friendRequestRecyclerView.setLayoutManager(layoutManager);
        adapter = new FriendRequestAdapter(requestKeyList, getActivity(), getContext());
        friendRequestRecyclerView.setAdapter(adapter);
    }

    public void istekler() {
        reference.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String kontrol = snapshot.child("Type").getValue().toString();
                if (kontrol.equals("Received")) {
                    if (requestKeyList.indexOf(snapshot.getKey()) == -1) {
                        requestKeyList.add(snapshot.getKey());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                requestKeyList.remove(snapshot.getKey());
                adapter.notifyDataSetChanged();

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