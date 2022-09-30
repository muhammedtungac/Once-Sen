package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.AllCommentActivity;
import com.example.oncesen2.Activity.PostActivity;
import com.example.oncesen2.Activity.SikayetActivity;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AllCommentAdapter extends RecyclerView.Adapter<AllCommentAdapter.ViewHolder> implements  PopupMenu.OnMenuItemClickListener {
    List<String> comment;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId,gelenId,gonderiId,gonderiKey,commentKey,mesajTag,userIdTag,userNameTag;
    Boolean yorumSilButtonDurum=false,duzenleButtonDurum=false,sikayetButtonDurum=false;

    public AllCommentAdapter() {

    }

    public AllCommentAdapter(List<String> comment, Activity activity, Context context,String gelenId,String gonderiId,String gonderiKey) {
        this.comment = comment;
        this.activity=activity;
        this.context=context;
        this.gelenId=gelenId;
        this.gonderiId=gonderiId;
        this.gonderiKey=gonderiKey;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
    }

    @NonNull
    @Override
    public AllCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_all_comment_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCommentAdapter.ViewHolder holder, int position) {
        databaseReference.child("Gonderiler").child(gelenId).child(gonderiKey).child("Yorumlar").child(comment.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()==true) {
                    holder.postCommentMessage.setText(snapshot.child("text").getValue().toString());
                    holder.postCommentMessage.setTag(snapshot.child("text").getValue().toString());
                    holder.postCommentUserName.setText(snapshot.child("userName").getValue().toString());
                    holder.postCommentUserName.setTag(snapshot.child("userName").getValue().toString());
                    if(snapshot.child("userId").getValue().toString().equals(userId)){
                        holder.commentPopupImageView.setTag(userId);
                    }
                    else {
                        holder.commentPopupImageView.setTag(snapshot.child("userId").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.commentPopupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((gelenId==userId || gelenId!=userId) && holder.commentPopupImageView.getTag().toString().equals(userId)){
                    yorumSilButtonDurum=false;
                    duzenleButtonDurum=false;
                    sikayetButtonDurum=true;
                    showPopup(holder.commentPopupImageView);
                    commentKey=comment.get(position);
                    mesajTag=holder.postCommentMessage.getTag().toString();
                    userIdTag=holder.commentPopupImageView.getTag().toString();
                    userNameTag=holder.postCommentUserName.getTag().toString();
                }
                else if(gelenId==userId && !holder.commentPopupImageView.getTag().toString().equals(userId)){
                    yorumSilButtonDurum=false;
                    duzenleButtonDurum=true;
                    sikayetButtonDurum=false;
                    showPopup(holder.commentPopupImageView);
                    commentKey=comment.get(position);
                    mesajTag=holder.postCommentMessage.getTag().toString();
                    userIdTag=holder.commentPopupImageView.getTag().toString();
                    userNameTag=holder.postCommentUserName.getTag().toString();
                }

                else {
                    yorumSilButtonDurum=true;
                    duzenleButtonDurum=true;
                    sikayetButtonDurum=false;
                    showPopup(holder.commentPopupImageView);
                    commentKey=comment.get(position);
                    mesajTag=holder.postCommentMessage.getTag().toString();
                    userIdTag=holder.commentPopupImageView.getTag().toString();
                    userNameTag=holder.postCommentUserName.getTag().toString();
                }

            }
        });
    }


    @Override
    public int getItemCount() {

        return comment.size();
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.comment_popup_menu);
        if(yorumSilButtonDurum==true){
            MenuItem item1=popup.getMenu().findItem(R.id.silPopupItem);
            item1.setVisible(false);
        }
        if(duzenleButtonDurum==true){
            MenuItem item1=popup.getMenu().findItem(R.id.duzenlePopupItem);
            item1.setVisible(false);
        }
        if(sikayetButtonDurum==true){
            MenuItem item1=popup.getMenu().findItem(R.id.sikayetPopupItem);
            item1.setVisible(false);
        }
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.silPopupItem:
                databaseReference.child("Gonderiler").child(gelenId).child(gonderiKey).child("Yorumlar").child(commentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            comment.remove(comment.indexOf(commentKey));
                            notifyDataSetChanged();

                        }
                    }
                });
                return true;
            case R.id.sikayetPopupItem:
                SikayetActivity.getCommentInfo(gelenId,gonderiId,gonderiKey,commentKey);
                Intent intent=new Intent(activity, SikayetActivity.class);
                intent.putExtra("userId",userIdTag);
                intent.putExtra("userName",userNameTag);
                intent.putExtra("message",mesajTag);
                intent.putExtra("type","comment");
                activity.startActivity(intent);
                return true;
            case R.id.duzenlePopupItem:
                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.edit_comment_popup_layout, null);
                TextInputEditText editCommentTextInputEditText = view.findViewById(R.id.editCommentTextInputEditText);
                Button editCommentButton = view.findViewById(R.id.editCommentButton);
                editCommentTextInputEditText.setText(mesajTag);
                editCommentTextInputEditText.requestFocus();
                editCommentTextInputEditText.setSelection(editCommentTextInputEditText.length());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setView(view);
                //builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                editCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editCommentTextInputEditText.getText().toString().equals("")){
                            DatabaseReference reference = databaseReference.child("Gonderiler").child(gelenId).child(gonderiKey).child("Yorumlar").child(commentKey).child("text");
                            reference.setValue(editCommentTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ShowToastMessage show = new ShowToastMessage(context);
                                        show.showToast("Mesaj Düzenlendi");

                                    }
                                }
                            });

                            dialog.cancel();
                        }
                        else {
                            ShowToastMessage show = new ShowToastMessage(context);
                            show.showToast("Geçerli bir mesaj girin");
                        }

                    }
                });

                dialog.show();
                return true;
        }
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView postCommentMessage,postCommentUserName;
        ImageView commentPopupImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postCommentMessage=(TextView)itemView.findViewById(R.id.postCommentMessage);
            postCommentUserName=(TextView)itemView.findViewById(R.id.postCommentUserName);
            commentPopupImageView=(ImageView) itemView.findViewById(R.id.commentPopupImageView);
        }
    }
}
