package com.example.linguasnap.model;

import java.util.List;

public class Word {
    private List<Meaning> meanings;

    // Getter and setter for "meanings" field
    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    // Inner class to represent the "meaning" object
    public static class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;

        // Getter and setter for "partOfSpeech" field
        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public void setPartOfSpeech(String partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
        }

        // Getter and setter for "definitions" field
        public List<Definition> getDefinitions() {
            return definitions;
        }

        public void setDefinitions(List<Definition> definitions) {
            this.definitions = definitions;
        }

        // Inner class to represent the "definition" object
        public static class Definition {
            private String definition;
            private List<String> synonyms;
            private List<String> antonyms;

            // Getter and setter for "definition" field
            public String getDefinition() {
                return definition;
            }

            public void setDefinition(String definition) {
                this.definition = definition;
            }

            // Getter and setter for "synonyms" field
            public List<String> getSynonyms() {
                return synonyms;
            }

            public void setSynonyms(List<String> synonyms) {
                this.synonyms = synonyms;
            }

            // Getter and setter for "antonyms" field
            public List<String> getAntonyms() {
                return antonyms;
            }

            public void setAntonyms(List<String> antonyms) {
                this.antonyms = antonyms;
            }
        }
    }
}

