package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.ViewStoryActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditStoryAdapter extends RecyclerView.Adapter<EditStoryAdapter.ViewHolder> {
    Activity activity;
    Context context;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId,imageUrl,time;
    List<String> myStoryKeyList,myStoryImageList,myStoryUserNameList,myStoryTimeList,myStorySeenList,myStoryLikeList ;
    public EditStoryAdapter(){

    }

    public EditStoryAdapter(List<String> myStoryList,Activity activity,Context context){
        this.activity=activity;
        this.context=context;
        this.myStoryKeyList=myStoryList;
        myStoryImageList=new ArrayList<>();
        myStoryUserNameList=new ArrayList<>();
        myStoryTimeList=new ArrayList<>();
        myStorySeenList=new ArrayList<>();
        myStoryLikeList=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        userId=user.getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public EditStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.edit_story_recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditStoryAdapter.ViewHolder holder, int position) {
  databaseReference.child("Hikayeler").child(userId).child(myStoryKeyList.get(position)).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
          if(snapshot.exists()){
              Picasso.get().load(snapshot.child("Bilgiler").child("imageUrl").getValue().toString()).into(holder.storyPicRecyclerView);
              holder.storyTimeRecyclerView.setText(snapshot.child("Bilgiler").child("time").getValue().toString());
              holder.storySeenRecyclerView.setText(String.valueOf(snapshot.child("Goruntulenme").getChildrenCount()) + " " +"Görüntülenme");

              //holder.editStoryRelativeLayout.setTag(1,snapshot.child("Bilgiler").child("imageUrl").getValue().toString());
              if(myStoryImageList.indexOf(snapshot.child("Bilgiler").child("imageUrl").getValue().toString())==-1){
                  myStoryImageList.add(snapshot.child("Bilgiler").child("imageUrl").getValue().toString());
                  myStoryUserNameList.add(snapshot.child("Bilgiler").child("userName").getValue().toString());
                  myStorySeenList.add(String.valueOf(snapshot.child("Goruntulenme").getChildrenCount()));
                  myStoryTimeList.add(snapshot.child("Bilgiler").child("time").getValue().toString());
                  }
          }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
  });

  holder.storyDeleteRecyclerView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          databaseReference.child("Hikayeler").child(userId).child(myStoryKeyList.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(activity, "Hikaye Silindi", Toast.LENGTH_SHORT).show();
                  myStoryKeyList.remove(position);
                  notifyDataSetChanged();
              }
          });
      }
  });

  holder.editStoryRelativeLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          //ViewStoryActivity.getImageInfo(myStoryImageList,myStoryKeyList,holder.editStoryRelativeLayout.getTag(R.string.bir).toString(),holder.editStoryRelativeLayout.getTag(R.string.iki).toString());
          ViewStoryActivity.getImageInfo(myStoryImageList,myStoryKeyList,myStoryUserNameList,myStoryTimeList,myStorySeenList);
          Intent intent=new Intent(activity, ViewStoryActivity.class);
          activity.startActivity(intent);

      }
  });
    }

    @Override
    public int getItemCount() {
        return myStoryKeyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView storyPicRecyclerView,storyDeleteRecyclerView;
        TextView storySeenRecyclerView,storyTimeRecyclerView;
        RelativeLayout editStoryRelativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyPicRecyclerView=(CircleImageView)itemView.findViewById(R.id.storyPicRecyclerView);
            storyDeleteRecyclerView=(CircleImageView)itemView.findViewById(R.id.storyDeleteRecyclerView);
            storySeenRecyclerView=(TextView)itemView.findViewById(R.id.storySeenRecyclerView);
            storyTimeRecyclerView=(TextView)itemView.findViewById(R.id.storyTimeRecyclerView);
            editStoryRelativeLayout=(RelativeLayout)itemView.findViewById(R.id.editStoryRelativeLayout);
        }
    }
}
