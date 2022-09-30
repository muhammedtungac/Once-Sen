package com.example.oncesen2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.oncesen2.Fragment.ShuffleFragment;
import com.example.oncesen2.Fragment.BildirimFragment;
import com.example.oncesen2.Fragment.MessagesFragment;
import com.example.oncesen2.Fragment.NewKullaniciFragment;
import com.example.oncesen2.Fragment.StoriesFragment;
import com.example.oncesen2.Utils.ChangeFragment;
import com.example.oncesen2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AnaActivity extends AppCompatActivity {

    public static ChangeFragment changeFragment;
    FirebaseAuth auth;
    FirebaseUser user;
    public static BottomNavigationView navView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userId;
    static String prevString="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana);
        tanimla();
        kontrol();
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        if(prevString.equals("")){
            changeFragment = new ChangeFragment(AnaActivity.this);
            changeFragment.change(new ShuffleFragment());
            navView.setSelectedItemId(R.id.navigation_shuffle);
        }
        else if(prevString.equals("istek")){
            changeFragment = new ChangeFragment(AnaActivity.this);
            changeFragment.change(new MessagesFragment());
            navView.setSelectedItemId(R.id.navigation_message);
        }
        else if(prevString.equals("story")){
            changeFragment = new ChangeFragment(AnaActivity.this);
            changeFragment.change(new StoriesFragment());
            navView.setSelectedItemId(R.id.navigation_stories);
        }
        else {
            changeFragment = new ChangeFragment(AnaActivity.this);
            changeFragment.change(new NewKullaniciFragment());
            navView.setSelectedItemId(R.id.navigation_profile);
        }


    }
    public BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_stories:
                            selectedFragment = new StoriesFragment();
                            changeFragment.change(selectedFragment);
                            return true;
                        case R.id.navigation_message:
                            selectedFragment = new MessagesFragment();
                            changeFragment.change(selectedFragment);
                            return true;
                        case R.id.navigation_shuffle:
                            selectedFragment = new ShuffleFragment();
                            changeFragment.change(selectedFragment);
                            return true;

                        case R.id.navigation_bildirim:
                            selectedFragment = new BildirimFragment();
                            changeFragment.change(selectedFragment);
                            return true;

                        case R.id.navigation_profile:
                            selectedFragment = new NewKullaniciFragment();
                            changeFragment.change(selectedFragment);
                            return true;
                        /*case R.id.navigation_quit:
                            cik();
                            return true;*/
                    }

                    return false;
                }
            };

    public void cik() {
        auth.signOut();
        Intent intent = new Intent(getApplicationContext(), GirisActivity.class);
        startActivity(intent);
        //databaseReference.child(user.getUid()).child("userState").setValue(false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Kullanıcılar");
        databaseReference.child(user.getUid()).child("userState").setValue(false);
        finish();

    }

    public void tanimla() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        navView = (BottomNavigationView) findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Kullanıcılar");
        databaseReference.child(user.getUid()).child("userState").setValue(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Kullanıcılar");
        databaseReference.child(user.getUid()).child("userState").setValue(true);

    }

    public void kontrol() {
        if (user == null) {
            Intent intent = new Intent(AnaActivity.this, GirisActivity.class);
            startActivity(intent);
            finish();
        } else {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("Kullanıcılar");
            databaseReference.child(user.getUid()).child("userState").setValue(true);

        }
    }
    public static void previousPost(String postActivity){
        prevString=postActivity;
    }
}