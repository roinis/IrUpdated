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

    public void addDocToPosting(HashMap<String,Integer> allTermsInDoc,String docID){
        if(firstTime==0){
            firstTime=1;
            createNewFile();
        }
        try {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(postingPaths.get(postingPaths.size()-1).getPath(),true)));
        for(String term:allTermsInDoc.keySet()) {
            out.print(term +","+docID+","+allTermsInDoc.get(term) + " | ");
        }
            out.println();
        out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
