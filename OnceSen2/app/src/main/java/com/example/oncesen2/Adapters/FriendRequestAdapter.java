package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Fragment.OtherProfileFragment;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>{
    List<String> userKeyList;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    public FriendRequestAdapter(List<String> userKeyList, Activity activity, Context context) {
        this.userKeyList = userKeyList;
        this.activity = activity;
        this.context = context;
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        userId=user.getUid();
    }

    //Layout tanımlaması yapılacak
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.friend_request_layout,parent,false);
        return new ViewHolder(view);
    }
    //view'lara setlemeler yapılacak
    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder,final int position) {

        //holder.usernameTextView.setText(userKeyList.get(position).toString());
        databaseReference.child("Kullanıcılar").child(userKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanicilar kullanicilar = snapshot.getValue(Kullanicilar.class);
                    Picasso.get().load(kullanicilar.getResim()).into(holder.friendRequestImage);
                    holder.usernameTextView.setText(kullanicilar.getKullaniciIsim());
                    holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            kabulEt(userId,userKeyList.get(position));
                        }
                    });

                    holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reddet(userId,userKeyList.get(position));
                        }
                    });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void kabulEt(String userId, String otherId) {
        DateFormat df=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today= Calendar.getInstance().getTime();
        final String date=df.format(today);
        databaseReference.child("Arkadaslar").child(userId).child(otherId).child("tarih").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.child("Arkadaslar").child(otherId).child(userId).child("tarih").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context,"Arkadaşlık isteğini kabul ettiniz",Toast.LENGTH_LONG).show();
                            databaseReference.child("Arkadaslik_Istek").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference.child("Arkadaslik_Istek").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void reddet(String userId, String otherId){

        databaseReference.child("Arkadaslik_Istek").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child("Arkadaslik_Istek").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Arkadaşlık isteği reddedildi",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //Adapteri oluşturulacak listenin size'si
    @Override
    public int getItemCount() {
        return userKeyList.size();
    }
    //View'lerin tanımlanma işlemleri
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView usernameTextView;
        CircleImageView friendRequestImage;
        Button acceptButton,cancelButton;

        ViewHolder(View itemView){
            super(itemView);
            usernameTextView=(TextView) itemView.findViewById(R.id.usernameTextView);
            friendRequestImage=(CircleImageView) itemView.findViewById(R.id.friendRequestImage);
            acceptButton=(Button) itemView.findViewById(R.id.acceptButton);
            cancelButton=(Button) itemView.findViewById(R.id.cancelButton);

        }
    }
}
