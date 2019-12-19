import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class which represent document
 */
public class Doc {

    private int numOfwords;
    private int mostFreqWord;
    private String docID;
    private HashMap<String,Integer> termsAndFrequency;

    /**
     * Constructor which initializing the object
     * @param docID
     * @param numOfwords
     */
    public Doc(String docID, int numOfwords) {
        this.numOfwords = numOfwords;
        this.mostFreqWord = 0;
        this.docID = docID;
    }

    /**
     * Setter for the terms in the doc and the frequencies of each term
     * @param termsAndFrequency
     */
    public void setTermsAndFrequency(HashMap<String, Integer> termsAndFrequency) {
        this.termsAndFrequency = termsAndFrequency;
    }


    /**
     * Getter for the terms frequencies hashmap
     * @return hashamap
     */
    public HashMap<String, Integer> getTermsAndFrequency() {
        return termsAndFrequency;
    }

    /**
     * Getter for number of terms in the doc
     * @return
     */
    public int getNumOfwords() {
        return numOfwords;
    }

    /**
     * Setter for the number of terms
     * @param numOfwords
     */
    public void setNumOfwords(int numOfwords) {
        this.numOfwords = numOfwords;
    }

    /**
     * Getter for the most frequent word
     * @return number
     */
    public int getMostFreqWord() {
        return mostFreqWord;
    }

    /**
     * Setter for the most frequent word
     */
    public void setMostFreqWord(int mostFreqWord) {
        this.mostFreqWord = mostFreqWord;
    }

    /**
     * Getter for the doc ID
     * @return string
     */
    public String getDocID() {
        return docID;
    }

    /**
     * Setter for the do ID
     * @param docID
     */
    public void setDocID(String docID) {
        this.docID = docID;
    }
}
