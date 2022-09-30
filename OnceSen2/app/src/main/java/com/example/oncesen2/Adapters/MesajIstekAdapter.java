package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.AnaActivity;
import com.example.oncesen2.Activity.ChatActivity;
import com.example.oncesen2.Fragment.MessagesFragment;
import com.example.oncesen2.Fragment.OtherProfileFragment;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.Models.MesajIzinleri;
import com.example.oncesen2.Models.MessageModel;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajIstekAdapter extends RecyclerView.Adapter<MesajIstekAdapter.ViewHolder> {
    View view;
    Context context;
    Activity activity;
    List<String> requestKeyList,anonimIstekList;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String userId,myAnonId;

    public MesajIstekAdapter(List<String> requestKeyList,List<String> anonimIstekList, Activity activity, Context context) {
        this.anonimIstekList=anonimIstekList;
        this.context = context;
        this.activity = activity;
        this.requestKeyList = requestKeyList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.istek_messages_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myAnonId = snapshot.child("anonimKullaniciIsmi").getValue().toString();
                Log.i("istek1",myAnonId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("MesajIzinleri").child(userId).child(requestKeyList.get(position)).child(anonimIstekList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                holder.kullaniciIsmiIstekMessages.setText(snapshot1.child("anonimIsim").getValue().toString());
                Picasso.get().load(snapshot1.child("anonResim").getValue().toString()).into(holder.profilePicIstekMessages);
                databaseReference.child("Mesajlar").child(userId).child(requestKeyList.get(position)).child(anonimIstekList.get(position)).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                        databaseReference.child("Mesajlar").child(userId).child(requestKeyList.get(position)).child(anonimIstekList.get(position)).child(snapshot2.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {

                                holder.mesajIcerikIstekMessages.setText(snapshot3.child("text").getValue().toString());
                                holder.messageİIstekTime.setText(snapshot3.child("time").getValue().toString());
                                Log.i("istek2",holder.kullaniciIsmiIstekMessages.getText().toString());
                                Log.i("istek3",holder.mesajIcerikIstekMessages.getText().toString());
                                Log.i("istek4",holder.messageİIstekTime.getText().toString());
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

        holder.chatBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnaActivity.previousPost("istek");
                Intent intent=new Intent(activity, AnaActivity.class);
                activity.startActivity(intent);
            }
        });
        holder.profilePicIstekMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment fragment = new ChangeFragment(context);
                fragment.changeWithParameter(new OtherProfileFragment(), requestKeyList.get(position));
            }
        });
        holder.inboxIstekLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("userName", anonimIstekList.get(position));
                intent.putExtra("id", requestKeyList.get(position));
                intent.putExtra("previous","istek");
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestKeyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView kullaniciIsmiIstekMessages, mesajIcerikIstekMessages, messageİIstekTime;
        CircleImageView profilePicIstekMessages;
        LinearLayout inboxIstekLayout;
        ImageView chatBackImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatBackImageButton=(ImageView) activity.findViewById(R.id.chatBackImageButton);
            kullaniciIsmiIstekMessages = (TextView) itemView.findViewById(R.id.kullaniciIsmiIstekMessages);
            mesajIcerikIstekMessages = (TextView) itemView.findViewById(R.id.mesajIcerikIstekMessages);
            messageİIstekTime = (TextView) itemView.findViewById(R.id.messageIstekTime);
            profilePicIstekMessages = (CircleImageView) itemView.findViewById(R.id.profilePicIstekMessages);
            inboxIstekLayout = (LinearLayout) itemView.findViewById(R.id.inboxIstekLayout);
        }
    }
}
