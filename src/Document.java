import java.util.HashMap;

public class Document {
    private HashMap<Term,Integer> terms;
    private String docNum;
    private String docText;
    public Document(String docNum,String docText) {
        this.docNum = docNum;
        this.docText = docText;
        terms = new HashMap<>();
    }

    public String getDocText() {
        return docText;
    }
}
