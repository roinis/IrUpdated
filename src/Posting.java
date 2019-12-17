import javafx.util.Pair;

import java.io.*;
import java.util.*;

import static java.lang.System.out;
import static java.lang.System.setOut;

public class Posting {
    private HashMap<String,String> postingFilePaths;
    private int numberOfDocs ;
    private String currentPostingPath;
    private List<File> postingPaths;
    private int counterForPostin;
    private Index indexer;
    private int firstTime;
    private List<String> alphaBet;
    private List<List<String>> postingDictionary;
    private final int K1 = 20000;
    private final int K2 = 60000;
    private PriorityQueue<String> minHeap;
    private int [] postingLastLine;

    public Posting(){
        postingFilePaths=new HashMap<>();
        counterForPostin=0;
        numberOfDocs = 0;
        currentPostingPath ="";
        postingPaths = new ArrayList<>();
        firstTime=0;
        alphaBet = new ArrayList<>(Arrays.asList("abc","def","ghi","jkl","nop",
                "qrs","tyu","vwx","yz"));
        postingDictionary = new ArrayList<>();
        minHeap = new PriorityQueue<>();



    }



    public void createNewFile(String name){
        createFile(System.getProperty("user.dir"),name);
    }

    public void createFile(String path,String name){
        if(!name.equals("")){
            File file = new File(path + "\\" + name+ ".txt");
            postingPaths.add(file);
        }
        else {
            counterForPostin++;
            File file = new File(path + "\\" + "Posting" + counterForPostin + ".txt");
            postingPaths.add(file);
        }
    }

    public void addDocToPosting(List<String> allTermsInDoc){
        try {
            FileWriter out = new FileWriter(postingPaths.get(postingPaths.size() - 1).getPath(), true);
            int counter = 0;
            int sizeOfList = allTermsInDoc.size();
            for (String postingTerm : allTermsInDoc) {
                if(counter < sizeOfList -1 )
                    out.write(postingTerm + "\n");
                else
                    out.write(postingTerm );
            }
            out.flush();
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> sortPostingElements(HashMap<String,HashMap<String,Integer>> terms){
        List<String> postingTerms = new ArrayList<>();
        for (String docID: terms.keySet()){
            for(String term:terms.get(docID).keySet()){
                postingTerms.add(term + "," + docID + "," + terms.get(docID).get(term) + "|");
            }
        }
        Collections.sort(postingTerms);
        return postingTerms;
    }



    public List<List<String>> getPostingDictionary(){
        return postingDictionary;
    }

    private List<String> twoWayMergeSort(List<String> firstPostingList,List<String>secondPostingList){

        int firstPostingPointer = 0;
        int secondPostingPointer = 0;
        List<String> mergedPosting = new ArrayList<>();
        while (firstPostingPointer < firstPostingList.size() &&
                secondPostingPointer < secondPostingList.size()) {
            if (firstPostingList.get(firstPostingPointer).compareTo(secondPostingList.get(secondPostingPointer)) < 0) {
                mergedPosting.add(firstPostingList.get(firstPostingPointer));
                firstPostingPointer++;

            } else {
                mergedPosting.add(secondPostingList.get(secondPostingPointer));
                secondPostingPointer++;

            }
        }
        for (int i = firstPostingPointer; i < firstPostingList.size(); i++) {
            mergedPosting.add(firstPostingList.get(i));
        }
        for (int i = secondPostingPointer; i < secondPostingList.size(); i++) {
            mergedPosting.add(secondPostingList.get(i));
        }
        return mergedPosting;

    }

    public List<String> combineTerms(List<String> terms) {
        String previousTerm = "";
        boolean firstTime = true;
        String[] splittedTerm;
        List<String> combineTerms = new ArrayList<>();
        int listCounter = -1;
        for(String term:terms){
            listCounter++;
            if(firstTime){
                previousTerm = term;
                firstTime = false;
                continue;
            }
            splittedTerm = term.split(",");
            if (previousTerm.split(",")[0].equals(splittedTerm[0])){
                previousTerm = previousTerm + splittedTerm[1] + "," + splittedTerm[2];
                if(listCounter == terms.size() - 1)
                    combineTerms.add(previousTerm);
            }else{
                combineTerms.add(previousTerm);
                previousTerm=term;
            }
        }
        return combineTerms;
    }

    private List<String> readKLinesFromDoc(String DocPath,int startIndex) {
        List<String> kLines = new ArrayList<>();
        BufferedReader reader = null;
        String postingLine = "";
        int linesCounter = 0;

        try {
            reader = new BufferedReader(new FileReader(DocPath));
            while ((postingLine = reader.readLine()) != null && kLines.size() < K1) {
                if (linesCounter >= startIndex) {
                    kLines.add(postingLine);
                }
                linesCounter++;
            }
            return kLines;

        } catch (Exception e) {
            System.out.println(e);
            return kLines;
        }
    }



    private void KWayMerge(List<List<String>> postingFilesKlines){
        List<String> termsToSave = new ArrayList<>();
        int numberOfFiles = postingPaths.size();
        int [] lineIndex = new int[numberOfFiles];
        int [] finishPostingArr = new int[numberOfFiles];
        String minWord;
        int fileWithMinWord = 0;
        boolean firstTime = true;
        int finishedPosting  = 0;
        for(List<String> files : postingFilesKlines){
            minHeap.add(files.get(0));
        }
        while(finishedPosting < numberOfFiles){
            minWord = minHeap.poll();
            termsToSave.add(minWord);
            for(int i = 0; fileWithMinWord < lineIndex.length; i++){
                fileWithMinWord = i;
                if(lineIndex[fileWithMinWord] >= postingFilesKlines.get(fileWithMinWord).size()){
                    if(finishPostingArr[fileWithMinWord] == 0) {
                        finishPostingArr[fileWithMinWord] = 1;
                        finishedPosting++;
                    }
                    if( i == lineIndex.length - 1)
                        break;
                    else
                        continue;
                }
                if(postingFilesKlines.get(fileWithMinWord).get(lineIndex[fileWithMinWord]).equals(minWord)){
                    lineIndex[fileWithMinWord]++;
                    if(lineIndex[fileWithMinWord] >= K1) {
                        postingFilesKlines.set(fileWithMinWord,readKLinesFromDoc(postingPaths.get(fileWithMinWord).getAbsolutePath(),
                                postingLastLine[fileWithMinWord]));
                        postingLastLine[fileWithMinWord]+=K1;
                        lineIndex[fileWithMinWord] = 0;
                    }
                    break;
                }
            }

            if (postingFilesKlines.get(fileWithMinWord).size() > lineIndex[fileWithMinWord])
                minHeap.add(postingFilesKlines.get(fileWithMinWord).get(lineIndex[fileWithMinWord]));
            if(termsToSave.size() > K2){
                if(firstTime) {
                    firstTime = false;
                    createNewFile("combinedList");
                }
                addDocToPosting(termsToSave);
                termsToSave = new ArrayList<>();
            }
        }
        if(firstTime) {
            firstTime = false;
            createNewFile("combinedList");
        }
        addDocToPosting(termsToSave);

    }

    public void mergePostingFiles(){
        List<List<String>> files = new ArrayList<>();
        postingLastLine = new int[postingPaths.size()];
        for(File f: postingPaths){
            files.add(readKLinesFromDoc(f.getAbsolutePath(),0));
        }
        KWayMerge(files);

    }

}

