import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Doc {

    private int numOfwords;
    private int mostFreqWord;
    private String docID;
    private int maxFrequency;

    public HashMap<String, Integer> getTermsAndFrequency() {
        return termsAndFrequency;
    }

    private HashMap<String,Integer> termsAndFrequency;



    public Doc(String docID, int numOfwords) {
        this.numOfwords = numOfwords;
        this.mostFreqWord = 0;
        this.docID = docID;
        this.maxFrequency = 0;
    }

    public void setTermsAndFrequency(HashMap<String, Integer> termsAndFrequency) {
        this.termsAndFrequency = termsAndFrequency;
    }



    public int getNumOfwords() {
        return numOfwords;
    }

    public int getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    public void setNumOfwords(int numOfwords) {
        this.numOfwords = numOfwords;
    }

    public int getMostFreqWord() {
        return mostFreqWord;
    }

    public void setMostFreqWord(int mostFreqWord) {
        this.mostFreqWord = mostFreqWord;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
