package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Models.MessageModel;
import com.example.oncesen2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VerticalStoryAdapter extends RecyclerView.Adapter<VerticalStoryAdapter.ViewHolder> {
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    List<String> allStoryUserList,allStoryKeyList;

    public VerticalStoryAdapter(List<String> allStoryUserList,List<String> allStoryKeyList, Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.allStoryUserList = allStoryUserList;
        this.allStoryKeyList=allStoryKeyList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
    }

    @NonNull
    @Override
    public VerticalStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gonderi_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalStoryAdapter.ViewHolder holder, int position) {
        databaseReference.child("Hikayeler").child(allStoryUserList.get(position)).child(allStoryKeyList.get(position)).child("Bilgiler").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(holder.gonderiImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.gonderiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                databaseReference.child("Hikayeler").child(allStoryUserList.get(position)).child(allStoryKeyList.get(position)).child("Goruntulenme").child(userId).child("seen").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return allStoryUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gonderiImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gonderiImageView = (ImageView) itemView.findViewById(R.id.gonderiImageView);
        }
    }
}
