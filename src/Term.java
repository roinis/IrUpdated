import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Term {

    private int idf;
    private int tf;
    private int line;
    private String term;
    private String currentDoc;

    public Term(String term) {
        this.idf = 0;
        this.tf = 0;
        this.term = term;
    }

    public int getIdf() {
        return idf;
    }

    public void setIdf(int idf) {
        this.idf = idf;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
