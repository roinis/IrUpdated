import javafx.scene.shape.Path;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;


public class ReadFile {

    private String path;
    private HashSet<String> stopWords;
    private HashMap<String,String> documents ;
    private static int currentFile;
    private final int NUMBER_OF_FILES = 8;
    File[] directories;

    public ReadFile(String path) throws IOException {
        this.path = path;
        currentFile=0;
        stopWords = new HashSet<>();
        documents = new HashMap();
        readStopWords();
        getDocumentsPath();

    }

    public HashMap<String, String> getDocuments() {
        return documents;
    }

    public void setDocuments(HashMap<String, String> documents) {
        this.documents = documents;
    }

    public HashSet<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }

    private void readStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path+"\\stopwords.txt"));
        String word;
        while((word = br.readLine()) != null){
            stopWords.add(word);
        }
    }

    private void readDocument(String docPath) {
        String openText = "<TEXT>";
        String closeText = "</TEXT>";
        String docId = "<DOCNO>";
        String newDoc = "</DOC>";
        String documentText= "";
        String line;
        String key="";
        boolean textCheck = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(docPath));

        while((line = br.readLine()) != null){
            if(line.contains("</f>")||line.contains("</F>"))
                continue;
            if(line.contains("<h")||line.contains("<H")||line.contains("h>")||line.contains("H>"))
                line = line.replaceAll("<\\/*(h|H)\\d+>","");
            if(line.contains(docId)){
                key = line.replaceAll("<.*?>","");
            }
            if(line.contains(openText)){
                textCheck = true;
                continue;
            }
            if(line.contains(closeText)){
                textCheck =false;
                documents.put(key,documentText);
                documentText = "";
            }
            if(textCheck){
                documentText= documentText + " " +line;
            }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean readFewFiles()  {
        documents = new HashMap();
         for (int i = currentFile; i < currentFile + NUMBER_OF_FILES; i++){
            if(i >= directories.length)
                return false;
            readDocument(directories[i].getPath()+"\\"+directories[i].getName());
        }
        currentFile = currentFile + NUMBER_OF_FILES;
        return true;
    }

    private void getDocumentsPath() throws IOException {
        directories = new File(path).listFiles(new FileFilter(){
            @Override
            public boolean accept(File file){
                return file.isDirectory();
            }
        });
    }
}
