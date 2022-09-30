package com.example.oncesen2.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oncesen2.Activity.AnaActivity;
import com.example.oncesen2.Activity.EditStoryActivity;
import com.example.oncesen2.Adapters.HorizontalStoryAdapter;
import com.example.oncesen2.Adapters.VerticalStoryAdapter;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.GetDate;
import com.example.oncesen2.Utils.RandomName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class StoriesFragment extends Fragment {

    View view;
    RecyclerView storyHorizontalRecyclerView, storyVerticalRecyclerView;
    List<String> allStoryUserList, allStoryKeyList, randomStoryHorizontalUserList, randomStoryHorizontalKeyList,randomStoryVerticalUserList,randomStoryVerticalKeyList;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId, imagePath, userName;
    VerticalStoryAdapter adapter1;
    HorizontalStoryAdapter adapter2;
    CircleImageView myProfilePicture, editStory,addStory;
    TextView myUserName;
    CardView userCardView;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    int storySize;

    public StoriesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stories, container, false);
        tanimla();
        action();
        createRandomVerticalStory();
        createRandomHorizontalStory();
        return view;
    }

    public void tanimla() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        allStoryUserList = new ArrayList<>();
        allStoryKeyList = new ArrayList<>();
        randomStoryVerticalUserList = new ArrayList<>();
        randomStoryVerticalKeyList = new ArrayList<>();
        randomStoryHorizontalUserList = new ArrayList<>();
        randomStoryHorizontalKeyList = new ArrayList<>();
        storyVerticalRecyclerView = (RecyclerView) view.findViewById(R.id.storyVerticalRecyclerView);
        storyHorizontalRecyclerView = (RecyclerView) view.findViewById(R.id.storyHorizontalRecyclerView);
        myProfilePicture = (CircleImageView) view.findViewById(R.id.myProfilePicture);
        editStory = (CircleImageView) view.findViewById(R.id.editStory);
        addStory= (CircleImageView) view.findViewById(R.id.addStory);
        userCardView = (CardView) view.findViewById(R.id.userCardView);
        myUserName = (TextView) view.findViewById(R.id.myUserName);
        adapter1 = new VerticalStoryAdapter(randomStoryVerticalUserList, randomStoryVerticalKeyList, getActivity(), getContext());
        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(getContext(), 5);
        storyVerticalRecyclerView.setLayoutManager(layoutManager1);
        storyVerticalRecyclerView.setAdapter(adapter1);
        adapter2 = new HorizontalStoryAdapter(randomStoryHorizontalUserList, randomStoryHorizontalKeyList, getActivity(), getContext());
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(getContext(), 5);
        storyVerticalRecyclerView.setLayoutManager(layoutManager2);
        storyVerticalRecyclerView.setAdapter(adapter2);

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
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if(snapshot1.getKey().equals("Hikayeler")){
                    databaseReference.child(snapshot1.getKey()).child(userId).addChildEventListener(new ChildEventListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                            databaseReference.child(snapshot1.getKey()).child(userId).child(snapshot2.getKey()).child("Bilgiler").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                    if (snapshot3.exists()) {
                                        Picasso.get().load(snapshot3.child("imageUrl").getValue().toString()).into(myProfilePicture);
                                        myUserName.setText(snapshot3.child("userName").getValue().toString());
                                        editStory.setVisibility(View.VISIBLE);
                                        myProfilePicture.setBorderColor(R.color.light_blue_600);
                                    }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


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
                }
                else {
                    databaseReference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Picasso.get().load(snapshot.child("resim").getValue().toString()).into(myProfilePicture);
                            myUserName.setText(snapshot.child("kullaniciIsim").getValue().toString());
                            //editStory.setImageResource(R.drawable.add_user_picture);
                            editStory.setVisibility(View.INVISIBLE);
                            myProfilePicture.setBorderColor(R.color.oncesen_mainColor);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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

        databaseReference.child("Hikayeler").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if (!snapshot1.getKey().equals(userId)){
                    databaseReference.child("Hikayeler").child(snapshot1.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot2, @Nullable String previousChildName) {
                            if (allStoryKeyList.indexOf(snapshot2.getKey()) == -1) {
                                allStoryUserList.add(snapshot1.getKey());
                                allStoryKeyList.add(snapshot2.getKey());
                                adapter1.notifyDataSetChanged();
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

        editStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditStoryActivity.class);
                    startActivity(intent);

            }
        });

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hikayeYukle();
            }
        });
    }
    public void createRandomVerticalStory() {
        if (allStoryUserList.size() != 0) {
            Random rnd = new Random();
            for (int i = 0; i <= 20; i++) {
                int randomInt = rnd.nextInt(allStoryUserList.size());
                randomStoryVerticalUserList.add(allStoryUserList.get(randomInt));
                randomStoryVerticalKeyList.add(allStoryUserList.get(randomInt));
                adapter1.notifyDataSetChanged();
            }
        }
    }
    public void createRandomHorizontalStory() {
        if (allStoryUserList.size() != 0) {
            Random rnd = new Random();
            for (int i = 0; i <= 20; i++) {
                int randomInt = rnd.nextInt(allStoryUserList.size());
                randomStoryHorizontalUserList.add(allStoryUserList.get(randomInt));
                randomStoryHorizontalKeyList.add(allStoryUserList.get(randomInt));
                adapter2.notifyDataSetChanged();
            }
        }
    }

    public void hikayeYukle() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.i("uri", resultUri.toString());
                //profilePicture.setImageUriAsync(resultUri);
                StorageReference ref = storageReference.child("Hikayeler").child(userId).child(RandomName.getSaltString() + ".jpg");
                ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagePath = uri.toString();
                                    Log.i("imagepath", imagePath);
                                    //Toast.makeText(getContext(), "Resim Yüklendi", Toast.LENGTH_LONG).show();
                                    DatabaseReference reference = firebaseDatabase.getReference().child("Hikayeler").child(userId).child(RandomName.getSaltString()).child("Bilgiler");
                                    Map map = new HashMap();
                                    map.put("userName", userName);
                                    map.put("imageUrl", imagePath);
                                    map.put("date", GetDate.getDate());
                                    map.put("time",GetDate.getNow());
                                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                /*ChangeFragment fragment = new ChangeFragment(getContext());
                                                fragment.change(new NewKullaniciFragment());*/
                                                Toast.makeText(getContext(), "Hikaye Eklendi", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getContext(), "Hikaye Eklenemedi", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}