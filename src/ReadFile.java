import javafx.scene.shape.Path;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;


/**
 * Class to read the documents.
 */

public class ReadFile {

    private String corpusPath;
    private HashSet<String> stopWords;
    private HashMap<String,String> documents ;
    private static int currentFile;
    private final int NUMBER_OF_FILES = 16;
    File[] directories;

    /**
     * Constructor to build Read file object
     * @param path
     * @throws IOException
     */
    public ReadFile(String path) throws IOException {
        this.corpusPath = path;
        currentFile=0;
        stopWords = new HashSet<>();
        documents = new HashMap();
        readStopWords();
        getDocumentsPath();

    }

    /**
     * method to clear ram when clicking on "reset button"
     */
    public void clearFileReader(){
        stopWords.clear();
        documents.clear();
        directories=new File[0];
    }

    /**
     * Getter for last K documents which was read
     * @return
     */
    public HashMap<String, String> getDocuments() {
        return documents;
    }

    /**
     * Setter for documents
     * @param documents
     */
    public void setDocuments(HashMap<String, String> documents) {
        this.documents = documents;
    }

    /**
     * Getter for stopwords
     * @return hashset of stop words
     */
    public HashSet<String> getStopWords() {
        return stopWords;
    }

    /**
     * Setter for stopwords
     * @param stopWords
     */
    public void setStopWords(HashSet<String> stopWords) {
        this.stopWords = stopWords;
    }

    /**
     * Method to read stop words file and placing it in HashSet
     * @throws IOException
     */
    private void readStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(corpusPath+"\\stopwords.txt"));
        String word;
        while((word = br.readLine()) != null){
            stopWords.add(word);
        }
    }

    /**
     * Method which gets document path and reads and storing its content
     * sets the content to HashMap
     * @param docPath
     */
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

            while ((line = br.readLine()) != null) {
                if (line.contains("</f>") || line.contains("</F>"))
                    continue;
                //if(line.contains("</h")||line.contains("</H")||line.contains("<h")||line.contains("<H")||line.contains("h>")||line.contains("H>"))
                //  line = line.replaceAll("<\\/*(h|H)\\d+>","");

                if (line.contains(docId)) {
                    key = line.replaceAll("<.*?>", "");
                }
                if (line.contains(openText)) {
                    textCheck = true;
                    continue;
                }
                if (line.contains(closeText)) {
                    textCheck = false;
                    documents.put(key, documentText);
                    documentText = "";
                }
                line = line.replaceAll("<.*>","");
                if (textCheck) {
                    documentText = documentText + " " + line;
                }

            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method which send to readDocument method K documents
     * @return
     */
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

    /**
     * Method which find the documents paths and storing it in a list.
     * @throws IOException
     */
    private void getDocumentsPath() throws IOException {
        directories = new File(corpusPath).listFiles(new FileFilter(){
            @Override
            public boolean accept(File file){
                return file.isDirectory();
            }
        });
    }
}
