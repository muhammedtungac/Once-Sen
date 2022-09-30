package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Activity.EditProfileActivity;
import com.example.oncesen2.Activity.PostActivity;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GonderiEditProfileAdapter extends RecyclerView.Adapter<GonderiEditProfileAdapter.ViewHolder> {
    View view;
    List<String> editGonderi,editGonderilenList;
    Context context;
    Activity activity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId,userName;

    @NonNull
    @Override
    public GonderiEditProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.edit_profile_gonderi_recycler_view     , parent, false);
        return new ViewHolder(view);
    }

    public GonderiEditProfileAdapter(List<String> editGonderi, Activity activity, Context context, String userName) {
        //this.kullanicilar=kullanicilar;
        this.activity = activity;
        this.editGonderi = editGonderi;
        this.context = context;
        this.userName=userName;
        editGonderilenList=new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();


    }

    public GonderiEditProfileAdapter() {

    }

    @Override
    public void onBindViewHolder(@NonNull GonderiEditProfileAdapter.ViewHolder holder, int position) {
        databaseReference.child("Gonderiler").child(userId).child(editGonderi.get(position)).child("resim").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null && !editGonderi.get(position).equals("")){
                    String veri = snapshot.getValue().toString();
                    editGonderilenList.add(veri);
                    Picasso.get().load(veri).into(holder.gonderiImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*databaseReference.child("Gonderiler").child(userId).child(gonderi.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String veri = snapshot.child("resim").getValue().toString();
                gonderilenList.add(veri);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        holder.gonderiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(activity,PostActivity.class);
                intent.putExtra("gonderi",editGonderilenList.get(position));
                intent.putExtra("userId",userId);
                activity.startActivity(intent);
            }
        });

        holder.gonderiSilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.gonderi_sil_dialog_layout,null);
                Button evetButton=view.findViewById(R.id.evetButton);
                Button hayirButton=view.findViewById(R.id.hayirButton);

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setView(view);
                builder.setCancelable(false);
                AlertDialog dialog= builder.create();

                evetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference dr=databaseReference.child("Gonderiler").child(userId).child(editGonderi.get(position));
                        dr.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                editGonderi.remove(position);
                                editGonderilenList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Gönderi Silindi", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                    }
                });

                hayirButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return editGonderi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gonderiImageView;
        CircleImageView gonderiSilImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gonderiImageView = (ImageView) itemView.findViewById(R.id.gonderiImageView);
            gonderiSilImageView = (CircleImageView) itemView.findViewById(R.id.gonderiSilImageView);
        }
    }
}
