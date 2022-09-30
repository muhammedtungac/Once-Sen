package com.example.oncesen2.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oncesen2.Activity.AnaActivity;
import com.example.oncesen2.Activity.EditProfileActivity;
import com.example.oncesen2.Activity.EditStoryActivity;
import com.example.oncesen2.Activity.GirisActivity;
import com.example.oncesen2.Activity.KayitOlActivity4;
import com.example.oncesen2.Activity.SettingsActivity;
import com.example.oncesen2.Adapters.GonderiAdapter;
import com.example.oncesen2.Adapters.InboxAdapter;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
import com.example.oncesen2.Utils.GetDate;
import com.example.oncesen2.Utils.RandomName;
import com.example.oncesen2.Utils.ShowToastMessage;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class NewKullaniciFragment extends Fragment {

    View view;
    Toolbar toolbar;
    CircleImageView mainProfilePicture;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth auth;
    String userId, imagePath, veriYolu;
    TextView customTitle, takipciSayisiText, arkadasSayisiText, mesajSayisiText, kullaniciIsmi, hakkimdaText;
    RecyclerView gonderiRecyclerView;
    GonderiAdapter adapter;
    List<String> gonderi, gonderi2;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    //String userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //((AppCompatActivity) getContext()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.example_menu, menu);
        /*final CircleImageView imageView = new CircleImageView(getActivity());
        imageView.setMaxHeight(35);
        imageView.setMaxWidth(35);
        imageView.setImageResource();
        menuItem.setActionView(imageView);*/
        //menu.findItem(R.id.item1).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item1) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.item2) {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.gonderiEkle) {
            sharePicAlertDialog();
        }
        if (itemId == R.id.itemCikis) {
            cik();
        }
        return super.onOptionsItemSelected(item);
    }

    public void cik() {
        auth.signOut();
        Intent intent = new Intent(getActivity(), GirisActivity.class);
        startActivity(intent);
        //databaseReference.child(user.getUid()).child("userState").setValue(false);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Kullanıcılar");
        reference.child(user.getUid()).child("userState").setValue(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_kullanici, container, false);
        tanimla();
        gonderileriYukle();

        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.oncesen_kullaniciresmithemecolor);

        return view;
    }


    public void tanimla() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        toolbar = view.findViewById(R.id.toolbar2);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        customTitle = toolbar.findViewById(R.id.customTitle);
        mainProfilePicture = view.findViewById(R.id.mainProfilePicture);
        takipciSayisiText = view.findViewById(R.id.takipciSayisiText);
        mesajSayisiText = view.findViewById(R.id.mesajSayisiText);
        arkadasSayisiText = view.findViewById(R.id.arkadasSayisiText);
        kullaniciIsmi = view.findViewById(R.id.kullaniciIsmi);
        hakkimdaText = view.findViewById(R.id.hakkimdaText);
        gonderiRecyclerView = view.findViewById(R.id.gonderiRecyclerView);
        gonderi = new ArrayList<>();
        gonderi2 = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        gonderiRecyclerView.setLayoutManager(layoutManager);
        adapter = new GonderiAdapter(gonderi, getActivity(), getContext(),userId);
        gonderiRecyclerView.setAdapter(adapter);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        reference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanicilar k1 = snapshot.getValue(Kullanicilar.class);
                if (!k1.getResim().equals("null")) {
                    Picasso.get().load(k1.getResim()).into(mainProfilePicture);
                } else {
                    mainProfilePicture.setImageResource(R.drawable.oncesen_kullaniciresmithemecolor);
                }
                //Picasso.get().load(k1.getResim()).into(mainProfilePicture);
                customTitle.setText(k1.getKullaniciIsim());
                kullaniciIsmi.setText(k1.getKullaniciIsim());
                hakkimdaText.setText(k1.getHakkimda());
                mesajSayisiText.setText(k1.getMesajSayisi());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("Begeniler").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                takipciSayisiText.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("Hikayeler").child(userId).addChildEventListener(new ChildEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    mainProfilePicture.setBorderColor(R.color.light_blue_600);
                    mainProfilePicture.setTag("editStory");
                }
                else {
                    mainProfilePicture.setBorderColor(R.color.oncesen_mainColor);
                    mainProfilePicture.setTag("addStory");
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

        mainProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainProfilePicture.getTag().toString().equals("editStory")){
                    Intent intent = new Intent(getActivity(), EditStoryActivity.class);
                    startActivity(intent);
                }
                else {
                    ChangeFragment changeFragment=new ChangeFragment(getContext());
                    changeFragment.change(new StoriesFragment());
                    AnaActivity.navView.setSelectedItemId(R.id.navigation_stories);
                }
            }
        });
        gonderiTextGuncelle();

    }

    public void sharePicAlertDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.sharepic_alert_dialog, null);
        Button gonderiButton = view.findViewById(R.id.gonderiButton);
        Button hikayeButton = view.findViewById(R.id.hikayeButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        //builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        gonderiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gonderiYukle();
                dialog.cancel();
            }
        });

        hikayeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeFragment changeFragment=new ChangeFragment(getContext());
                changeFragment.change(new StoriesFragment());
                AnaActivity.navView.setSelectedItemId(R.id.navigation_stories);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void gonderiTextGuncelle() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("Gonderiler").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arkadasSayisiText.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void gonderileriYukle() {
        reference.child("Gonderiler").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (gonderi.indexOf(snapshot.getKey()) == -1) {
                    gonderi.add(snapshot.getKey());
                    //adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();


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

    public void gonderiYukle() {
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
                StorageReference ref = storageReference.child("Gonderiler").child(userId).child(RandomName.getSaltString() + ".jpg");
                ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagePath = uri.toString();
                                    Log.i("imagepath", imagePath);
                                    Toast.makeText(getContext(), "Resim Yüklendi", Toast.LENGTH_LONG).show();
                                    reference = database.getReference().child("Gonderiler").child(user.getUid()).child(RandomName.getSaltString()).child("Bilgiler");
                                    Map map = new HashMap();
                                    map.put("kullaniciIsim", userId);
                                    map.put("resim", imagePath);
                                    map.put("tarih", GetDate.getDate());
                                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                /*ChangeFragment fragment = new ChangeFragment(getContext());
                                                fragment.change(new NewKullaniciFragment());*/
                                                gonderiTextGuncelle();
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(getContext(), "Gönderi Eklendi", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getContext(), "Gönderi Eklenemedi", Toast.LENGTH_LONG).show();
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