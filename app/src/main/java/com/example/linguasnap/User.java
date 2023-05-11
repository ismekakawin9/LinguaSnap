package com.example.linguasnap;

public class User {
    public String from;
    public String to;
    public String inputtext;
    public String translatetext;
    public int thich;
    public User(String from, String to, String textinput, String translatetext){
        this.from= from;
        this.to = to;
        this.inputtext=inputtext;
        this.translatetext=translatetext;
    };
    public User(String from, String to,String inputtext, String translatetext,int thich){
        this.from= from;
        this.to = to;
        this.inputtext=inputtext;
        this.translatetext=translatetext;
        this.thich= thich;
    }

    public int getThich(){return  thich;}

    public void setThich(int thich) {
        this.thich = thich;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getInputtext() {
        return inputtext;
    }

    public void setInputtext(String inputtext) {
        this.inputtext = inputtext;
    }

    public String getTranslatetext() {
        return translatetext;
    }

    public void setTranslatetext(String translatetext) {
        this.translatetext = translatetext;
    }
}
