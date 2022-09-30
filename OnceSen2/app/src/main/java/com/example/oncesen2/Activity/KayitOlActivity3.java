package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.oncesen2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class KayitOlActivity3 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText dogumTarihiEditText, cinsiyetEditText;
    Button kayitOl3Button;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol_3);
        tanimla();
    }

    public void tanimla() {
        dogumTarihiEditText = (EditText) findViewById(R.id.dogumTarihiEditText);
        cinsiyetEditText = (EditText) findViewById(R.id.cinsiyetEditText);
        kayitOl3Button = (Button) findViewById(R.id.kayitOl3Button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        dogumTarihiEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        cinsiyetEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cinsiyetDialogAc();
            }
        });
        kayitOl3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task1=reference.child("Kullanıcılar").child(firebaseUser.getUid()).child("yas").setValue(dogumTarihiEditText.getText().toString());
                Task task2=reference.child("Kullanıcılar").child(firebaseUser.getUid()).child("cinsiyet").setValue(cinsiyetEditText.getText().toString());
                Intent intent=new Intent(KayitOlActivity3.this,KayitOlActivity4.class);
                startActivity(intent);

            }
        });
    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }


    public void cinsiyetDialogAc(){
        LayoutInflater inflater=this.getLayoutInflater();
        View view = inflater.inflate(R.layout.alertsex_layout,null);
        Button erkekButton=view.findViewById(R.id.erkekButton);
        Button kızButton=view.findViewById(R.id.kızButton);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog= builder.create();

        erkekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cinsiyetEditText.setText(erkekButton.getText().toString());
                dialog.cancel();
            }
        });

        kızButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cinsiyetEditText.setText(kızButton.getText().toString());
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" +(month+1) + "/" + year;
        dogumTarihiEditText.setText(date);
    }
}