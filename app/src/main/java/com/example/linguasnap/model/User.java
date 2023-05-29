package com.example.linguasnap.model;

public class User {
    public String keyid;
    public String from;
    public String to;
    public String inputtext;
    public String translatetext;
    public int like;
    public User(){};
    public User(String keyid, String from, String to,String inputtext, String translatetext, int like){
        this.keyid= keyid;
        this.from= from;
        this.to = to;
        this.inputtext=inputtext;
        this.translatetext=translatetext;
        this.like= like;
    }

    public String getKeyid() {
        return keyid;
    }

    public int getLike(){return like;}

    public void setKeyid(String key) {
        this.keyid = key;
    }

    public void setLike(int like) {
        this.like = like;
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
