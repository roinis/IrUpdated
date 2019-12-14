import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class main {

    public static void main(String args[]) throws IOException {
      /**  int checkr = 0;
        ReadFile rf = new ReadFile("corpus");
        int counter  =0;
        int docSize = 0;
        Parse pr = new Parse(rf.getStopWords());
        HashSet<String> check =rf.getStopWords();
        HashMap<String,String> checkDoc=null;
        List<Pair<String,String>> terms=null;
        while(rf.readFewFiles()){
            checkDoc = rf.getDocuments();
            counter++;
            docSize=docSize+checkDoc.size();
            for(String s : checkDoc.keySet()){
                System.out.println(s);

                terms = pr.parse(checkDoc.get(s),s);
            }
            checkDoc.clear();
        }
        System.out.println(docSize);
        System.out.println(counter);
        pr.printTotalWords();**/


      Index indexer = new Index(false);
      indexer.startIndex();

    }
}
