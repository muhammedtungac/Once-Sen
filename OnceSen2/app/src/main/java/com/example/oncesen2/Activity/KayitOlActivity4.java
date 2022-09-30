package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oncesen2.R;
import com.example.oncesen2.Utils.GetDate;
import com.example.oncesen2.Utils.RandomName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class KayitOlActivity4 extends AppCompatActivity {
    String imagePath;
    CropImageView profilePicture;
    EditText durumText;
    Button kayitOl4Button;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol_4);
        tanimla();

    }

    public void tanimla() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        profilePicture = (CropImageView) findViewById(R.id.profilePicture);
        durumText = (EditText) findViewById(R.id.durumText);
        kayitOl4Button = (Button) findViewById(R.id.kayitOl4Button);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();



        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

        kayitOl4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task1=reference.child("Kullanıcılar").child(user.getUid()).child("resim").setValue(imagePath);
                Task task2=reference.child("Kullanıcılar").child(user.getUid()).child("hakkimda").setValue(durumText.getText().toString());
                Task task3=reference.child("Kullanıcılar").child(user.getUid()).child("mesajSayisi").setValue("0");
                Task task4=reference.child("Kullanıcılar").child(user.getUid()).child("sikayetSayisi").setValue("0");
                Task task5=reference.child("Kullanıcılar").child(user.getUid()).child("anonimKullaniciIsmi").setValue("oncesen - "+RandomName.getSaltString());
                Task task6=reference.child("Kullanıcılar").child(user.getUid()).child("kayitTarih").setValue(GetDate.getDate());
                Task task7=reference.child("Kullanıcılar").child(user.getUid()).child("kayitSaat").setValue(GetDate.getNow());
                Intent intent = new Intent(KayitOlActivity4.this, AnaActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void resimYukle() {
        ActivityCompat.requestPermissions(KayitOlActivity4.this,new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE},1);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profilePicture.setImageUriAsync(resultUri);
                StorageReference ref = storageReference.child("KullanıcıResimleri").child(user.getUid()).child(RandomName.getSaltString()+".jpg");
                ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imagePath=uri.toString();
                                    Toast.makeText(getApplicationContext(), "Resim Yüklendi", Toast.LENGTH_LONG).show();
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