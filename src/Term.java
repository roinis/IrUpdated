import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Term {

    private int tf;
    private int line;
    private String term;

    public Term(String term) {
        line = -1;
        this.tf = 0;
        this.term = term;
    }

    public Term(String term,int tf){
        this.tf=tf;
        this.term=term;
        line = -1;
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
