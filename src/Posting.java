import javafx.util.Pair;

import java.io.*;
import java.util.*;

import static java.lang.System.out;
import static java.lang.System.setOut;

public class Posting {
    private List<File> postingPaths;
    private int counterForPostin;
    private String postingPath;
    private int firstTime;
    private List<String> alphaBet;
    private List<List<String>> postingDictionary;
    private final int K1 = 20000;
    private final int K2 = 60000;
    private int [] postingLastLine;
    private PriorityQueue<String> minHeap;


    public Posting(String postingPath){
        counterForPostin=0;
        this.postingPath=postingPath;
        postingPaths = new ArrayList<>();
        firstTime=0;
        alphaBet = new ArrayList<>(Arrays.asList("abc","def","ghi","jkl","mno","pqr"
                ,"stu","vwx","yz","chars"));
        postingDictionary = new ArrayList<>();
        minHeap = new PriorityQueue<>();
    }

    public List<File> getPostingPaths() {
        return postingPaths;
    }

    public void clearPosting(){
        this.postingPaths.clear();
        this.alphaBet.clear();
        this.postingDictionary.clear();
        postingLastLine = new int[0];
    }

    public void createNewFile(String name){
        createFile(name);
    }

    public void createFile(String name){
        if(!name.equals("")){
            File file = new File(postingPath + "\\" + name+ ".txt");
            postingPaths.add(file);
        }
        else {
            counterForPostin++;
            File file = new File(postingPath + "\\" + "Posting" + counterForPostin + ".txt");
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
                postingTerms.add(term + ":" + docID + ":" + terms.get(docID).get(term) + "|");
            }
        }
        Collections.sort(postingTerms);
        return postingTerms;
    }



    public List<List<String>> getPostingDictionary(){
        return postingDictionary;
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
            if(term == null)
                continue;
            splittedTerm = term.split(":");
            if (previousTerm.split(":")[0].equals(splittedTerm[0])) {
                previousTerm = previousTerm + splittedTerm[1] + ":" + splittedTerm[2];
                if (listCounter == terms.size() - 1)
                    combineTerms.add(previousTerm);
            } else {
                combineTerms.add(previousTerm);
                previousTerm = term;
            }
        }

        return combineTerms;
    }

    public void mergePostingFiles(){
        List<List<String>> files = new ArrayList<>();
        postingLastLine = new int[postingPaths.size()];
        for(File f: postingPaths){
            files.add(readKLinesFromDoc(f.getAbsolutePath(),0));
        }
        KWayMerge(files);
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

    public HashMap<String,Term> splitPostingToAlphaBet(HashMap<String,Term> dictionary){
        int updatedFile =0;
        Term term;
        int[] fileCounter = new int[10];
        List<String> kLines = new ArrayList<>();
        BufferedReader reader = null;
        FileWriter abc,def,ghi,jkl,mno,pqr,stu,vwx,yz,chars;
        String postingLine = "";
        String []postingLineSplited;
        int numberOfTerms = 0;
        for(String s:alphaBet)
            createNewFile(s);
        try {
            abc = new FileWriter(postingPaths.get(postingPaths.size() - 10).getPath(), true);
            def = new FileWriter(postingPaths.get(postingPaths.size() - 9).getPath(), true);
            ghi = new FileWriter(postingPaths.get(postingPaths.size() - 8).getPath(), true);
            jkl = new FileWriter(postingPaths.get(postingPaths.size() - 7).getPath(), true);
            mno = new FileWriter(postingPaths.get(postingPaths.size() - 6).getPath(), true);
            pqr = new FileWriter(postingPaths.get(postingPaths.size() - 5).getPath(), true);
            stu = new FileWriter(postingPaths.get(postingPaths.size() - 4).getPath(), true);
            vwx = new FileWriter(postingPaths.get(postingPaths.size() - 3).getPath(), true);
            yz = new FileWriter(postingPaths.get(postingPaths.size() - 2).getPath(), true);
            chars = new FileWriter(postingPaths.get(postingPaths.size() - 1).getPath(), true);

            reader = new BufferedReader(new FileReader(postingPath+"\\combinedList.txt"));
            while ((postingLine = reader.readLine()) != null) {
                postingLineSplited = postingLine.split(":");
                /**
                 if(dictionary.containsKey(postingLineSplited[0])){
                 term = dictionary.get(postingLineSplited[0]).getTerm();
                 postingLineSplited[0] = term + " #";
                 postingLine = String.join(":",postingLineSplited)+ "\n";
                 }
                 **/
                postingLine = postingLine +"\n";
                if(postingLineSplited[0].length() == 0)
                    chars.write(postingLine);
                else if(postingLineSplited[0].charAt(0) >= 'a' && postingLineSplited[0].charAt(0) <= 'c'){
                    abc.write(postingLine);
                    updatedFile = 0;
                    fileCounter[0]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'd' && postingLineSplited[0].charAt(0) <= 'f'){
                    def.write(postingLine);
                    updatedFile = 1;
                    fileCounter[1]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'g' && postingLineSplited[0].charAt(0) <= 'i'){
                    ghi.write(postingLine);
                    updatedFile = 2;
                    fileCounter[2]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'j' && postingLineSplited[0].charAt(0) <= 'l'){
                    jkl.write(postingLine);
                    updatedFile =3;
                    fileCounter[3]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'm' && postingLineSplited[0].charAt(0) <= 'p'){
                    mno.write(postingLine);
                    updatedFile = 4;
                    fileCounter[4]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'p' && postingLineSplited[0].charAt(0) <= 'r'){
                    pqr.write(postingLine);
                    updatedFile = 5;
                    fileCounter[5]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 's' && postingLineSplited[0].charAt(0) <= 'u'){
                    stu.write(postingLine);
                    updatedFile = 6;
                    fileCounter[6]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'v' && postingLineSplited[0].charAt(0) <= 'x'){
                    vwx.write(postingLine);
                    updatedFile = 7;
                    fileCounter[7]++;
                }
                else if(postingLineSplited[0].charAt(0) >= 'y' && postingLineSplited[0].charAt(0) <= 'z'){
                    yz.write(postingLine);
                    updatedFile = 8;
                    fileCounter[8]++;
                }
                else{
                    updatedFile = 9;
                    fileCounter[9]++;
                    chars.write(postingLine);

                }
                if(dictionary.containsKey(postingLineSplited[0])){
                    term =dictionary.get(postingLineSplited[0]);
                    if(term.getLine()==-1)
                        term.setLine(fileCounter[updatedFile]);
                }
            }
            reader.close();
            abc.flush();
            def.flush();
            ghi.flush();
            jkl.flush();
            mno.flush();
            pqr.flush();
            stu.flush();
            vwx.flush();
            yz.flush();
            chars.flush();

            abc.close();
            def.close();
            ghi.close();
            jkl.close();
            mno.close();
            pqr.close();
            stu.close();
            vwx.close();
            yz.close();
            chars.close();
            System.out.println(numberOfTerms);
            return dictionary;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }



}

