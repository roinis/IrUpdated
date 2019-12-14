public class Doc {

    private int numOfwords;
    private int mostFreqWord;
    private String docID;

    public Doc(String docID,int numOfwords) {
        this.numOfwords = numOfwords;
        this.mostFreqWord = 0;
        this.docID = docID;
    }

    public int getNumOfwords() {
        return numOfwords;
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
