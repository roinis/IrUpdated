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


    public Index(boolean stem) throws IOException {
        this.stem=stem;
        dictionary=new HashMap<>();
        readFile=new ReadFile("check");
        parser = new Parse(readFile.getStopWords());
        stemmer=new Stemmer();
        posting = new Posting();
        documents = new HashMap<>();
    }

    public void startIndex(){
        HashMap<String,String> docs=null;
        HashMap<String,Integer> termFreqInDoc = null;
        List<Pair<String,String>> terms=null;
        Term newTerm = null;
        int numberOfDocs = 0;
        while(readFile.readFewFiles()){
            docs = readFile.getDocuments();
            for(String key : docs.keySet()){
                terms = parser.parse(docs.get(key));
                if(stem){
                }
                documents.put(key,new Doc(key,terms.size()));
                termFreqInDoc = getNumOfTermFreq(terms);
                for (Pair<String,String> termPosition: terms) {
                    newTerm = saveTerm(termPosition,key);
                }
                counter++;
                posting.addDocToPosting(termFreqInDoc,key);
            }
            posting.createNewFile();
            docs.clear();
        }

    }

    private  HashMap<String,Integer> getNumOfTermFreq(List<Pair<String,String>> terms){
        HashMap<String,Integer> termFreqInDoc = new HashMap<>();
        int freq = 0;
        for (Pair<String,String> termFreq:terms) {
            if(termFreqInDoc.containsKey(termFreq.getKey().toLowerCase())){
                freq = termFreqInDoc.get(termFreq.getKey().toLowerCase());
                freq++;
                termFreqInDoc.put(termFreq.getKey().toLowerCase(),freq);
            }else{
                termFreqInDoc.put(termFreq.getKey().toLowerCase(),1);
            }
        }
        return termFreqInDoc;


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
