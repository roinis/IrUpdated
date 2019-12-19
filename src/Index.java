import javafx.util.Pair;

import java.io.*;
import java.util.*;

/**
 * Class which represent the index  - saves the dictionary and starts all the process
 */
public class Index {
    public HashMap<String, Term> getDictionary() {
        return dictionary;
    }
    private HashMap<String,Term> dictionary;
    private HashMap<String,Doc> documents;
    private boolean stem;
    private Parser parser;
    private String corpusPath;
    private ReadFile readFile;
    private Stemmer stemmer;
    private Posting posting;
    private String postingPath;
    private HashMap<String,HashMap<String,Integer>> termFreqInDoc;
    private List<Term> dictionaryListSorted;


    /**
     * Constructor to initialize the index
     * @param stem
     * @param corpusPath
     * @param postingPath
     * @throws IOException
     */
    public Index(boolean stem, String corpusPath, String postingPath) throws IOException {
        this.stem=stem;
        dictionary=new HashMap<>();
        this.corpusPath=corpusPath;
        this.postingPath=postingPath;
        readFile=new ReadFile(this.corpusPath);
        parser = new Parser(readFile.getStopWords());
        posting = new Posting(postingPath);
        documents = new HashMap<>();
        termFreqInDoc = new HashMap<>();
    }

    /**
     * Method to clear the ram when clicking on "reset" button
     */
    public void clearIndex(){
        this.dictionary.clear();
        this.documents.clear();
        this.termFreqInDoc.clear();
    }

    /**
     * Main method of index which creates the index using the read file and parsing classes
     * and after that creating the psoting
     */
    public void startIndex(){
        HashMap<String,String> docs=null;
        List<String> terms=null;
        boolean continueIteration = true;
        while(continueIteration){
            if(!readFile.readFewFiles())
                continueIteration = false;
            docs = readFile.getDocuments();
            if(docs.size() == 0)
                break;
            posting.createNewFile("");
            for(String docID : docs.keySet()){
                terms = parser.parse(docs.get(docID));
                if(stem){
                    List<String> stemmedTerms = new ArrayList<>();
                    for(String term:terms){
                        if(term.matches("\\w+")) {
                            stemmer = new Stemmer();
                            stemmer.add(term.toCharArray(), term.length());
                            stemmer.stem();
                            stemmedTerms.add(stemmer.toString());
                        }
                    }
                    documents.put(docID,new Doc(docID,stemmedTerms.size()));
                    getNumOfTermFreq(docID,stemmedTerms);
                    for (String term: stemmedTerms) {
                        saveTerm(term);
                    }
                }else {
                    documents.put(docID, new Doc(docID, terms.size()));
                    getNumOfTermFreq(docID, terms);
                    for (String term : terms) {
                        saveTerm(term);
                    }
                }
            }
            posting.addDocToPosting(posting.sortPostingElements(termFreqInDoc));
            termFreqInDoc = new HashMap<>();
            docs.clear();
        }

        posting.mergePostingFiles();
        dictionary = posting.splitPostingToAlphaBet(dictionary,stem);
        saveDictionaryToDisk();
    }

    /**
     * Method to read sort the dictionary alphabetcally
     */
    private void sortDict(){
        dictionaryListSorted = new ArrayList<>();
        for (String termString:dictionary.keySet()) {
            dictionaryListSorted.add(dictionary.get(termString));
        }
        Collections.sort(dictionaryListSorted, new TermComprator());
    }

    /**
     * Getter for the sorted Dictionary
     * @return List of terms
     */
    public List<Term> getDictionaryListSorted() {
        return dictionaryListSorted;
    }

    /**
     * Method which saves the final dictionary to disk for easy retrieving
     */
    private void saveDictionaryToDisk(){
        sortDict();
        FileWriter dict= null;
        String fileName;
        if(!stem)
            fileName = "Dictionary";
        else
            fileName = "Dictionary-s";
        posting.createNewFile(fileName);
        try {
            dict = new FileWriter(posting.getPostingPaths().get(posting.getPostingPaths().size() -1), true);
            for (Term term:dictionaryListSorted) {
                dict.write(term.getTerm() + "#" + String.valueOf(term.getTf()) + "\n");
            }
            dict.flush();
            dict.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     *method which loads the dictionary from file
     */
    public void createDictionaryFromFile(){
        dictionary = new HashMap<>();
        BufferedReader reader = null;
        String postingLine;
        String []splitedLine;
        String fileName;
        try {
            if(!stem)
                fileName = "\\Dictionary.txt";

            else
                fileName = "\\Dictionary-s.txt";
            reader = new BufferedReader(new FileReader(postingPath+fileName));
            while ((postingLine = reader.readLine()) != null) {
                splitedLine = postingLine.split("#");
                if (splitedLine.length == 0)
                    continue;
                try{
                    int tf = Integer.valueOf(splitedLine[1]);
                    dictionary.put(splitedLine[0].toLowerCase(),new Term(splitedLine[0],tf));
                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
            sortDict();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * finction which updates the number of appearances of term in a doc
     * @param docID
     * @param terms
     */
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
        int max = 0;
        for (Map.Entry<String, Integer> entry : termsFreqMap.entrySet()) {
            if (entry.getValue().compareTo(max) > 0) {
                max = entry.getValue();
            }
        }if(docID.matches(" FBIS3-3366 "))
            this.documents.get(docID).setTermsAndFrequency(termsFreqMap);
        this.documents.get(docID).setMostFreqWord(max);
        termFreqInDoc.put(docID,termsFreqMap);
    }


    /**
     * Method to clear the posting
     * @throws IOException
     */
    public void resetPosting() throws IOException {
        List<File> postingPaths = this.posting.getPostingPaths();
        for(File file: postingPaths){
            file.delete();
        }
        parser.clearParser();
        posting.clearPosting();
        readFile.clearFileReader();
        this.clearIndex();
    }


    /**
     * function which create new term for each token
     * @param term
     */
    private  void saveTerm(String term){
        String termLowerCase = term.toLowerCase();
        Term newTerm = null;
        if(dictionary.containsKey(termLowerCase)){
            newTerm = dictionary.get(termLowerCase);
           if(!term.equals(newTerm.getTerm())){
               newTerm.setTerm(termLowerCase);
           }
        }else{
            newTerm = new Term(term);
            dictionary.put(termLowerCase,newTerm);
        }
        newTerm.setTf(newTerm.getTf() + 1);
    }


    /**
     * Comparators
     */
    public class FreqComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            String firstTerm = (String) o1;
            String secondTerm = (String) o2;
            int firstTermFreq = Integer.valueOf(firstTerm.split("#")[1]);
            int secondTermFreq = Integer.valueOf(secondTerm.split("#")[1]);
            return firstTermFreq - secondTermFreq;
        }
    }

    public class TermComprator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Term firstTerm = (Term) o1;
            Term secondTerm = (Term) o2;
            return firstTerm.getTerm().compareTo(secondTerm.getTerm());
        }
    }


}
