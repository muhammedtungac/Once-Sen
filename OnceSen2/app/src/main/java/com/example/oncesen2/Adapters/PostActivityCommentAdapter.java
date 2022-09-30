package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

public class PostActivityCommentAdapter extends RecyclerView.Adapter<PostActivityCommentAdapter.ViewHolder> {
    List<String> commentList;
    Activity activity;
    Context context;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    String gelenId,gonderiId,userId;
    FirebaseAuth auth;
    FirebaseUser user;


    public PostActivityCommentAdapter() {

    }

    public PostActivityCommentAdapter(List<String> commentList, Activity activity, Context context,String gelenId,String gonderiId) {
        this.activity = activity;
        this.context = context;
        this.commentList = commentList;
        this.gelenId = gelenId;
        this.gonderiId = gonderiId;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
    }

    @NonNull
    @Override
    public PostActivityCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comment_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostActivityCommentAdapter.ViewHolder holder, int position) {
    databaseReference.child("Gonderiler").child(gelenId).child(gonderiId).child("Yorumlar").child(commentList.get(position)).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()==true ){
                holder.postCommentMessage.setText(snapshot.child("text").getValue().toString());
                holder.postCommentUserName.setText(snapshot.child("userName").getValue().toString());
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView postCommentMessage,postCommentUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postCommentMessage=(TextView) itemView.findViewById(R.id.postCommentMessage1);
            postCommentUserName=(TextView) itemView.findViewById(R.id.postCommentUserName1);
        }
    }
}
