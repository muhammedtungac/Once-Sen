package com.example.oncesen2.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.oncesen2.R;

public class HakkimizdaFragment extends Fragment {
    EditText mailAdres,mailKonu,mailIcerik;
    Button mailGonder;
    String adresText,konuText,icerikText;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_hakkimizda, container, false);
        tanimla();
        mailGonderme();
        return view;
    }
    public void tanimla(){
        mailAdres=(EditText)view.findViewById(R.id.mail);
        mailIcerik=(EditText)view.findViewById(R.id.mailIcerik);
        mailKonu=(EditText)view.findViewById(R.id.mailKonu);
        mailGonder=(Button)view.findViewById(R.id.gonderButton);
    }
    public void mailGonderme(){
        icerikText=mailIcerik.getText().toString();
        konuText=mailKonu.getText().toString();
        adresText=mailAdres.getText().toString();
        mailGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    //intent.putExtra(Intent.EXTRA_EMAIL,new String[]{adresText,konuText,icerikText}); Birden fazla mail için
                    intent.putExtra(Intent.EXTRA_EMAIL,adresText);
                    intent.putExtra(Intent.EXTRA_SUBJECT,konuText);
                    intent.putExtra(Intent.EXTRA_TEXT,icerikText);
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent,"Mail Gönderiniz !"));
                    startActivity(intent);
                }
                catch (Exception e){
                    System.out.println(""+e);
                }
            }
        });
    }
}