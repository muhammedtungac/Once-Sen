package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalStoryAdapter extends RecyclerView.Adapter<HorizontalStoryAdapter.ViewHolder> {
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    List<String> allStoryUserList,allStoryKeyList;

    public HorizontalStoryAdapter(List<String> allStoryUserList, List<String> allStoryKeyList, Activity activity, Context context) {
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
    public HorizontalStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.takipciler_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalStoryAdapter.ViewHolder holder, int position) {
        databaseReference.child("Hikayeler").child(allStoryUserList.get(position)).child(allStoryKeyList.get(position)).child("Bilgiler").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(holder.horizontalUserImage);
                databaseReference.child("Kullanıcılar").child(allStoryUserList.get(position)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.horizontalUserName.setText(snapshot.child("kullaniciIsim").getValue().toString());
                        if(snapshot.child("userState").getValue().toString().equals("true")){
                            holder.horizontalState.setImageResource(R.drawable.online_state);
                        }
                        else {
                            holder.horizontalState.setImageResource(R.drawable.offline_state);
                        }
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

    @Override
    public int getItemCount() {
        return allStoryUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView horizontalUserImage,horizontalState;
        TextView horizontalUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalUserImage = (CircleImageView) itemView.findViewById(R.id.horizontalUserImage);
            horizontalState = (CircleImageView) itemView.findViewById(R.id.horizontalState);
            horizontalUserName = (TextView) itemView.findViewById(R.id.horizontalUserName);
        }
    }
}
