package com.example.oncesen2.Utils;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ReportFragment;

import com.example.oncesen2.R;

public class ChangeFragment {
    private Context context;
    public ChangeFragment(Context context){

        this.context=context;
    }
    public void change(Fragment fragment){
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout1,fragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();


    }


    public void changeWithParameter(Fragment fragment,String userId){
        Bundle bundle=new Bundle();
        bundle.putString("userId",userId);
        fragment.setArguments(bundle);
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout1,fragment,"fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    
}
