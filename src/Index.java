import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
        readFile=new ReadFile("corpus");
        parser = new Parse(readFile.getStopWords());
        stemmer=new Stemmer();
        posting = new Posting();
        documents = new HashMap<>();
        termFreqInDoc = new HashMap<>();
    }

    public void startIndex(){
        HashMap<String,String> docs=null;

        List<Pair<String,String>> terms=null;
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
                for (Pair<String,String> termPosition: terms) {
                    newTerm = saveTerm(termPosition,docID);
                }
                counter++;

            }
            posting.addDocToPosting(termFreqInDoc);
            termFreqInDoc = new HashMap<>();
            posting.createNewFile();
            docs.clear();
        }

    }

    private  void getNumOfTermFreq(String docID ,List<Pair<String,String>> terms){
        HashMap<String,Integer> termsFreqMap = new HashMap<>();
        int freq = 0;
        for (Pair<String,String> termFreq:terms) {
            if(termsFreqMap.containsKey(termFreq.getKey().toLowerCase())){
                freq = termsFreqMap.get(termFreq.getKey().toLowerCase());
                freq++;
                termsFreqMap.put(termFreq.getKey().toLowerCase(),freq);
            }else{
                termsFreqMap.put(termFreq.getKey().toLowerCase(),1);
            }
        }
        termFreqInDoc.put(docID,termsFreqMap);


    }





    private Term saveTerm(Pair<String,String>termPosition,String docID){
        String termLowerCase = termPosition.getKey().toLowerCase();
        Term newTerm = null;
        if(dictionary.containsKey(termLowerCase)){
            newTerm = dictionary.get(termLowerCase);
           if(!termPosition.getKey().equals(newTerm.getTerm())){
               newTerm.setTerm(termLowerCase);
           }
        }else{
            newTerm = new Term(termPosition.getKey());
            dictionary.put(termLowerCase,newTerm);
        }
        newTerm.setIdf(newTerm.getIdf() + 1);
        return newTerm;
    }










/**
    private void addWordToHash(String termName,Document document){
        if (!terms.containsKey(termName.toLowerCase())){
            if(Character.isUpperCase(termName.charAt(0))){
                Term newTerm = new Term(termName.toUpperCase());
                newTerm.addDocument(document);
                newTerm.incrementCounter();
                terms.put(termName.toLowerCase(),newTerm);
            }
            else{
                Term newTerm = new Term(termName);
                newTerm.addDocument(document);
                newTerm.incrementCounter();
                terms.put(termName.toLowerCase(),newTerm);
            }
        }else{
            Term tempoTerm = terms.remove(termName.toLowerCase());
            if(Character.isLowerCase(tempoTerm.getTerm().charAt(0))){
                tempoTerm.incrementCounter();
                tempoTerm.addDocument(document);
                terms.put(termName.toLowerCase(),tempoTerm);
            }
            else{
                if(Character.isUpperCase(termName.charAt(0))){
                    tempoTerm.addDocument(document);
                    tempoTerm.incrementCounter();
                    terms.put(termName.toLowerCase(),tempoTerm);
                }
                else{
                    tempoTerm.setTerm(termName.toLowerCase());
                    tempoTerm.incrementCounter();
                    tempoTerm.addDocument(document);
                    terms.put(termName.toLowerCase(),tempoTerm);
                }
            }
        }
    }

    private void addNumericTermToHash(String termName, Document document) {
        if (!terms.containsKey(termName)) {
            Term newTerm = new Term(termName);
            newTerm.addDocument(document);
            newTerm.incrementCounter();
            terms.put(termName, newTerm);
        } else {
            Term tempoTerm = terms.remove(termName);
            tempoTerm.incrementCounter();
            tempoTerm.addDocument(document);
            terms.put(termName, tempoTerm);
        }
    }

**/
}
