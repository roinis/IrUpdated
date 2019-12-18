import javafx.util.Pair;

import java.io.*;
import java.util.*;

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
    private List<Term> specificDocTerm;

    public List<Term> getDictionaryListSorted() {
        return dictionaryListSorted;
    }

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

    public void clearIndex(){
        this.dictionary.clear();
        this.documents.clear();
        this.termFreqInDoc.clear();
    }

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
        System.out.println("Dictionary size is:");
        System.out.println(dictionary.size());
        posting.mergePostingFiles();
        dictionary = posting.splitPostingToAlphaBet(dictionary);
        saveDictionaryToDisk();
        printSpecificDocToFile();
    }

    public void printNumbersInDictionary(){
        int counter = 0;
        for(String term:dictionary.keySet()){
            if(term.matches(".*\\d+.*")){
                counter++;
            }
        }
        System.out.println("");
        System.out.println("The amount of terms with numbers is: "+counter);
    }

    private void printSpecificDocToFile(){

        HashMap<String,Integer> termsAndFreq = documents.get(" FBIS3-3366 ").getTermsAndFrequency();
        List<String> terms = new ArrayList<>();
        for(String term:termsAndFreq.keySet()){
            terms.add(term + " " + String.valueOf(termsAndFreq.get(term)));
        }
        System.out.println("");
        System.out.println("The terms in Document FBIS3-3366 are:");
        Collections.sort(terms);
            for(String term:terms){
                System.out.println(term);
            }
    }

    private void sortDict(){
        dictionaryListSorted = new ArrayList<>();
        for (String termString:dictionary.keySet()) {
            dictionaryListSorted.add(dictionary.get(termString));
        }
        Collections.sort(dictionaryListSorted, new TermComprator());
    }


    private void saveDictionaryToDisk(){
        sortDict();
        FileWriter dict= null;
        posting.createNewFile("Dictionary");
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

    public void createDictionaryFromFile(){
        dictionary = new HashMap<>();
        BufferedReader reader = null;
        String postingLine;
        String []splitedLine;
        try {
            reader = new BufferedReader(new FileReader(postingPath+"\\Dictionary.txt"));
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

    public void topTenTerms(){
        List<String> termsAndFreq = new ArrayList<>();
        String[] mostTopTenFreq = new String[10];
        String[] leastTopTenFreq = new String[10];

        for(String term:dictionary.keySet()){
            termsAndFreq.add(dictionary.get(term).getTerm() + " #" + dictionary.get(term).getTf());
        }

        Collections.sort(termsAndFreq,new FreqComparator());
        FileWriter fw=null;
        try{
            String str = "";
            fw = new FileWriter(System.getProperty("user.dir")+"\\zipf.csv", true);
            for(int i=termsAndFreq.size()-1;i>-1;i--){
                fw.append(termsAndFreq.get(i).split("#")[1]+"\n");
                str = termsAndFreq.get(i).split("#")[1]+"\n";
            }
            fw.flush();
            fw.close();
        }
        catch(Exception e){
            System.out.println(e);
        }

        for(int i = 0; i < 10; i++){
            leastTopTenFreq[i] = termsAndFreq.get(i);
            mostTopTenFreq[i] = termsAndFreq.get(termsAndFreq.size() - (i + 1));
        }
        System.out.println("");

        System.out.println("The top ten terms with most frequencies are:");
        for (int i = 0; i<10; i++){
            System.out.println(mostTopTenFreq[i].replaceAll("#",""));
        }
        System.out.println("");
        System.out.println("The top ten terms with least frequencies are:");
        for (int i = 0; i<10; i++){
            System.out.println(leastTopTenFreq[i].replaceAll("#",""));
        }
    }

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
