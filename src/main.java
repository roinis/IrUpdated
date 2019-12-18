import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class main extends Application {

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        FileInputStream fxmlStream = new FileInputStream("src\\GuiFXML.fxml");
        Pane rootPane = (Pane) fxmlLoader.load(fxmlStream);
        Scene scene = new Scene(rootPane,600,400);
        stage.setScene(scene);
        stage.setTitle("Indexer");
        stage.show();
    }

    public static void main(String args[]) throws IOException {

        launch(args);







    }
}
