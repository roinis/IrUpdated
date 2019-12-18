import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Gui {

    private Stage mainStage;
    private BorderPane rootPane;
    public javafx.scene.control.TextField tx_corpusPath;
    private String corpusPath;
    public javafx.scene.control.TextField tx_postingPath;
    private String postingPath;
    public javafx.scene.control.CheckBox stem;
    private Index index;


    public Gui(){
    }


    public void getCorpusBrowser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Corpus Directory:");
        File fileChoosed = directoryChooser.showDialog(null);
        if(fileChoosed!=null){
            corpusPath = fileChoosed.getAbsolutePath();
            tx_corpusPath.setText(corpusPath);
        }
    }

    public void getPostingBrowser(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose where to save the Posting Files:");
        File fileChoosed = directoryChooser.showDialog(null);
        if(fileChoosed!=null){
            postingPath = fileChoosed.getAbsolutePath();
            tx_postingPath.setText(postingPath);
        }
    }

    public void run() throws IOException {
        try {
            long startTime = 0;
            if (tx_corpusPath.getText().isEmpty() || tx_postingPath.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setContentText("Please insert Corpus and Posting paths.");
                alert.showAndWait();
            } else {
                postingPath = tx_postingPath.getText();
                corpusPath = tx_corpusPath.getText();
                startTime = System.currentTimeMillis();
                if (stem.isSelected()) {
                    index = new Index(true, corpusPath, postingPath);
                    index.startIndex();
                } else {
                    index = new Index(false, corpusPath, postingPath);
                    index.startIndex();
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("");
            System.out.println("It took " + TimeUnit.MILLISECONDS.toMinutes(endTime - startTime) + " Minutes to create the Index");
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void reset() throws IOException {
        try {
            if(index!=null) {
                index.resetPosting();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Posting Files deleted and memory cleared.");
                alert.showAndWait();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void loadDictToMemory(){
        try {
            if(tx_corpusPath.getText().isEmpty()||tx_postingPath.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please insert Corpus and Posting path");
                alert.showAndWait();
            }
            if (index == null) {
                index = new Index(stem.isSelected(), tx_corpusPath.getText(), tx_postingPath.getText());
                index.createDictionaryFromFile();
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setContentText("Dictionary loaded to memory");
                alert2.showAndWait();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Dictionary already loaded");
                alert.showAndWait();
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void printNumbersAmount(){
        try{
            index.printNumbersInDictionary();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void print10Terms(){
        try{
            index.topTenTerms();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void showDict(){
        try {
            if (index == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please insert Corpus and Posting path");
                alert.showAndWait();
                index = new Index(stem.isSelected(), tx_corpusPath.getText(), tx_postingPath.getText());
                index.createDictionaryFromFile();
            }
            Stage newStage = new Stage();
            VBox vBox = new VBox();
            List<Term> dict = index.getDictionaryListSorted();
            ListView<String> listView=new ListView<>();
            for (Term term : dict) {
                 listView.getItems().add(term.getTerm() + " " + term.getTf());
            }
            vBox.getChildren().addAll(listView);
            Scene scene = new Scene(vBox);
            newStage.setScene(scene);
            newStage.setMinHeight(400);
            newStage.setMinWidth(400);
            newStage.show();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

}
