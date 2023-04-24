package com.example.linguasnap.model;

import java.util.ArrayList;

public class GrammarBotResponse {
    public Language language;
    public ArrayList<Match> matches;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }
}