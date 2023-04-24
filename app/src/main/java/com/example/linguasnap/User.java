package com.example.linguasnap;

public class User {
    // public String email;
    public String inputtext;
    public String translatetext;
    public User(){};
    public User(String inputtext,String translatetext){
        this.inputtext=inputtext;
        this.translatetext=translatetext;
    }
  /*  public String getEmail() {
        return email;
    } */

 /*   public void setEmail(String email) {
        this.email = email;
    }*/

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
