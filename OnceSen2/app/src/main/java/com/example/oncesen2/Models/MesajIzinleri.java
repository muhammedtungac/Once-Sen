package com.example.oncesen2.Models;

public class MesajIzinleri {
    String anonResim,anonIsim,engelDurum,kontrol,medyaIzinDurum,mesajDurum;
    public MesajIzinleri(){

    }

    public MesajIzinleri(String anonResim, String anonIsim, String engelDurum, String kontrol, String medyaIzinDurum, String mesajDurum) {
        this.anonResim = anonResim;
        this.anonIsim = anonIsim;
        this.engelDurum = engelDurum;
        this.kontrol = kontrol;
        this.medyaIzinDurum = medyaIzinDurum;
        this.mesajDurum = mesajDurum;
    }

    public String getAnonResim() {
        return anonResim;
    }

    public void setAnonResim(String anonResim) {
        this.anonResim = anonResim;
    }

    public String getAnonIsim() {
        return anonIsim;
    }

    public void setAnonIsim(String anonIsim) {
        this.anonIsim = anonIsim;
    }

    public String getEngelDurum() {
        return engelDurum;
    }

    public void setEngelDurum(String engelDurum) {
        this.engelDurum = engelDurum;
    }

    public String getKontrol() {
        return kontrol;
    }

    public void setKontrol(String kontrol) {
        this.kontrol = kontrol;
    }

    public String getMedyaIzinDurum() {
        return medyaIzinDurum;
    }

    public void setMedyaIzinDurum(String medyaIzinDurum) {
        this.medyaIzinDurum = medyaIzinDurum;
    }

    public String getMesajDurum() {
        return mesajDurum;
    }

    public void setMesajDurum(String mesajDurum) {
        this.mesajDurum = mesajDurum;
    }
}
