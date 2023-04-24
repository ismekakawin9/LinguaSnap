package com.example.linguasnap.model;

import java.util.ArrayList;

public class Match{
    public ArrayList<Replacement> replacements;
    public int offset;
    public int length;
    public Context context;
    public String sentence;

    public ArrayList<Replacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(ArrayList<Replacement> replacements) {
        this.replacements = replacements;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}