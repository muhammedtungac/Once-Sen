package com.example.oncesen2.Utils;

import com.example.oncesen2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomName {
    public static String getSaltString(){
        String SALTCHARS="abcdefghijklmnoprstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt=new StringBuilder();
        Random rnd=new Random();
        while (salt.length()<18){
            int index=(int) (rnd.nextFloat()*SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr=salt.toString();
        return saltStr;
    }
    public static String setPictures() {
        List<String> list=new ArrayList<>();
        list.add("https://firebasestorage.googleapis.com/v0/b/once-sen-project2.appspot.com/o/AnonimKullaniciResimleri%2Fanonimuserpics1.png?alt=media&token=98858a86-f117-4936-ae1c-cd3cffd1920f");
        list.add("https://firebasestorage.googleapis.com/v0/b/once-sen-project2.appspot.com/o/AnonimKullaniciResimleri%2Fanonimuserpics2.png?alt=media&token=4706db68-b685-420e-8732-bc5018ec8b82");
        list.add("https://firebasestorage.googleapis.com/v0/b/once-sen-project2.appspot.com/o/AnonimKullaniciResimleri%2Fanonimuserpics3.png?alt=media&token=2b00b128-22fe-4f36-88e0-293de493ac31");
        list.add("https://firebasestorage.googleapis.com/v0/b/once-sen-project2.appspot.com/o/AnonimKullaniciResimleri%2Fanonimuserpics4.png?alt=media&token=ad5e921a-2893-4a41-8b82-acf91e756b76");
        list.add("https://firebasestorage.googleapis.com/v0/b/once-sen-project2.appspot.com/o/AnonimKullaniciResimleri%2Fanonimuserpics5.png?alt=media&token=6c6f2a41-c526-4589-b722-fe12633f3905");
        list.add("https://firebasestorage.googleapis.com/v0/b/once-sen-project2.appspot.com/o/AnonimKullaniciResimleri%2Fanonimuserpics6.png?alt=media&token=71d7b83a-e5a2-41b7-87a1-6f8b24113478");
        int size=list.size();
        Random rnd=new Random();
        return list.get(rnd.nextInt(size));
    }


}
