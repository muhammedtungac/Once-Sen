package com.example.oncesen2.Models;

public class Kullanicilar {
    private String kullaniciIsim, hakkimda, yas, resim, cinsiyet,anonimKullaniciIsmi,kayitSaat,kayitTarih,mesajSayisi;
    private Object userState;


    public Kullanicilar() {

    }

    public Kullanicilar(String anonimKullaniciIsmi,String cinsiyet,String hakkimda,String kayitSaat, String kayitTarih, String kullaniciIsim, String mesajSayisi, String resim,Object userState,String yas) {
        this.kullaniciIsim = kullaniciIsim;
        this.hakkimda = hakkimda;
        this.yas = yas;
        this.resim = resim;
        this.cinsiyet = cinsiyet;
        this.anonimKullaniciIsmi = anonimKullaniciIsmi;
        this.kayitSaat = kayitSaat;
        this.kayitTarih = kayitTarih;
        this.userState = userState;
        this.mesajSayisi = mesajSayisi;
    }

    public String getKullaniciIsim() {
        return kullaniciIsim;
    }

    public void setKullaniciIsim(String kullaniciIsim) {
        this.kullaniciIsim = kullaniciIsim;
    }

    public String getHakkimda() {
        return hakkimda;
    }

    public void setHakkimda(String hakkimda) {
        this.hakkimda = hakkimda;
    }

    public String getYas() {
        return yas;
    }

    public void setYas(String yas) {
        this.yas = yas;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getAnonimKullaniciIsmi() {
        return anonimKullaniciIsmi;
    }

    public void setAnonimKullaniciIsmi(String anonimKullaniciIsmi) {
        this.anonimKullaniciIsmi = anonimKullaniciIsmi;
    }

    public String getKayitSaat() {
        return kayitSaat;
    }

    public void setKayitSaat(String kayitSaat) {
        this.kayitSaat = kayitSaat;
    }

    public String getKayitTarih() {
        return kayitTarih;
    }

    public void setKayitTarih(String kayitTarih) {
        this.kayitTarih = kayitTarih;
    }

    public Object getUserState() {
        return userState;
    }

    public void setUserState(Object userState) {
        this.userState = userState;
    }

    public String getMesajSayisi() {
        return mesajSayisi;
    }

    public void setMesajSayisi(String mesajSayisi) {
        this.mesajSayisi = mesajSayisi;
    }

    @Override
    public String toString() {
        return "Kullanicilar{" +
                "kullaniciIsim='" + kullaniciIsim + '\'' +
                ", hakkimda='" + hakkimda + '\'' +
                ", yas='" + yas + '\'' +
                ", resim='" + resim + '\'' +
                ", cinsiyet='" + cinsiyet + '\'' +
                '}';
    }
}
