package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.oncesen2.R;
import com.example.oncesen2.Utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SikayetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String text;
    ImageView sikayetBackImageView;
    Button gonderButton;
    EditText sikayetMesajEditText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    public static String userId,gelenId,gonderiId,gonderiKey,commentKey,userId_,otherId_,reportUserName_,userName_;
    public void tanimla() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        spinner = findViewById(R.id.spinner1);
        adapter = ArrayAdapter.createFromResource(this, R.array.sikayetKonu, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        sikayetMesajEditText=findViewById(R.id.sikayetMesajEditText);
        gonderButton=findViewById(R.id.gonderButton);
        sikayetBackImageView=findViewById(R.id.sikayetBackImageView);
    }
    public static void getCommentInfo(String gelenId1, String gonderiId1,String gonderiKey1, String commentKey1){
        gelenId=gelenId1;
        gonderiId=gonderiId1;
        gonderiKey=gonderiKey1;
        commentKey=commentKey1;
    }
    public static void getUserInfo(String userId1,String otherId1,String reportUserName1,String userName1){
        userId_=userId1;
        otherId_=otherId1;
        reportUserName_=reportUserName1;
        userName_=userName1;

    }
    public String getSikayetUserId(){
        Bundle bundle=getIntent().getExtras();
        String getSikayetUserId=bundle.getString("userId");
        return getSikayetUserId;
    }
    public String getSikayetUserName(){
        Bundle bundle=getIntent().getExtras();
        String getSikayetUserName=bundle.getString("userName");
        return getSikayetUserName;
    }
    public String getSikayetMessage(){
        Bundle bundle=getIntent().getExtras();
        String getSikayetMessage=bundle.getString("message");
        return getSikayetMessage;
    }
    public String getSikayetType(){
        Bundle bundle=getIntent().getExtras();
        String getSikayetType=bundle.getString("type");
        return getSikayetType;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sikayet);
        tanimla();
        action();
    }

    public void action() {
        gonderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSikayetType().equals("comment")){
                    if(!sikayetMesajEditText.getText().toString().equals("")){
                        String sikayetKey="sikayetKey - "+databaseReference.child("Gonderiler").child("Sikayetler").child(gelenId).child(gonderiKey).child(commentKey).push().getKey();
                        Map map=new HashMap<>();
                        map.put("userId",getSikayetUserId());
                        map.put("userName",getSikayetUserName());
                        map.put("message",getSikayetMessage());
                        map.put("reportMessage",sikayetMesajEditText.getText().toString());
                        map.put("reportSubject",spinner.getSelectedItem().toString());
                        map.put("type",getSikayetType());
                        databaseReference.child("Sikayetler").child("Gonderiler").child(gelenId).child(gonderiKey).child(commentKey).child(sikayetKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    sikayetMesajEditText.setText("");
                                    spinner.setSelection(0);
                                    ShowToastMessage showToastMessage=new ShowToastMessage(getApplicationContext());
                                    showToastMessage.showToast("Şikayetiniz Alındı");
                                }
                            }
                        });
                    }
                }
                else if (getSikayetType().equals("user")){
                    if(!sikayetMesajEditText.getText().toString().equals("")){
                        String sikayetKey="sikayetKey - "+ databaseReference.child("Sikayetler").child("Kullanicilar").child(userId_).child(otherId_).push().getKey();
                        Map map=new HashMap<>();
                        map.put("userId",userId);
                        map.put("userName",reportUserName_);
                        map.put("reportMessage",sikayetMesajEditText.getText().toString());
                        map.put("reportUserId",otherId_);
                        map.put("reportSubject",spinner.getSelectedItem().toString());
                        map.put("type",getSikayetType());
                        databaseReference.child("Sikayetler").child("Kullanicilar").child(userId_).child(otherId_).child(sikayetKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    sikayetMesajEditText.setText("");
                                    spinner.setSelection(0);
                                    ShowToastMessage showToastMessage=new ShowToastMessage(getApplicationContext());
                                    showToastMessage.showToast("Şikayetiniz Alındı");
                                }
                            }
                        });
                    }
                }

            }
        });
        sikayetBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSikayetType().equals("comment")){
                    Intent intent=new Intent(SikayetActivity.this,AllCommentActivity.class);
                    intent.putExtra("gonderi",gonderiId);
                    intent.putExtra("gonderiKey",gonderiKey);
                    intent.putExtra("userId",gelenId);
                    startActivity(intent);
                }
                else if(getSikayetType().equals("user")){
                    Intent intent=new Intent(SikayetActivity.this,ChatActivity.class);
                    intent.putExtra("userName",userName_);
                    intent.putExtra("previous","messageFragment");
                    intent.putExtra("id",otherId_);
                    startActivity(intent);

                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}