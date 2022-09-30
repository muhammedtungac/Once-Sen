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

public class GirisActivity extends AppCompatActivity {

    Button loginButton;
    TextView registerTextView;
    EditText input_id_login, input_password_login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        tanimlama();
    }

    public void tanimlama() {
        auth=FirebaseAuth.getInstance();
        loginButton = (Button) findViewById(R.id.loginButton);
        registerTextView = (TextView) findViewById(R.id.registerTextView);
        input_id_login = (EditText) findViewById(R.id.input_id_login);
        input_password_login = (EditText) findViewById(R.id.input_password_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = input_id_login.getText().toString();
                String pass = input_password_login.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    sistemeGiris(email, pass);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Boş Girilemez",Toast.LENGTH_LONG).show();
                }
            }
        });
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GirisActivity.this, KayitOlActivity1.class);
                startActivity(intent);
                finish();

            }
        });

    }

    public void sistemeGiris(String email, String pass) {
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(GirisActivity.this,AnaActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Id veya Şifre Yanlış Girildi",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}