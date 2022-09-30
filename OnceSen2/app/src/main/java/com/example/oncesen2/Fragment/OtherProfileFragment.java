package com.example.oncesen2.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oncesen2.Activity.ChatActivity;
import com.example.oncesen2.Adapters.GonderiAdapter;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileFragment extends Fragment {
    Toolbar toolbar;
    TextView kullaniciIsmi,hakkimdaText,mesajSayisiText,gönderiSayisiText,takipciSayisiText;
    CircleImageView mainProfilePicture;
    View view;
    Button mesajButton,takipEtButton;
    String otherId,userId,anonimKullaniciIsmi;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ShowToastMessage showToastMessage;
    RecyclerView gonderiRecyclerView;
    GonderiAdapter adapter;
    List<String>gonderiKeyList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        tanimla();
        action();
        gonderileriYukle();
        getBegeniText();
        getArkadasText();
        return view;
    }

    public void tanimla() {

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        userId=user.getUid();
        otherId = getArguments().getString("userId");
        mesajButton=(Button) view.findViewById(R.id.mesajButton);
        toolbar = view.findViewById(R.id.toolbar);
        gonderiKeyList=new ArrayList<>();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        takipEtButton=(Button) view.findViewById(R.id.takipEtButton);
        kullaniciIsmi=(TextView)view.findViewById(R.id.kullaniciIsmi);
        hakkimdaText=(TextView)view.findViewById(R.id.hakkimdaText);
        gönderiSayisiText=(TextView)view.findViewById(R.id.gönderiSayisiText);
        mesajSayisiText=(TextView)view.findViewById(R.id.mesajSayisiText);
        takipciSayisiText=(TextView)view.findViewById(R.id.takipciSayisiText);
        mainProfilePicture=(CircleImageView)view.findViewById(R.id.mainProfilePicture);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        showToastMessage=new ShowToastMessage(getContext());
        gonderiRecyclerView=(RecyclerView)view.findViewById(R.id.gonderiRecyclerView);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        gonderiRecyclerView.setLayoutManager(layoutManager);
        adapter=new GonderiAdapter(gonderiKeyList,getActivity(),getContext(),otherId);
        gonderiRecyclerView.setAdapter(adapter);

    }
    public void action()
    {
        databaseReference.child("Kullanıcılar").child(otherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanicilar kl=snapshot.getValue(Kullanicilar.class);
                kullaniciIsmi.setText(kl.getKullaniciIsim());
                hakkimdaText.setText(kl.getHakkimda());
                mesajSayisiText.setText(kl.getMesajSayisi());
                anonimKullaniciIsmi=kl.getAnonimKullaniciIsmi();
                if(!kl.getResim().equals("null")){
                    Picasso.get().load(kl.getResim()).into(mainProfilePicture);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("TakipListesi").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if(snapshot1.getKey().equals(otherId)){
                    databaseReference.child("TakipListesi").child(userId).child(snapshot1.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if(!snapshot2.getValue().equals(null)){
                                if(snapshot2.child("tip").getValue().toString().equals("takipci")){
                                    takipEtButton.setText("Takip Ediliyor");
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

        takipEtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(takipEtButton.getText().toString().equals("Takip Ediliyor")){
                    begeniIptal(userId,otherId);
                }
                else {
                    begen(userId,otherId);
                }
            }

        });
        mesajButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName",kullaniciIsmi.getText());
                intent.putExtra("id",otherId);
                intent.putExtra("previous","profile");
                //intent.putExtra("anonimKullaniciIsmi",anonimKullaniciIsmi);
                startActivity(intent);

            }
        });

    }
    public void gonderileriYukle() {
        databaseReference.child("Gonderiler").child(otherId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (gonderiKeyList.indexOf(snapshot.getKey()) == -1) {
                    gonderiKeyList.add(snapshot.getKey());
                    //adapter.notifyDataSetChanged();
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

    }
    public void  begen(String userId,String otherId){
        databaseReference.child("TakipListesi").child(otherId).child(userId).child("tip").setValue("takipci").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToastMessage.showToast("Bu Profil Takip Ediliyor");
                    //begeniKontrol="begendi";
                    takipEtButton.setText("Takip Ediliyor");
                    getBegeniText();
                }
            }
        });
    }
    public void begeniIptal(String userId, String otherId) {
        databaseReference.child("TakipListesi").child(otherId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //begeniKontrol="";
                getBegeniText();
                takipEtButton.setText("Takip Et");
                showToastMessage.showToast("Bu Profili Artık Takip Etmiyorsunuz");

            }
        });
    }
    public void getBegeniText(){
        //final List<String> begeniList=new ArrayList<>();
        //begeniSayisiText.setText("0 Beğeni");
        databaseReference.child("Begeniler").child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                takipciSayisiText.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getArkadasText(){
        //getChildrenCount için addListenerForSingleValueEvent() kullanılması önerilir
        //final List<String> arkadasList=new ArrayList<>();
        //arkadasSayisiText.setText("0 Arkadaş");
        databaseReference.child("Arkadaslar").child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gönderiSayisiText.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}