package com.example.oncesen2.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;

import com.example.oncesen2.Activity.MesajIstekActivity;
import com.example.oncesen2.Adapters.InboxAdapter;
import com.example.oncesen2.Adapters.TakipciAdapter;
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

public class MessagesFragment extends Fragment {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    String userId, myAnonId;
    View view;
    TextView istekTextView;
    List<String> requestKeyList, takipciList, istekList, anonimIstekList;
    RecyclerView messagesRecyclerView, takipciRecyclerView;
    public static InboxAdapter adapter;
    TakipciAdapter takipciAdapter;
    public static Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {
        //((AppCompatActivity) getContext()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.example_menu, menu);
        /*final CircleImageView imageView = new CircleImageView(getActivity());
        imageView.setMaxHeight(35);
        imageView.setMaxWidth(35);
        imageView.setImageResource();
        menuItem.setActionView(imageView);*/
        menu.findItem(R.id.item1).setVisible(false);
        menu.findItem(R.id.item2).setVisible(false);
        menu.findItem(R.id.gonderiEkle).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       /* int itemId = item.getItemId();
        if (itemId == R.id.item2) {
            ShowToastMessage show = new ShowToastMessage(getContext());
            show.showToast("Düzenleme Modu Açıldı");
        }
        if (itemId == R.id.gonderiEkle) {
            resimYukle();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        tanimla();
        kisileriYukle();
        takipcileriYukle();
        istekleriYukle();
        //kisiAnonimIdYukle();
        return view;
    }

    public void tanimla() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        database = FirebaseDatabase.getInstance();
        requestKeyList = new ArrayList<>();
        takipciList = new ArrayList<>();
        istekList = new ArrayList<>();
        anonimIstekList = new ArrayList<>();
        istekTextView = view.findViewById(R.id.istekTextView);
        reference = database.getReference().child("Mesajlar");
        toolbar = view.findViewById(R.id.messagesFragmentToolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        //((AppCompatActivity) getContext()).getSupportActionBar().setHomeButtonEnabled(true);
        //((AppCompatActivity) getContext()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        messagesRecyclerView = (RecyclerView) view.findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        messagesRecyclerView.setLayoutManager(layoutManager);
        adapter = new InboxAdapter(requestKeyList, anonimIstekList, getActivity(), getContext(), toolbar, this);
        messagesRecyclerView.setAdapter(adapter);
        takipciRecyclerView = (RecyclerView) view.findViewById(R.id.takipciRecyclerView);
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
        takipciRecyclerView.setLayoutManager(layoutManager2);
        takipciAdapter = new TakipciAdapter(takipciList, getActivity(), getContext());
        takipciRecyclerView.setAdapter(takipciAdapter);
        database.getReference().child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanicilar k1 = snapshot.getValue(Kullanicilar.class);
                myAnonId = k1.getAnonimKullaniciIsmi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        istekTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MesajIstekActivity.class);
                startActivity(intent);
            }
        });
    }

    public void kisileriYukle() {
        database.getReference().child("MesajIzinleri").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                database.getReference().child("MesajIzinleri").child(user.getUid()).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {

                        database.getReference().child("MesajIzinleri").child(user.getUid()).child(snapshot1.getKey()).child(snapshot2.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                reference.child(user.getUid()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot4, @Nullable String previousChildName) {
                                        reference.child(user.getUid()).child(snapshot4.getKey()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot5) {
                                                if (snapshot5.getKey().equals(snapshot1.getKey()) && snapshot3.child("mesajDurum").getValue().toString().equals("mesaj")) {
                                                    if (requestKeyList.indexOf(snapshot1.getKey()) == -1) {
                                                        requestKeyList.add(snapshot1.getKey());
                                                        if(anonimIstekList.indexOf(snapshot2.getKey()) == -1){
                                                            anonimIstekList.add(snapshot2.getKey());
                                                        }

                                                    }
                                                    adapter.notifyDataSetChanged();
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

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //requestKeyList.remove(snapshot.getKey());
                //adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void takipcileriYukle() {
        database.getReference().child("Begeniler").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if (!snapshot1.getKey().equals(userId)) {
                    database.getReference().child("Begeniler").child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.getKey().equals(userId)) {
                                if (takipciList.indexOf(snapshot1.getKey()) == -1) {
                                    takipciList.add(snapshot1.getKey());
                                }
                                takipciAdapter.notifyDataSetChanged();

                                //takipciList.add(snapshot1.getKey());
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

    public void istekleriYukle() {
        database.getReference().child("MesajIzinleri").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    database.getReference().child("MesajIzinleri").child(snapshot.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                            database.getReference().child("MesajIzinleri").child(snapshot.getKey()).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                                    if (!snapshot2.getKey().equals(myAnonId)) {
                                        database.getReference().child("MesajIzinleri").child(snapshot.getKey()).child(snapshot1.getKey()).child(snapshot2.getKey()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                                if (snapshot3.child("mesajDurum").getValue().toString().equals("istek")) {
                                                    istekList.add(snapshot1.getKey());
                                                    if (istekList.size() != 0) {
                                                        Log.i("istekListGöster", String.valueOf(istekList.size()));
                                                        istekTextView.setText(istekList.size() + " " + "Mesaj İsteği");
                                                        istekTextView.setEnabled(true);
                                                        istekTextView.setVisibility(View.VISIBLE);
                                                        istekTextView.setTextSize(20);
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
    /*public void setToolbar(){
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.multiple_delete_menu);
        ((AppCompatActivity)getContext()).getSupportActionBar().setHomeButtonEnabled(true);
        //((AppCompatActivity)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getContext()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }*/

}