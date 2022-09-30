package com.example.oncesen2.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.example.oncesen2.Activity.SettingsActivity;
import com.example.oncesen2.Adapters.UserAdapter;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShuffleFragment extends Fragment {

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List<String> userKeyList;
    List<String> userNameList;
    RecyclerView userListRecyclerView;
    View view;
    MenuItem searchItem;
    SearchView searchView;
    UserAdapter userAdapter;
    Toolbar shuffleToolbar;
    String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_shuffle, container, false);
        tanimla();
        kullaniciGetir();
        return view;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.searchView) {
            kullaniciAra();
        }
        return super.onOptionsItemSelected(item);
    }

    public void kullaniciAra() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.kullanici_ara, null);
        EditText kullaniciAraEditText=view.findViewById(R.id.kullaniciAraEditText);
        Button kullaniciAraButton = view.findViewById(R.id.kullaniciAraButton);
        CircleImageView kullaniciAraCikisCircleImageView = view.findViewById(R.id.kullaniciAraCikisCircleImageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        kullaniciAraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userKeyList.clear();
                databaseReference.child("Kullanıcılar").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        databaseReference.child("Kullanıcılar").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Kullanicilar kullanicilar = snapshot.getValue(Kullanicilar.class);
                                //Log.i("deneme",kullanicilar.getKullaniciIsim());
                                if(!kullaniciAraEditText.getText().toString().equals(null)){
                                    if(kullanicilar.getKullaniciIsim().contains(kullaniciAraEditText.getText().toString())){
                                        if (!snapshot.getKey().equals(firebaseUser.getUid())) {
                                            if (userKeyList.indexOf(snapshot.getKey())==-1){
                                                userKeyList.add(snapshot.getKey());
                                                //userNameList.add(kullanicilar.getKullaniciIsim());

                                            }
                                            //userAdapter.notifyDataSetChanged();
                                        }
                                        userAdapter.notifyDataSetChanged();
                                    }
                                }
                                else {
                                    ShowToastMessage show = new ShowToastMessage(getContext());
                                    show.showToast("Boş bir şey arayamam");

                                    dialog.cancel();
                                }
                                userAdapter.notifyDataSetChanged();
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
                dialog.cancel();
            }

        });

        kullaniciAraCikisCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kullaniciGetir();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.searchview_menu,menu);
        //searchItem=menu.findItem(R.id.searchView);
        /*searchView= (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (userNameList.contains(query)) {
                    userAdapter.getFilter().filter(query);
                }
                else {
                    // Search query not found in List View
                    ShowToastMessage show=new ShowToastMessage(getContext());
                    show.showToast("Bulunamadı");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //userAdapter.getFilter().filter(newText);
                return false;
            }
        });*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void tanimla() {
        userKeyList = new ArrayList<>();
        userNameList= new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        shuffleToolbar=view.findViewById(R.id.shuffleToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(shuffleToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        userListRecyclerView = view.findViewById(R.id.userListRecyclerView);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 1);
        userListRecyclerView.setLayoutManager(manager);
        userAdapter = new UserAdapter(userKeyList, getActivity(), getContext());
        userListRecyclerView.setAdapter(userAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        userId=firebaseUser.getUid();

    }

    public void kullaniciGetir() {
        databaseReference.child("Kullanıcılar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                databaseReference.child("Kullanıcılar").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        for (DataSnapshot dataSnapshot : snapshot2.getChildren()) {
                            Kullanicilar kullanicilar = snapshot2.getValue(Kullanicilar.class);
                            if(!snapshot.getKey().equals(userId) & userKeyList.indexOf(snapshot.getKey())==-1){
                                userKeyList.add(String.valueOf(snapshot.getKey()));
                                Collections.shuffle(userKeyList);
                                userAdapter.notifyDataSetChanged();
                            }

                        }
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

        /*databaseReference.child("Kullanıcılar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.getKey().equals(userId)) {
                    databaseReference.child("Kullanıcılar").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Kullanicilar kullanicilar = snapshot.getValue(Kullanicilar.class);
                            //Log.i("deneme",kullanicilar.getKullaniciIsim());
                            if (!kullanicilar.getKullaniciIsim().equals("null")) {
                                if (userKeyList.indexOf(snapshot.getKey())==-1){
                                    userKeyList.add(snapshot.getKey());
                                    //userNameList.add(kullanicilar.getKullaniciIsim());

                                }
                                userAdapter.notifyDataSetChanged();
                            }
                            userAdapter.notifyDataSetChanged();
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
        });*/
    }


}