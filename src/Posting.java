import java.io.*;
import java.util.ArrayList;
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

    public void addDocToPosting(HashMap<String,HashMap<String,Integer>> allTermsInDoc){
        if(firstTime==0){
            firstTime=1;
            createNewFile();
        }
        try {
        FileWriter out = new FileWriter(postingPaths.get(postingPaths.size()-1).getPath(),true);
        for(String docID : allTermsInDoc.keySet()) {
            for (String term : allTermsInDoc.get(docID).keySet()) {
                out.write(term + "," + docID + "," + allTermsInDoc.get(docID).get(term) + " | ");
                out.write("\n");

            }
        }
        out.flush();
        out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
