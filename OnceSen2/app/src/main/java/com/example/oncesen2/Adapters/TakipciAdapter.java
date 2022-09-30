package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class TakipciAdapter extends RecyclerView.Adapter<TakipciAdapter.ViewHolder> {
    List<String> list;
    Context context;
    Activity activity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    public TakipciAdapter(List<String> list, Activity activity,Context context) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
    }

    @NonNull
    @Override
    public TakipciAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.takipciler_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TakipciAdapter.ViewHolder holder, int position) {
        databaseReference.child("Kullanıcılar").child(list.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanicilar kullanicilar=snapshot.getValue(Kullanicilar.class);
                Picasso.get().load(kullanicilar.getResim()).into(holder.takipUserImage);
                if(kullanicilar.getUserState().equals(true)){
                    holder.takipStateImage.setImageResource(R.drawable.online_state);
                }
                else {
                    holder.takipStateImage.setImageResource(R.drawable.offline_state);
                }
                holder.takipKullaniciIsmi.setText(kullanicilar.getKullaniciIsim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView takipUserImage,takipStateImage;
        TextView takipKullaniciIsmi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            takipKullaniciIsmi=itemView.findViewById(R.id.horizontalUserName);
            takipUserImage=itemView.findViewById(R.id.horizontalUserImage);
            takipStateImage=itemView.findViewById(R.id.horizontalState);
        }
    }
}
