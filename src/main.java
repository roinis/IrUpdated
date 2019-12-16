import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class main {

    public static void main(String args[]) throws IOException {

//        List<String> stringNames = new ArrayList<>(Arrays.asList("qi, FBIS3-1761 ,3|","qi, FBIS3-1778 ,1|","qian's, FBIS3-1905 ,1|","qian, FBIS3-1569 ,1|","qian, FBIS3-1843 ,1|","snipers, FBIS3-1505 ,2|","snow, FBIS3-1077 ,1|"));
//        List<String> stringNames2 = new ArrayList<>(Arrays.asList("qi, FBIS3-1761 ,3|","qi, FBIS3-1778 ,1|","qian's, FBIS3-1905 ,1|","qian's, FBIS3-1905 ,1|","qian, FBIS3-1569 ,1|","qian, FBIS3-1843 ,1|","snipers, FBIS3-1505 ,2|","snow, FBIS3-1077 ,1|"));
//        List<String> stringNames3 = new ArrayList<>(Arrays.asList("qi, FBIS3-1761 ,3|","qi, FBIS3-1778 ,1|","qian's, FBIS3-1905 ,1|","qian, FBIS3-1569 ,1|","qian, FBIS3-1843 ,1|","snipers, FBIS3-1505 ,2|","snow, FBIS3-1077 ,1|"));
//        Posting pr = new Posting();
//        List<String> retList = pr.twoWayMergeSort(stringNames,stringNames2);
//        pr.combineTerms(retList);
//        for (String term :pr.twoWayMergeSort(retList,stringNames3)) {
//            System.out.println(term);
//        };

      Index indexer = new Index(false);
      indexer.startIndex();
    }
}
