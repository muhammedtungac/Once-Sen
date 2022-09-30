package com.example.oncesen2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oncesen2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class KayitOlActivity1 extends AppCompatActivity {

    EditText input_email,input_password;
    Button  kayitOl1Button;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    TextView loginTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol_1);
        tanimla();
    }
    public void tanimla(){
        input_email=(EditText)findViewById(R.id.input_email);
        input_password=(EditText)findViewById(R.id.input_password);
        kayitOl1Button=(Button)findViewById(R.id.kayitOl1Button);
        loginTextView=(TextView)findViewById(R.id.loginTextView);
        auth=FirebaseAuth.getInstance();
        kayitOl1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=input_email.getText().toString();
                String pass= input_password.getText().toString();
                if(!email.equals("") && !pass.equals("")){
                    input_email.setText("");
                    input_password.setText("");
                    kayitOl(email,pass);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Bu mail ile kayıt yapamıyor ",Toast.LENGTH_LONG).show();
                }
            }
        });
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(KayitOlActivity1.this,GirisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void kayitOl(String email,String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    database=FirebaseDatabase.getInstance();
                    reference=database.getReference().child("Kullanıcılar").child(auth.getUid());
                    Map map=new HashMap();
                    map.put("resim","null");
                    map.put("kullaniciIsim","null");
                    map.put("cinsiyet","null");
                    map.put("yas","null");
                    map.put("hakkimda","null");
                    map.put("userState","true");
                    reference.setValue(map);
                    Intent intent=new Intent(KayitOlActivity1.this, KayitOlActivity2.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Kayıt Olurken Bir Problem Yaşandı",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}