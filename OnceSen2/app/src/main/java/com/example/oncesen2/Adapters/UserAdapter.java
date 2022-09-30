package com.example.oncesen2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oncesen2.Fragment.OtherProfileFragment;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    List<String> userKeyList;
    List<String> userKeyListFull;
    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public UserAdapter(List<String> userKeyList, Activity activity, Context context) {
        this.userKeyList = userKeyList;
        userKeyListFull = new ArrayList<>();
        this.activity = activity;
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //Layout tanımlaması yapılacak
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false);
        return new ViewHolder(view);
    }

    //view'lara setlemeler yapılacak
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //holder.usernameTextView.setText(userKeyList.get(position).toString());
        databaseReference.child("Kullanıcılar").child(userKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Kullanicilar kullanicilar = snapshot.getValue(Kullanicilar.class);
                    Object userState = Boolean.parseBoolean(snapshot.child("userState").getValue().toString());
                    if (!kullanicilar.getKullaniciIsim().equals("null")) {
                        Picasso.get().load(kullanicilar.getResim()).into(holder.userImage);
                        holder.usernameTextView.setText(kullanicilar.getKullaniciIsim());
                        userKeyListFull.add(kullanicilar.getKullaniciIsim());
                        holder.hakkimdaTextView.setText(kullanicilar.getHakkimda());
                    }
                    if (userState.equals(true)) {
                        holder.userStateImage.setImageResource(R.drawable.online_state);
                    } else {
                        holder.userStateImage.setImageResource(R.drawable.offline_state);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.userAnaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment fragment = new ChangeFragment(context);
                fragment.changeWithParameter(new OtherProfileFragment(), userKeyList.get(position));
            }
        });
    }

    //Adapteri oluşturulacak listenin size'si
    @Override
    public int getItemCount() {
        return userKeyList.size();
    }

    //View'lerin tanımlanma işlemleri
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, hakkimdaTextView;
        CircleImageView userImage, userStateImage;
        LinearLayout userAnaLayout;

        ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            hakkimdaTextView = (TextView) itemView.findViewById(R.id.hakkimdaTextView);
            userImage = (CircleImageView) itemView.findViewById(R.id.userImage);
            userStateImage = (CircleImageView) itemView.findViewById(R.id.userStateImage);
            userAnaLayout = (LinearLayout) itemView.findViewById(R.id.userAnaLayout);
        }
    }
/*
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userKeyListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String item : userKeyListFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userKeyList.clear();
            userKeyList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };*/
}
