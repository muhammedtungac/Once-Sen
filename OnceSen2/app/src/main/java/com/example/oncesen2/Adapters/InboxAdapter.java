package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.ChatActivity;
import com.example.oncesen2.Fragment.MessagesFragment;
import com.example.oncesen2.Fragment.OtherProfileFragment;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.myViewHolder> {
    Activity activity;
    Context context;
    List<String> model, selectedItems, anonimIstekList, anonSilinecekList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId, myAnonId;
    Boolean multipleSelectMode = false;
    Toolbar messageFragmentToolbar;
    MessagesFragment messagesFragment;
    List<LinearLayout> inboxLayoutList;
    Menu reportMenu;
    MenuItem reportMenuItem;


    public InboxAdapter(List<String> model, List<String> anonimIstekList, Activity activity, Context context, Toolbar messageFragmentToolbar, MessagesFragment messagesFragment1) {
        this.context = context;
        this.activity = activity;
        this.model = model;
        this.messageFragmentToolbar = messageFragmentToolbar;
        this.anonimIstekList = anonimIstekList;
        this.messagesFragment = messagesFragment1;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        selectedItems = new ArrayList<>();
        inboxLayoutList = new ArrayList<>();
        anonSilinecekList = new ArrayList<>();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.messages_layout, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull myViewHolder holder, final int position) {
        getMyAnonId();

        databaseReference.child("Mesajlar").child(userId).child(model.get(position)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {

                if (snapshot1.getKey().equals(myAnonId) && snapshot1.exists()) {

                    databaseReference.child("Kullanıcılar").child(model.get(position)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            /**/

                            //String kullaniciIsim = k1.getKullaniciIsim();
                            //String resim = k1.getResim();
                            //String otherAnonId = snapshot1.child("anonimKullaniciIsmi").getValue().toString();

                            holder.kullaniciIsmiMessages.setText(snapshot2.child("kullaniciIsim").getValue().toString());
                            Picasso.get().load(snapshot2.child("resim").getValue().toString()).into(holder.profilePicMessages);
                            databaseReference.child("Mesajlar").child(userId).child(model.get(position)).child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot3, @Nullable String previousChildName) {
                                    if (snapshot3.exists()) {
                                        databaseReference.child("Mesajlar").child(userId).child(model.get(position)).child(snapshot1.getKey()).child(snapshot3.getKey()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot4) {
                                                if (snapshot4.exists()) {
                                                    holder.messageTime.setText(snapshot4.child("time").getValue().toString());
                                                    holder.mesajIcerikMessages.setText(snapshot4.child("text").getValue().toString());
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

        databaseReference.child("Mesajlar").child(userId).child(model.get(position)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot s1, @Nullable String previousChildName) {
                if (!s1.getKey().equals(myAnonId)) {
                    databaseReference.child("MesajIzinleri").child(userId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snap1, @Nullable String previousChildName) {
                            if (snap1.getKey().equals(model.get(position))) {
                                databaseReference.child("MesajIzinleri").child(userId).child(snap1.getKey()).child(s1.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap2) {

                                        holder.kullaniciIsmiMessages.setText(snap2.child("anonimIsim").getValue().toString());
                                        Picasso.get().load(snap2.child("anonResim").getValue().toString()).into(holder.profilePicMessages);
                                        databaseReference.child("Mesajlar").child(userId).child(snap1.getKey()).child(s1.getKey()).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot snap3, @Nullable String previousChildName) {
                                                databaseReference.child("Mesajlar").child(userId).child(snap1.getKey()).child(s1.getKey()).child(snap3.getKey()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snap4) {
                                                        if (snap4.exists()) {
                                                            holder.messageTime.setText(snap4.child("time").getValue().toString());
                                                            holder.mesajIcerikMessages.setText(snap4.child("text").getValue().toString());

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

        holder.profilePicMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment fragment = new ChangeFragment(context);
                fragment.changeWithParameter(new OtherProfileFragment(), model.get(position));
            }
        });

    }
    public void getMyAnonId() {
        databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAnonId = snapshot.child("anonimKullaniciIsmi").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView kullaniciIsmiMessages, mesajIcerikMessages, messageTime;
        CircleImageView profilePicMessages;
        LinearLayout inboxLayout;
        ActionMode.Callback callback;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            kullaniciIsmiMessages = (TextView) itemView.findViewById(R.id.kullaniciIsmiMessages);
            mesajIcerikMessages = (TextView) itemView.findViewById(R.id.mesajIcerikMessages);
            messageTime = (TextView) itemView.findViewById(R.id.messageTime);
            profilePicMessages = (CircleImageView) itemView.findViewById(R.id.profilePicMessages);
            inboxLayout = (LinearLayout) itemView.findViewById(R.id.inboxLayout);


            inboxLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //messagesFragment.setToolbar();
                    multipleSelectMode = true;
                    ((AppCompatActivity) context).startSupportActionMode(callback);


                    //messageFragmentToolbar.getMenu().clear();
                    //messageFragmentToolbar.inflateMenu(R.menu.multiple_delete_menu);
                    //((AppCompatActivity) activity).getSupportActionBar().hide();

                    if (selectedItems.contains(model.get(getAdapterPosition()))) {

                        inboxLayout.setBackgroundColor(Color.TRANSPARENT);
                        inboxLayoutList.remove(inboxLayoutList.indexOf(inboxLayout));
                        String kullaniciId = kullaniciIsmiMessages.getText().toString();
                        databaseReference.child("Kullanıcılar").child(model.get(getAdapterPosition())).child("anonimKullaniciIsmi").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue().toString().equals(kullaniciId)) {
                                    anonimIstekList.remove(snapshot.getValue().toString());
                                } else {
                                    anonimIstekList.remove(getAdapterPosition());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        selectedItems.remove(model.get(getAdapterPosition()));
                        if(selectedItems.size()>1){
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(false);
                        }
                        else {
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(true);
                        }
                    } else {

                        inboxLayout.setBackgroundResource(R.color.blue);
                        inboxLayoutList.add(inboxLayout);
                        selectedItems.add(model.get(getAdapterPosition()));
                        String kullaniciId = kullaniciIsmiMessages.getText().toString();
                        databaseReference.child("Kullanıcılar").child(model.get(getAdapterPosition())).child("anonimKullaniciIsmi").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue().toString().equals(kullaniciId)) {
                                    anonimIstekList.add(getAdapterPosition(), snapshot.getValue().toString());
                                } else {
                                    anonimIstekList.add(getAdapterPosition(), myAnonId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if(selectedItems.size()>1){
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(false);
                        }
                        else {
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(true);
                        }
                    }
                    /*if (selectedItems.size() == 0) {
                        multipleSelectMode = false;
                    }*/
                    return true;
                }
            });

            inboxLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multipleSelectMode == true) {
                        if (selectedItems.contains(model.get(getAdapterPosition()))) {
                            inboxLayout.setBackgroundColor(Color.TRANSPARENT);
                            inboxLayoutList.remove(inboxLayoutList.indexOf(inboxLayout));
                            String kullaniciId = kullaniciIsmiMessages.getText().toString();
                            databaseReference.child("Kullanıcılar").child(model.get(getAdapterPosition())).child("anonimKullaniciIsmi").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue().toString().equals(kullaniciId)) {
                                        anonimIstekList.remove(snapshot.getValue().toString());
                                    } else {
                                        anonimIstekList.remove(getAdapterPosition());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            selectedItems.remove(model.get(getAdapterPosition()));
                            if(selectedItems.size()>1){
                                if(reportMenu !=null)
                                    reportMenuItem.setVisible(false);
                            }
                            else {
                                if(reportMenu !=null)
                                    reportMenuItem.setVisible(true);
                            }


                        } else {

                            inboxLayout.setBackgroundResource(R.color.blue);
                            inboxLayoutList.add(inboxLayout);
                            selectedItems.add(model.get(getAdapterPosition()));
                            String kullaniciId = kullaniciIsmiMessages.getText().toString();
                            databaseReference.child("Kullanıcılar").child(model.get(getAdapterPosition())).child("anonimKullaniciIsmi").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue().toString().equals(kullaniciId)) {
                                        anonimIstekList.add(getAdapterPosition(), snapshot.getValue().toString());
                                    } else {
                                        anonimIstekList.add(getAdapterPosition(), myAnonId);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        if(selectedItems.size()>1){
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(false);
                        }
                        else {
                            if(reportMenu !=null)
                                reportMenuItem.setVisible(true);
                        }
                        /*if (selectedItems.size() == 0) {
                            multipleSelectMode = false;
                        }*/
                    } else {
                        databaseReference.child("Kullanıcılar").child(model.get(getAdapterPosition())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Intent intent = new Intent(activity, ChatActivity.class);
                                intent.putExtra("userName", kullaniciIsmiMessages.getText());
                                intent.putExtra("previous", "messageFragment");
                                intent.putExtra("id", model.get(getAdapterPosition()));
                                activity.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            });
            callback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    reportMenu=menu;

                    mode.getMenuInflater().inflate(R.menu.multiple_delete_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    //((AppCompatActivity) context).getSupportActionBar().hide();
                    if (reportMenu != null) {
                        reportMenuItem=menu.findItem(R.id.reportIcon);

                    }
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.multipleDelete:
                            if (selectedItems.size() > 0) {
                                for (int i = 0; i < selectedItems.size(); i++) {
                                    String s1 = selectedItems.get(i);
                                    int sira = model.indexOf(selectedItems.get(i));
                                    databaseReference.child("Mesajlar").child(userId).child(s1).child(anonimIstekList.get(sira)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.i("selectedItems1",s1);
                                                //Log.i("selectedItems2",anonimIstekList.get(sira));
                                                model.remove(s1);
                                                notifyDataSetChanged();
                                                //Log.i("istekList",model.get(model.indexOf(s1)));

                                            }
                                        }
                                    });

                                }
                                /*for (int i = 0; i < selectedItems.size(); i++) {
                                    model.remove(selectedItems.get(i));
                                    notifyDataSetChanged();
                                }*/
                                //notifyDataSetChanged();
                                Toast.makeText(activity, "Seçilen  " + selectedItems.size() + " Sohbet Silindi", Toast.LENGTH_SHORT).show();
                            }

                            selectedItems.clear();
                            mode.finish();
                            return true;
                        case R.id.reportIcon:
                            if (selectedItems.size() > 0) {

                            }
                            else {
                            Toast.makeText(activity, "Silinecek bir sohbet bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }

                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    //((AppCompatActivity) context).getSupportActionBar().show();
                    multipleSelectMode = false;
                    if (inboxLayoutList.size() > 0) {
                        for (int i = 0; i < inboxLayoutList.size(); i++) {

                            inboxLayoutList.get(i).setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    //((AppCompatActivity) context).getSupportActionBar().show();
                    selectedItems.clear();

                }

            };

        }

        public void mesajSil() {

            if (selectedItems.size() > 0) {
                for (int i = 0; i < selectedItems.size(); i++) {
                    databaseReference.child("Mesajlar").child(userId).child(model.get(model.indexOf(selectedItems.get(i)))).child(anonimIstekList.get(model.indexOf(selectedItems.get(i)))).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }
    }
}
