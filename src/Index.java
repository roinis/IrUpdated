import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Index {
    private HashMap<String,Term> dictionary;
    private HashMap<String,Doc> documents;
    private boolean stem;
    private Parse parser;
    private ReadFile readFile;
    private Stemmer stemmer;
    private Posting posting;
    int counter = 0;
    private HashMap<String,HashMap<String,Integer>> termFreqInDoc;


    public Index(boolean stem) throws IOException {
        this.stem=stem;
        dictionary=new HashMap<>();
        readFile=new ReadFile("ch1");
        parser = new Parse(readFile.getStopWords());
        stemmer=new Stemmer();
        posting = new Posting();
        documents = new HashMap<>();
        termFreqInDoc = new HashMap<>();
    }

    public void startIndex(){
        HashMap<String,String> docs=null;
        HashMap<String,List<Pair<String,Integer>>> termsFreqSorted  = new HashMap<>();
        List<String> terms=null;
        Term newTerm = null;
        int numberOfDocs = 0;
        boolean continueIteration = true;
        while(continueIteration){

            if(!readFile.readFewFiles())
                continueIteration = false;
            docs = readFile.getDocuments();
            if(docs.size() == 0)
                break;
            for(String docID : docs.keySet()){
                terms = parser.parse(docs.get(docID));
                if(stem){
                }
                documents.put(docID,new Doc(docID,terms.size()));
                getNumOfTermFreq(docID,terms);
                for (String termPosition: terms) {
                    newTerm = saveTerm(termPosition,docID);
                }

            }
            posting.addDocToPosting(posting.sortPostingElements(termFreqInDoc));
            termFreqInDoc = new HashMap<>();
            posting.createNewFile();
            docs.clear();
        }
        posting.mergePosting("Posting1.txt","Posting2.txt");

    }

    private  void getNumOfTermFreq(String docID ,List<String> terms){
        HashMap<String,Integer> termsFreqMap = new HashMap<>();
        int freq = 0;
        for (String termFreq:terms) {
            if(termsFreqMap.containsKey(termFreq.toLowerCase())){
                freq = termsFreqMap.get(termFreq.toLowerCase());
                freq++;
                termsFreqMap.put(termFreq.toLowerCase(),freq);
            }else{
                termsFreqMap.put(termFreq.toLowerCase(),1);
            }
        }
        termFreqInDoc.put(docID,termsFreqMap);
    }





    private Term saveTerm(String termPosition,String docID){
        String termLowerCase = termPosition.toLowerCase();
        Term newTerm = null;
        if(dictionary.containsKey(termLowerCase)){
            newTerm = dictionary.get(termLowerCase);
           if(!termPosition.equals(newTerm.getTerm())){
               newTerm.setTerm(termLowerCase);
           }
        }else{
            newTerm = new Term(termPosition);
            dictionary.put(termLowerCase,newTerm);
        }
        newTerm.setIdf(newTerm.getIdf() + 1);
        return newTerm;
    }


}
