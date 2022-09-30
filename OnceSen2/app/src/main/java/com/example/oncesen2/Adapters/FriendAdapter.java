package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
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

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    List<String> userKeyList;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    public FriendAdapter(List<String> userKeyList, Activity activity, Context context) {
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
        View view= LayoutInflater.from(context).inflate(R.layout.friend_layout,parent,false);
        return new ViewHolder(view);
    }
    //view'lara setlemeler yapılacak
    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder,final int position) {

        //holder.usernameTextView.setText(userKeyList.get(position).toString());
        databaseReference.child("Kullanıcılar").child(userKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName=snapshot.child("kullaniciIsim").getValue().toString();
                    String userImage=snapshot.child("resim").getValue().toString();
                    Boolean userState=Boolean.parseBoolean(snapshot.child("userState").getValue().toString());
                    if(userState==true){
                        holder.friendStateImage.setImageResource(R.drawable.online_state);
                    }
                    else{
                        holder.friendStateImage.setImageResource(R.drawable.offline_state);
                    }


                    Picasso.get().load(userImage).into(holder.friendImage);
                    holder.friendText.setText(userName);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        TextView friendText;
        CircleImageView friendStateImage,friendImage;

        ViewHolder(View itemView){
            super(itemView);
            friendText=(TextView) itemView.findViewById(R.id.friendText);
            friendStateImage=(CircleImageView) itemView.findViewById(R.id.friendStateImage);
            friendImage=(CircleImageView) itemView.findViewById(R.id.friendImage);

        }
    }
}
