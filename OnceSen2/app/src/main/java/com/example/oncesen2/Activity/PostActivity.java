package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oncesen2.Adapters.PostActivityCommentAdapter;
import com.example.oncesen2.Fragment.NewKullaniciFragment;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    public static String gonderiString;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    Toolbar toolbar;
    CircleImageView postProfilePic, likeCircleImageView, commentCircleImageView, postBackButton;
    TextView postUserId, showAllCommentTextView,postActivityBegeniText;
    ImageView postImageView;
    String userId,userName, deger = "begenmedi",yorumKey;
    RecyclerView postCommentRecyclerView;
    public static PostActivityCommentAdapter postActivityCommentAdapter;
    List<String> postActivityCommentList,commentKeyList;
    LinearLayout sendPostCommentLinearLayout;
    TextInputEditText sendPostCommentEditText;
    Button postCommentButton;
    Boolean opened = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tanimla();
        action();

    }

    public String getGelenId() {
        Bundle bundle = getIntent().getExtras();
        String gelenId = bundle.getString("userId");
        return gelenId;
    }

    public String getGonderiKey() {
        Bundle bundle = getIntent().getExtras();
        String gonderiId = bundle.getString("gonderiKey");
        return gonderiId;
    }

    public String getGonderiId() {
        Bundle bundle = getIntent().getExtras();
        String gonderiId = bundle.getString("gonderi");
        return gonderiId;
    }

    public void tanimla() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        postActivityCommentList = new ArrayList<>();
        commentKeyList = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.post_toolbar2);
        postBackButton = (CircleImageView) toolbar.findViewById(R.id.postBackButton);
        postProfilePic = (CircleImageView) findViewById(R.id.postProfilePic);
        likeCircleImageView = (CircleImageView) findViewById(R.id.likeCircleImageView);
        likeCircleImageView.setImageResource(R.drawable.post_activity_empty_heart);
        commentCircleImageView = (CircleImageView) findViewById(R.id.commentCircleImageView);
        postImageView = (ImageView) findViewById(R.id.postImageView);
        postUserId = (TextView) findViewById(R.id.postUserId);
        postActivityBegeniText=(TextView) findViewById(R.id.postActivityBegeniText);
        sendPostCommentEditText = (TextInputEditText) findViewById(R.id.sendPostCommentEditText);
        sendPostCommentLinearLayout = (LinearLayout) findViewById(R.id.sendPostCommentLinearLayout);
        sendPostCommentLinearLayout.setVisibility(View.INVISIBLE);
        postCommentButton = (Button) findViewById(R.id.postCommentButton);
        showAllCommentTextView = (TextView) findViewById(R.id.showAllCommentTextView);
        showAllCommentTextView.setVisibility(View.INVISIBLE);
        postCommentRecyclerView = (RecyclerView) findViewById(R.id.postCommentRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        postActivityCommentAdapter = new PostActivityCommentAdapter(postActivityCommentList, this, this, getGelenId(), getGonderiKey());
        postCommentRecyclerView.setLayoutManager(layoutManager);
        postCommentRecyclerView.setAdapter(postActivityCommentAdapter);

    }

    public void action() {
        databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("kullaniciIsim").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Kullanıcılar").child(getGelenId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postUserId.setText(snapshot.child("kullaniciIsim").getValue().toString());
                Picasso.get().load(snapshot.child("resim").getValue().toString()).into(postProfilePic);
                Picasso.get().load(getGonderiId()).into(postImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (commentKeyList.indexOf(snapshot.getKey()) == -1) {
                    postActivityCommentList.add(snapshot.getKey());
                    postCommentRecyclerView.scrollToPosition(postActivityCommentList.size() - 1);
                }
                postActivityCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deger = String.valueOf(snapshot.getChildrenCount());
                if (Integer.parseInt(deger) >= 4) {
                    showAllCommentTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Begeniler").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    likeCircleImageView.setImageResource(R.drawable.post_activity_full_heart);
                    deger = "begendi";
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Begeniler").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int deger=Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                postActivityBegeniText.setText(deger + " kişi beğendi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnaActivity.previousPost("postActivity");
                Intent intent = new Intent(PostActivity.this, AnaActivity.class);
                startActivity(intent);
            }
        });
        likeCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deger.equals("begendi")) {
                    databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Begeniler").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                likeCircleImageView.setImageResource(R.drawable.post_activity_empty_heart);
                                deger = "begenmedi";
                            }
                        }
                    });

                } else {
                    databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Begeniler").child(userId).child("state").setValue("like").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                likeCircleImageView.setImageResource(R.drawable.post_activity_full_heart);
                                deger = "begendi";
                            }
                        }
                    });

                }
            }
        });
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key=databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").push().getKey();
                Map map = new HashMap();
                map.put("text", sendPostCommentEditText.getText().toString());
                map.put("userId", userId);
                map.put("userName", userName);
                databaseReference.child("Gonderiler").child(getGelenId()).child(getGonderiKey()).child("Yorumlar").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendPostCommentEditText.setText("");
                        }
                    }
                });
            }
        });

        commentCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opened == false) {
                    opened = true;
                    sendPostCommentLinearLayout.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            sendPostCommentLinearLayout.getHeight(),
                            0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    sendPostCommentLinearLayout.startAnimation(animate);
                } else {
                    opened = false;
                    sendPostCommentLinearLayout.setVisibility(View.INVISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            0,
                            sendPostCommentLinearLayout.getHeight());
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    sendPostCommentLinearLayout.startAnimation(animate);
                }
            }
        });

        showAllCommentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostActivity.this,AllCommentActivity.class);
                intent.putExtra("gonderi",getGonderiId());
                intent.putExtra("gonderiKey",getGonderiKey());
                intent.putExtra("userId",getGelenId());
                startActivity(intent);
            }
        });

    }
}