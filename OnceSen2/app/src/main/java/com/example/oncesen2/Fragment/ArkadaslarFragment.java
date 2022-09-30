package com.example.oncesen2.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oncesen2.Adapters.FriendAdapter;
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


public class ArkadaslarFragment extends Fragment {
    View view;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    RecyclerView friendRecyclerView;
    FriendAdapter friendAdapter;
    List<String> keyList;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_arkadaslar, container, false);
        tanimla();
        getArkadasList();
        return view;
    }
    public void tanimla(){
        keyList=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference("Arkadaslar");
        friendRecyclerView=(RecyclerView) view.findViewById(R.id.friendRecyclerView);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(),1);
        friendRecyclerView.setLayoutManager(layoutManager);
        friendAdapter=new FriendAdapter(keyList,getActivity(),getContext());
        friendRecyclerView.setAdapter(friendAdapter);
    }
    public void getArkadasList(){
        reference.child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(keyList.indexOf(snapshot.getKey())==-1){
                    keyList.add(snapshot.getKey());
                }

                friendAdapter.notifyDataSetChanged();
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