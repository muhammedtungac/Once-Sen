package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.PostActivity;
import com.example.oncesen2.R;
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

public class GonderiAdapter extends RecyclerView.Adapter<GonderiAdapter.ViewHolder> {
    View view;
    static List<String> gonderiKeyList,gonderilenList;
    Context context;
    Activity activity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId,userKey;

    @NonNull
    @Override
    public GonderiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.gonderi_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    public GonderiAdapter(List<String> gonderiKeyList, Activity activity, Context context,String userKey) {
        //this.kullanicilar=kullanicilar;
        this.activity = activity;
        this.gonderiKeyList = gonderiKeyList;
        this.context = context;
        this.userKey=userKey;
        gonderilenList=new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
    }

    public GonderiAdapter() {

    }

    @Override
    public void onBindViewHolder(@NonNull GonderiAdapter.ViewHolder holder, int position) {
        databaseReference.child("Gonderiler").child(userKey).child(gonderiKeyList.get(position)).child("Bilgiler").child("resim").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null && !gonderiKeyList.get(position).equals("")){
                    String veri = snapshot.getValue().toString();
                    gonderilenList.add(veri);
                    Picasso.get().load(veri).into(holder.gonderiImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.gonderiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,PostActivity.class);
                intent.putExtra("gonderi",gonderilenList.get(position));
                intent.putExtra("gonderiKey", gonderiKeyList.get(position));
                intent.putExtra("userId",userKey);
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return gonderiKeyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gonderiImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gonderiImageView = itemView.findViewById(R.id.gonderiImageView);
        }
    }
}
