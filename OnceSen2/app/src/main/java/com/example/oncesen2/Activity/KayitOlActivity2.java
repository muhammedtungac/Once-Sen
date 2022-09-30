package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.oncesen2.Adapters.UserAdapter;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KayitOlActivity2 extends AppCompatActivity {
    EditText userName;
    TextView userNameState;
    Button kayitOl2Button;
    List<String> userKeyList;
    UserAdapter userAdapter;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String kontrol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol_2);
        tanimla();
    }

    public void tanimla() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userName = (EditText) findViewById(R.id.userName);
        userKeyList = new ArrayList<>();
        userNameState = (TextView) findViewById(R.id.userNameState);
        kayitOl2Button = (Button) findViewById(R.id.kayitOl2Button);
        userAdapter = new UserAdapter(userKeyList, this, this);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kontrol = "bulunamadı";
                //userNameState.setText("Kullanıcı İsminiz Diğer Kullanıcılarla Aynı Olabilir ");
                reference.child("Kullanıcılar").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        reference.child("Kullanıcılar").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Kullanicilar kullanicilar = snapshot.getValue(Kullanicilar.class);
                                //String userNameText=snapshot.child("kullaniciIsim").getValue().toString();
                                if (kullanicilar.getKullaniciIsim().equals(userName.getText().toString()) && kontrol.equals("bulunamadı")) {
                                    kontrol = "bulundu";
                                    //userAdapter.notifyDataSetChanged();
                                }
                                if (kontrol.equals("bulundu") || userName.getText().toString().equals("null") || userName.getText().toString().equals("")) {
                                    userNameState.setText("Bu Kullanıcı Adı Uygun Değil!");
                                    kayitOl2Button.setEnabled(false);
                                    kayitOl2Button.setClickable(false);
                                } else {
                                    userNameState.setText("Bu Kullanıcı Adı Uygun!");
                                    kayitOl2Button.setEnabled(true);
                                    kayitOl2Button.setClickable(true);
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

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        kayitOl2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userNameText=userName.getText().toString();
                reference.child("Kullanıcılar").child(firebaseUser.getUid()).child("kullaniciIsim").setValue(userNameText).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent=new Intent(KayitOlActivity2.this,KayitOlActivity3.class);
                            startActivity(intent);
                        }

                    }
                });


            }
        });
    }
}