package com.example.oncesen2.Models;

public class InboxModel {
    String messageFrom, messageText, messageTime, messageType, messageSeen,anonimKullaniciIsmi;

    public InboxModel() {

    }

    public InboxModel(String anonimKullaniciIsmi,String messageFrom,String messageSeen, String messageText, String messageTime, String messageType) {
        this.messageFrom = messageFrom;
        this.messageText = messageText;
        this.messageTime = messageTime;
        this.messageType = messageType;
        this.messageSeen = messageSeen;
        this.anonimKullaniciIsmi=anonimKullaniciIsmi;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageSeen() {
        return messageSeen;
    }

    public void setMessageSeen(String messageSeen) {
        this.messageSeen = messageSeen;
    }

    public String getAnonimKullaniciIsmi() {
        return anonimKullaniciIsmi;
    }

    public void setAnonimKullaniciIsmi(String anonimKullaniciIsmi) {
        this.anonimKullaniciIsmi = anonimKullaniciIsmi;
    }
}
