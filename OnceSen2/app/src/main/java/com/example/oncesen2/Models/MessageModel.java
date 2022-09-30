package com.example.oncesen2.Models;

public class MessageModel {
    String type, time, text, from, anonimKullaniciIsmi, seen, date,visibility;


    public MessageModel() {

    }

    public MessageModel(String anonimKullaniciIsmi, String date, String from, String seen, String text, String time, String type,String visibility) {
        this.date = date;
        this.type = type;
        this.time = time;
        this.text = text;
        this.seen = seen;
        this.from = from;
        this.visibility=visibility;
        this.anonimKullaniciIsmi = anonimKullaniciIsmi;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getAnonimKullaniciIsmi() {
        return anonimKullaniciIsmi;
    }

    public void setAnonimKullaniciIsmi(String anonimKullaniciIsmi) {
        this.anonimKullaniciIsmi = anonimKullaniciIsmi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
