package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oncesen2.Adapters.MessageAdapter;
import com.example.oncesen2.Fragment.MessagesFragment;
import com.example.oncesen2.Fragment.OtherProfileFragment;
import com.example.oncesen2.Models.MessageModel;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
import com.example.oncesen2.Utils.GetDate;
import com.example.oncesen2.Utils.RandomName;
import com.example.oncesen2.Utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //TextView chat_username_textview;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView chatBackImageButton;
    CircleImageView toolbarProfilePic;
    TextView toolbarUserId;
    FloatingActionButton sendMessageButton;
    EditText messageEditText;
    List<MessageModel> messageModelList;
    RecyclerView chat_recyc_view;
    MessageAdapter messageAdapter;
    List<String> userKeyList, anonimIdList, messageKeyList, fromList;
    String anonimKullaniciIsmi, myAnonimKullaniciIsmi, userId, otherAnonId, getirilenDeger, medyaDurumKontrol, deger;
    Boolean durum;
    int mesajSayisi;
    Menu chatMenu;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.chat_toolbar2);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getUserName());*/
        tanimla();
        getMyAnonId();
        getOtherAnonId();
        kontrol1();
        kontrol2();
        mesajKontrol();
        action();
        loadMessage();
        engelDurumKontrol();
        medyaIzınKontrol();
    }

    public void tanimla() {
        //chat_username_textview = findViewById(R.id.chat_username_textview);
        //chat_username_textview.setText(getUserName());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        chatBackImageButton = (ImageView) toolbar.findViewById(R.id.chatBackImageButton);
        toolbarProfilePic = (CircleImageView) toolbar.findViewById(R.id.toolbarProfilePic);
        toolbarUserId = (TextView) toolbar.findViewById(R.id.toolbarUserId);
        sendMessageButton = (FloatingActionButton) findViewById(R.id.sendMessageButton);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        //chatBackButton=(ImageView)findViewById(R.id.chatBackButton);
        messageModelList = new ArrayList<>();
        chat_recyc_view = (RecyclerView) findViewById(R.id.chat_recyc_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 1);
        chat_recyc_view.setLayoutManager(layoutManager);
        userKeyList = new ArrayList<>();
        anonimIdList = new ArrayList<>();
        messageKeyList = new ArrayList<>();
        fromList = new ArrayList<>();
        messageAdapter = new MessageAdapter(ChatActivity.this, ChatActivity.this,getId(), messageModelList,anonimIdList,messageKeyList);
        chat_recyc_view.setAdapter(messageAdapter);
        getMesajSayisi(getId());
    }

    /*@Override
   public boolean onSupportNavigateUp() {
       onBackPressed();
       return true;
   }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.chatMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_settings, menu);
        if (getMesajIstekKontrol() == "") {
            if (chatMenu != null) {
                MenuItem item = chatMenu.findItem(R.id.mesajDurumItem);
                item.setVisible(false);
            }
        }
        if(getMedyaIzinKontrol() == ""){
            if (chatMenu != null) {
                MenuItem item = chatMenu.findItem(R.id.medyaIzinVerItem);
                item.setVisible(false);
            }
        }

        return true;
    }

    public String getMedyaIzinKontrol() {
        String deger="";
    if(anonimKullaniciIsmi.equals(otherAnonId)){
       deger="anon";
    }
    return deger;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mesajDurumItem:
                mesajIstekOnayla();
                return true;

            case R.id.medyaIzinVerItem:
                medyaIzınVer();
                return true;

            case R.id.mesajlariSilItem:
                mesajlariSil();
                return true;

            case R.id.engelleItem:
                engelle();
                return true;

            case R.id.sikayetIzinItem:
                SikayetActivity.getUserInfo(userId,getId(),anonimKullaniciIsmi,getUserName());
                Intent intent=new Intent(this, SikayetActivity.class);
                intent.putExtra("type","user");
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getId() {//otherID
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        return id;
    }

    public String getUserName() {//otherUserName
        Bundle bundle = getIntent().getExtras();
        String userNameText = bundle.getString("userName");
        return userNameText;
    }

    public String getMesajIstekKontrol() {

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("previous") == "istek") {
            deger = bundle.getString("previous");
        } else {
            deger = "";
        }

        return deger;
    }

    public void getMyAnonId() {
        databaseReference.child("Kullanıcılar").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAnonimKullaniciIsmi = snapshot.child("anonimKullaniciIsmi").getValue().toString();
                Log.i("kontrol1", myAnonimKullaniciIsmi);
                //anonimKullaniciIsmi=snapshot.child("anonimKullaniciIsmi").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getOtherAnonId() {

        databaseReference.child("Kullanıcılar").child(getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                otherAnonId = snapshot.child("anonimKullaniciIsmi").getValue().toString();
                Log.i("kontrol1,", otherAnonId);
                Log.i("kontrol1,,", getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void mesajIstekOnayla() {
        databaseReference.child("MesajIzinleri").child(userId).child(getId()).child(otherAnonId).child("mesajDurum").setValue("mesaj").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (chatMenu != null) {
                    MenuItem menuItem = chatMenu.findItem(R.id.mesajDurumItem);
                    menuItem.setVisible(false);
                }
            }
        });
    }

    public void medyaIzınVer() {
        if (chatMenu != null) {
            MenuItem item1 = chatMenu.findItem(R.id.medyaIzinVerItem);
            if (item1.getTitle().toString().equals("Medya İznini Kaldır")) {
                databaseReference.child("MesajIzinleri").child(userId).child(getId()).child(otherAnonId).child("medyaIzinDurum").setValue("hayir").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            item1.setTitle("Medya İzni Ver");
                            ShowToastMessage showToastMessage = new ShowToastMessage(getApplicationContext());
                            showToastMessage.showToast("Medya İzni Kaldırıldı");
                        }
                    }
                });
            } else {
                databaseReference.child("MesajIzinleri").child(userId).child(getId()).child(anonimKullaniciIsmi).child("medyaIzinDurum").setValue("evet").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            item1.setTitle("Medya İznini Kaldır");
                            ShowToastMessage showToastMessage = new ShowToastMessage(getApplicationContext());
                            showToastMessage.showToast("Medya İzni Verildi");
                        }
                    }
                });

            }
        }
    }

    public void engelle() {
        if (chatMenu != null) {
            MenuItem item1 = chatMenu.findItem(R.id.engelleItem);
            if (item1.getTitle().toString().equals("Engeli Kaldır")) {
                databaseReference.child("MesajIzinleri").child(userId).child(getId()).child(anonimKullaniciIsmi).child("engelDurum").setValue("hayir").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            item1.setTitle("Engelle");
                            ShowToastMessage showToastMessage = new ShowToastMessage(getApplicationContext());
                            showToastMessage.showToast("Engel Kaldırıldı");
                        }
                    }
                });
            } else {
                databaseReference.child("MesajIzinleri").child(userId).child(getId()).child(anonimKullaniciIsmi).child("engelDurum").setValue("evet").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            item1.setTitle("Engeli Kaldır");
                            ShowToastMessage showToastMessage = new ShowToastMessage(getApplicationContext());
                            showToastMessage.showToast("Engellendi");
                        }
                    }
                });

            }
        }
    }

    public void mesajlariSil() {

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_messages, null);
        Button evetButton = view.findViewById(R.id.evetButton);
        Button hayirButton = view.findViewById(R.id.hayirButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        //builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        evetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = databaseReference.child("Mesajlar").child(userId).child(getId()).child(anonimKullaniciIsmi);
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ShowToastMessage show = new ShowToastMessage(getApplicationContext());
                            show.showToast("Tüm Mesajlar Silindi");
                            messageModelList.clear();
                            //userKeyList.clear();
                            //messageAdapter.notifyDataSetChanged();
                            chat_recyc_view.removeAllViews();
                        }
                    }
                });

                dialog.cancel();
            }
        });

        hayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });
        dialog.show();
    }

    /*public void mesajDurumKontrol() {
        if (getMesajIstekKontrol() == "") {
            if (chatMenu != null) {
                MenuItem item = chatMenu.findItem(R.id.mesajDurumItem);
                item.setVisible(false);
            }

        }
    }*/

    public void engelDurumKontrol() {
        databaseReference.child("MesajIzinleri").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot0, @Nullable String previousChildName) {
                if (snapshot0.getKey().equals(userId)) {
                    Log.i("snapshot", snapshot0.getKey());
                    databaseReference.child("MesajIzinleri").child(snapshot0.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                            if (snapshot1.getKey().equals(getId())) {
                                Log.i("snapshot1", snapshot1.getKey());
                                databaseReference.child("MesajIzinleri").child(snapshot0.getKey()).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                                        if (snapshot2.getKey().equals(otherAnonId)) {
                                            Log.i("snapshot2", snapshot2.getKey());
                                            databaseReference.child("MesajIzinleri").child(snapshot0.getKey()).child(snapshot1.getKey()).child(snapshot2.getKey()).child("engelDurum").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull final DataSnapshot snapshot3) {
                                                    if (chatMenu != null) {
                                                        MenuItem item1 = chatMenu.findItem(R.id.engelleItem);
                                                        if (snapshot3.getValue().toString().equals("evet")) {
                                                            item1.setTitle("Engeli Kaldır");
                                                        } else {
                                                            item1.setTitle("Engelle");
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

    public void medyaIzınKontrol() {
        databaseReference.child("MesajIzinleri").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot0, @Nullable String previousChildName) {
                if (snapshot0.getKey().equals(userId)) {
                    Log.i("snapshot", snapshot0.getKey());
                    databaseReference.child("MesajIzinleri").child(snapshot0.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                            if (snapshot1.getKey().equals(getId())) {
                                Log.i("snapshot1", snapshot1.getKey());
                                databaseReference.child("MesajIzinleri").child(snapshot0.getKey()).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                                        if (snapshot2.getKey().equals(anonimKullaniciIsmi) && anonimKullaniciIsmi.equals(otherAnonId)) {
                                            Log.i("snapshot2", snapshot2.getKey());
                                            databaseReference.child("MesajIzinleri").child(snapshot0.getKey()).child(snapshot1.getKey()).child(snapshot2.getKey()).child("medyaIzinDurum").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull final DataSnapshot snapshot3) {
                                                    if (chatMenu != null) {
                                                        if (snapshot3.getValue().toString().equals("evet")) {
                                                            MenuItem item = chatMenu.findItem(R.id.medyaIzinVerItem);
                                                            item.setTitle("Medya İzni Ver");
                                                        } else {
                                                            MenuItem item = chatMenu.findItem(R.id.medyaIzinVerItem);
                                                            item.setTitle("Medya İznini Kaldır");
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

    public void kontrol1() {
        databaseReference.child("Kullanıcılar").child(getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getUserName().equals(snapshot.child("kullaniciIsim").getValue().toString())) {
                    //type="kullanici";
                    anonimKullaniciIsmi = myAnonimKullaniciIsmi;
                    Log.i("kontrol2", myAnonimKullaniciIsmi);
                    Log.i("kontrol3", anonimKullaniciIsmi);
                } else {
                    //type="anonim";
                    anonimKullaniciIsmi = getUserName();
                    Log.i("kontrol2,", myAnonimKullaniciIsmi);
                    Log.i("kontrol3,", anonimKullaniciIsmi);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void kontrol2() {

        databaseReference.child("Kullanıcılar").child(getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!getUserName().equals(snapshot.child("anonimKullaniciIsmi").getValue().toString())) {
                    Picasso.get().load(snapshot.child("resim").getValue().toString()).into(toolbarProfilePic);
                    toolbarUserId.setText(getUserName());
                    Log.i("kontrol6", snapshot.child("resim").getValue().toString());
                    Log.i("kontrol7", getUserName());
                } else {
                    databaseReference.child("MesajIzinleri").child(userId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                            if (snapshot1.getKey().equals(getId())) {
                                databaseReference.child("MesajIzinleri").child(userId).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                                        if (snapshot2.getKey().equals(otherAnonId)) {
                                            databaseReference.child("MesajIzinleri").child(userId).child(snapshot1.getKey()).child(snapshot2.getKey()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot3) {

                                                    Picasso.get().load(snapshot3.child("anonResim").getValue().toString()).into(toolbarProfilePic);
                                                    toolbarUserId.setText(snapshot3.child("anonimIsim").getValue().toString());
                                                    Log.i("kontrol6,", snapshot3.child("anonResim").getValue().toString());
                                                    Log.i("kontrol7,", snapshot3.child("anonimIsim").getValue().toString());
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void action() {
        chatBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                String deger = bundle.getString("previous");
                if (deger == "profile") {
                    ChangeFragment changeFragment = new ChangeFragment(getApplicationContext());
                    changeFragment.changeWithParameter(new OtherProfileFragment(), getId());
                    finish();
                } else if (deger == "messageFragment") {
                    ChangeFragment changeFragment = new ChangeFragment(getApplicationContext());
                    changeFragment.change(new MessagesFragment());
                    finish();
                } else if (deger == "istek") {
                    Intent intent = new Intent(ChatActivity.this, MesajIstekActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(ChatActivity.this, AnaActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = messageEditText.getText().toString();
                if (!message.equals("")) {
                    messageEditText.setText("");
                    sendMessage(userId, getId(), "text", GetDate.getDate(), GetDate.getNow(), "false", message, anonimKullaniciIsmi, durum);
                }

            }
        });
    }

    public void mesajKontrol() {
        durum = false;
        databaseReference.child("MesajIzinleri").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if (snapshot1.getKey().equals(userId)) {
                    databaseReference.child("MesajIzinleri").child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                            if (snapshot2.getKey().equals(getId())) {
                                databaseReference.child("MesajIzinleri").child(userId).child(snapshot2.getKey()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot3, @Nullable String previousChildName) {
                                        if (anonimKullaniciIsmi.equals(snapshot3.getKey()) && anonimKullaniciIsmi.equals(otherAnonId) || anonimKullaniciIsmi.equals(snapshot3.getKey()) && anonimKullaniciIsmi.equals(myAnonimKullaniciIsmi)) {
                                            durum = true;
                                            Log.i("kontrol4", anonimKullaniciIsmi);
                                            Log.i("kontrol5", myAnonimKullaniciIsmi);
                                            Log.i("kontrol8", String.valueOf(durum));

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

    public int getMesajSayisi(String otherId) {
        databaseReference.child("Kullanıcılar").child(otherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mesajSayisi = Integer.parseInt(snapshot.child("mesajSayisi").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return mesajSayisi;
    }

    public void sendMessage(String userId, String otherId, String textType, String date, String time, String seen, String messageText, String anonimIsmi, Boolean durum) {

        String messageId = databaseReference.child("Mesajlar").child(userId).child(otherId).child(anonimIsmi).push().getKey();
        Map messageMap = new HashMap();
        messageMap.put("anonimKullaniciIsmi", anonimIsmi);
        messageMap.put("date", date);
        messageMap.put("from", userId);
        messageMap.put("seen", seen);
        messageMap.put("text", messageText);
        messageMap.put("time", time);
        messageMap.put("type", textType);
        messageMap.put("visibility", "visible");
        Log.i("anonimIsim", anonimIsmi);

        databaseReference.child("Mesajlar").child(userId).child(otherId).child(anonimIsmi).child(messageId).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.child("Mesajlar").child(otherId).child(userId).child(anonimIsmi).child(messageId).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (durum == false) {
                                databaseReference.child("Kullanıcılar").child(otherId).child("mesajSayisi").setValue(String.valueOf(mesajSayisi + 1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (task.isSuccessful()) {

                                        }
                                    }
                                });
                                String resimDeger = RandomName.setPictures();
                                Map messageMap1 = new HashMap();
                                messageMap1.put("mesajDurum", "mesaj");
                                messageMap1.put("engelDurum", "hayir");
                                messageMap1.put("medyaIzinDurum", "hayir");
                                messageMap1.put("kontrol", "true");
                                messageMap1.put("anonimIsim", anonimKullaniciIsmi);
                                messageMap1.put("anonResim", resimDeger);
                                databaseReference.child("MesajIzinleri").child(userId).child(otherId).child(anonimIsmi).setValue(messageMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map messageMap2 = new HashMap();
                                            messageMap2.put("mesajDurum", "istek");
                                            messageMap2.put("engelDurum", "hayir");
                                            messageMap2.put("medyaIzinDurum", "hayir");
                                            messageMap2.put("kontrol", "true");
                                            messageMap2.put("anonimIsim", anonimKullaniciIsmi);
                                            messageMap2.put("anonResim", resimDeger);
                                            databaseReference.child("MesajIzinleri").child(otherId).child(userId).child(anonimIsmi).setValue(messageMap2);

                                        }
                                    }
                                });

                            }


                        }
                    }
                });

            }
        });

    }

    public void loadMessage() {
        //Yeni load Message Şart


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();

        }
        getirilenDeger = "";
        databaseReference.child("Mesajlar").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if (snapshot1.getKey().equals(getId())) {

                    databaseReference.child("Mesajlar").child(userId).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                            if (anonimKullaniciIsmi.equals(otherAnonId) && anonimKullaniciIsmi.equals(snapshot2.getKey()) || anonimKullaniciIsmi.equals(myAnonimKullaniciIsmi) && anonimKullaniciIsmi.equals(snapshot2.getKey())) {

                                databaseReference.child("Mesajlar").child(userId).child(snapshot1.getKey()).child(snapshot2.getKey()).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot3, @Nullable String previousChildName) {
                                        databaseReference.child("Mesajlar").child(userId).child(snapshot1.getKey()).child(snapshot2.getKey()).child(snapshot3.getKey()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot4) {
                                                if (!snapshot4.getKey().equals(getirilenDeger)) {
                                                    getirilenDeger = snapshot4.getKey();
                                                    MessageModel messageModel = snapshot4.getValue(MessageModel.class);
                                                    messageModelList.add(messageModel);
                                                    anonimIdList.add(snapshot2.getKey());
                                                    messageKeyList.add(snapshot3.getKey());
                                                    messageAdapter.notifyDataSetChanged();
                                                    //userKeyList.add(getId());
                                                    chat_recyc_view.scrollToPosition(messageModelList.size() - 1);
                                                    //messageAdapter.notifyDataSetChanged();

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

}