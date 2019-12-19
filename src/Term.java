import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class which represing term/token object - used to save parameters of token - such as tf
 */
public class Term {

    private int tf;
    private int line;
    private String term;

    /**
     * Constructor for initializing the object
     * @param term
     */
    public Term(String term) {
        line = -1;
        this.tf = 0;
        this.term = term;
    }

    /**
     * Constructor for initializing the object
     * @param term
     */
    public Term(String term,int tf){
        this.tf=tf;
        this.term=term;
        line = -1;
    }

    /**
     * Getter for the TF
     * @return tf
     */
    public int getTf() {
        return tf;
    }

    /**
     * Setter for tf
     * @param tf
     */
    public void setTf(int tf) {
        this.tf = tf;
    }

    /**
     * Getter for line in posting
     * @return line number
     */
    public int getLine() {
        return line;
    }

    /**
     * Setter for line number in the posting file
     * @param line
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Getter for the term token
     * @return
     */
    public String getTerm() {
        return term;
    }

    /**
     * setter for the term token
     * @param term
     */
    public void setTerm(String term) {
        this.term = term;
    }

}
