package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oncesen2.Adapters.GonderiEditProfileAdapter;
import com.example.oncesen2.Fragment.MessagesFragment;
import com.example.oncesen2.Fragment.NewKullaniciFragment;
import com.example.oncesen2.Models.Kullanicilar;
import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ChangeFragment;
import com.example.oncesen2.Utils.RandomName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    List<String> editGonderiList;
    FirebaseUser user;
    FirebaseAuth auth;
    GonderiEditProfileAdapter adapter;
    DatabaseReference reference;
    FirebaseDatabase database;
    ImageView chatBackImage;
    CircleImageView userAddImage, userImage;
    RecyclerView postPicturesRecyclerView;
    TextInputEditText hakkimdaEditText, dogumTarihiEditText;
    TextView editProfileMainText;
    String userId, imagePath,hakkimdaTextString;
    private static final String KEY_USER = "KEY_USER";
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    Toolbar toolbar;
    Uri profilePicUri;
    public static String gonderiString;
    Menu editMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        tanimla();
        action();
        gonderileriYukle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.editMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem=menu.findItem(R.id.kaydetItem);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kaydetItem:
                item.setVisible(false);
                return true;
        }
return super.onOptionsItemSelected(item);
    }
    public void saveProfile() {
        Task task1 = reference.child("Kullanıcılar").child(userId).child("hakkimda").setValue(hakkimdaEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Task task2 = reference.child("Kullanıcılar").child(userId).child("yas").setValue(dogumTarihiEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            }
        });
    }

    public void tanimla() {
        toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        editGonderiList = new ArrayList<>();
        editProfileMainText = (TextView) toolbar.findViewById(R.id.editProfileMainText);
        chatBackImage = (ImageView) toolbar.findViewById(R.id.chatBackImage);
        hakkimdaEditText = (TextInputEditText) findViewById(R.id.hakkimdaEditText);
        dogumTarihiEditText = (TextInputEditText) findViewById(R.id.dogumTarihiEditText);
        userAddImage = (CircleImageView) findViewById(R.id.userAddImage);
        userImage = (CircleImageView) findViewById(R.id.userImage);
        postPicturesRecyclerView = (RecyclerView) findViewById(R.id.postPicturesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        postPicturesRecyclerView.setLayoutManager(layoutManager);
        adapter = new GonderiEditProfileAdapter(editGonderiList, this, this, userId);
        postPicturesRecyclerView.setAdapter(adapter);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


    }

    public static Intent newIntent(Activity callerActivity, String prm1) {
        Intent intent = new Intent(callerActivity, EditProfileActivity.class);
        intent.putExtra(KEY_USER, prm1);
        return intent;
    }

    public void action() {
        dogumTarihiEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        chatBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnaActivity.previousPost("postActivity");
                Intent intent = new Intent(EditProfileActivity.this, AnaActivity.class);
                startActivity(intent);

            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();

            }
        });
        reference.child("Kullanıcılar").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanicilar k1 = snapshot.getValue(Kullanicilar.class);
                if (!k1.getResim().equals("null")) {
                    Picasso.get().load(k1.getResim()).into(userImage);

                } else {
                    userImage.setImageResource(R.drawable.oncesen_kullaniciresmithemecolor);
                }
                //Picasso.get().load(k1.getResim()).into(mainProfilePicture);
                hakkimdaEditText.setText(k1.getHakkimda());
                hakkimdaTextString=hakkimdaEditText.getText().toString();
                dogumTarihiEditText.setText(k1.getYas());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        hakkimdaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editMenu!=null && s.length()!=hakkimdaTextString.length()){
                    MenuItem item=editMenu.findItem(R.id.kaydetItem);
                    item.setVisible(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });
    }


    public void gonderileriYukle() {
        reference.child("Gonderiler").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (editGonderiList.indexOf(snapshot.getKey()) == -1) {
                    editGonderiList.add(snapshot.getKey());
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

    public void resimYukle() {
        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profilePicUri = result.getUri();
                Picasso.get().load(profilePicUri).into(userImage);

                if(editMenu!=null){
                    MenuItem item=editMenu.findItem(R.id.kaydetItem);
                    item.setVisible(true);
                }

                StorageReference ref = storageReference.child("KullanıcıResimleri").child(userId).child(RandomName.getSaltString() + ".jpg");
                ref.putFile(profilePicUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagePath = uri.toString();
                                    reference.child("Kullanıcılar").child(userId).child("resim").setValue(imagePath).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Resim Yüklendi", Toast.LENGTH_LONG).show();
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

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        dogumTarihiEditText.setText(date);

        if(editMenu!=null){
            MenuItem item=editMenu.findItem(R.id.kaydetItem);
            item.setVisible(true);
        }
    }
}