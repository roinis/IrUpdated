import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.out;

public class Posting {
    private HashMap<String,String> postingFilePaths;
    private int numberOfDocs ;
    private String currentPostingPath;
    private List<File> postingPaths;
    private int counterForPostin;
    private Index indexer;
    private int firstTime;


    public Posting(){
        postingFilePaths=new HashMap<>();
        counterForPostin=0;
        numberOfDocs = 0;
        currentPostingPath ="";
        postingPaths = new ArrayList<>();
        firstTime=0;
    }


    public String CreateNewPosing(){
        return "";
    }

    public void createNewFile(){
        createFile(System.getProperty("user.dir"));
    }

    public void createFile(String path){
        counterForPostin++;
        File file = new File(path + "\\" + "Posting"+counterForPostin+".txt");
        postingPaths.add(file);
    }

    public void addDocToPosting(List<String> allTermsInDoc){
        if(firstTime==0){
            firstTime=1;
            createNewFile();
        }
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
        //List<List<String>> docPostingTerms = new ArrayList<>();
        List<String> postingTerms = new ArrayList<>();
        for (String docID: terms.keySet()){
            for(String term:terms.get(docID).keySet()){
                postingTerms.add(term + "," + docID + "," + terms.get(docID).get(term) + "|");
            }
        }
        Collections.sort(postingTerms);
        return postingTerms;
    }

    public void mergePosting(String firstPostingPath,String secondPostingPath){
        String postingLine = "";
        List<String> firstPostingList = new ArrayList<>();
        List<String> secondPostingList = new ArrayList<>();
        List<String> mergedPosting = new ArrayList<>();
      //  List<List<String>> mergedPostingCompress = new ArrayList<>();
        BufferedReader reader = null;
        int firstPostingPointer = 0;
        int secondPostingPointer = 0;
        try {
            reader = new BufferedReader(new FileReader(firstPostingPath));
            while((postingLine = reader.readLine()) != null){
                firstPostingList.add(postingLine);
            }
            reader = new BufferedReader(new FileReader(secondPostingPath));
            while((postingLine = reader.readLine()) != null){
                secondPostingList.add(postingLine);
            }
            while (firstPostingPointer < firstPostingList.size() &&
                    secondPostingPointer < secondPostingList.size()){
                if(firstPostingList.get(firstPostingPointer).compareTo(secondPostingList.get(secondPostingPointer)) < 0){
                    mergedPosting.add(firstPostingList.get(firstPostingPointer));
                    firstPostingPointer++;
                }else{
                    mergedPosting.add(secondPostingList.get(secondPostingPointer));
                    secondPostingPointer++;
                }
            }
            for (int i = firstPostingPointer; i < firstPostingList.size(); i ++){
                mergedPosting.add(firstPostingList.get(firstPostingPointer));
            }
            for (int i = secondPostingPointer; i < secondPostingList.size(); i ++){
                mergedPosting.add(secondPostingList.get(secondPostingPointer));
            }

            addDocToPosting(mergedPosting);


        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
