package com.example.linguasnap.utils;

import com.example.linguasnap.model.Match;
import com.example.linguasnap.model.Replacement;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;

public class TextCorrection {

    //Only use this method to get SuggestedText
    public static String getSuggestedText(ArrayList<Match> matches) {
        String result = null;
        if(matches !=null) {
            result = matches.get(0).getSentence();
            for (Match match : matches) {
                int startPosition = match.getOffset();
                int length = match.getLength();
                String subString = match.getSentence().substring(startPosition, startPosition + length);

                result= result.replace(subString,getReplacedText(subString,match));
            }
        }
        return result;
    }

    public static String getReplacedText(String toBeReplaced, Match match) {
        ArrayList<Replacement> replacements = match.getReplacements();

        int minDistance = Integer.MAX_VALUE;
        String mostSimilar = null;
        LevenshteinDistance ld = new LevenshteinDistance();

        for (Replacement replacement : replacements) {
            int distance = ld.apply(toBeReplaced, replacement.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                mostSimilar = replacement.getValue();
            }
        }
        return mostSimilar;
    }
}
