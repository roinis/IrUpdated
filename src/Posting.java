import javafx.util.Pair;

import java.io.*;
import java.util.*;

import static java.lang.System.out;

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
                for (String postingTerm : allTermsInDoc) {
                    out.write(postingTerm + "\n");
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

    public void alphaBetMergeSort(){
        for(String chars: alphaBet){
            mergePosting(chars);
        }
    }



    public void mergePosting(String chars){
        String postingLine = "";
        List<String> firstPostingList = new ArrayList<>();
        List<String> secondPostingList = new ArrayList<>();
        List<String> combinedTerms;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(postingPaths.get(0).getAbsolutePath()));
            while ((postingLine = reader.readLine()) != null) {
                if(alphaBetContains(postingLine)==0)
                    firstPostingList.add(postingLine);
            }
            for (int i = 1; i < postingPaths.size(); i++) {
                reader = new BufferedReader(new FileReader(postingPaths.get(i).getAbsolutePath()));
                while ((postingLine = reader.readLine()) != null) {
                    if(postingLine.charAt(0) >= chars.charAt(0) && postingLine.charAt(0) <= chars.charAt(chars.length() - 1))
                        secondPostingList.add(postingLine);
                }
                firstPostingList = twoWayMergeSort(firstPostingList,secondPostingList);
                secondPostingList = new ArrayList<>();
            }
            createNewFile(chars);
            combinedTerms = combineTerms(firstPostingList);
            postingDictionary.add(combinedTerms);
            addDocToPosting(combinedTerms);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int alphaBetContains(String line){
        if(line.charAt(0) >= alphaBet.get(0).charAt(0) && line.charAt(0) <= alphaBet.get(0).charAt(alphaBet.get(0).length() - 1)){
           return 0;
        }
        if(line.charAt(0) >= alphaBet.get(1).charAt(0) && line.charAt(0) <= alphaBet.get(1).charAt(alphaBet.get(1).length() - 1)){
            return 1;
        }
        if(line.charAt(0) >= alphaBet.get(2).charAt(0) && line.charAt(0) <= alphaBet.get(2).charAt(alphaBet.get(2).length() - 1)){
            return 2;
        }
        if(line.charAt(0) >= alphaBet.get(3).charAt(0) && line.charAt(0) <= alphaBet.get(3).charAt(alphaBet.get(3).length() - 1)){
            return 3;
        }
        if(line.charAt(0) >= alphaBet.get(4).charAt(0) && line.charAt(0) <= alphaBet.get(4).charAt(alphaBet.get(4).length() - 1)){
            return 4;
        }
        if(line.charAt(0) >= alphaBet.get(5).charAt(0) && line.charAt(0) <= alphaBet.get(5).charAt(alphaBet.get(5).length() - 1)){
            return 5;
        }if(line.charAt(0) >= alphaBet.get(6).charAt(0) && line.charAt(0) <= alphaBet.get(6).charAt(alphaBet.get(6).length() - 1)){
            return 6;
        }if(line.charAt(0) >= alphaBet.get(7).charAt(0) && line.charAt(0) <= alphaBet.get(7).charAt(alphaBet.get(7).length() - 1)){
            return 7;
        }
        if(line.charAt(0) >= alphaBet.get(8).charAt(0) && line.charAt(0) <= alphaBet.get(8).charAt(alphaBet.get(8).length() - 1)){
            return 8;
        }
        return 0;
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


}
