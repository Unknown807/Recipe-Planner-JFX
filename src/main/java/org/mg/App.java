package org.mg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static Scene scene;

    @Override
    public void start(Stage window) throws IOException {
        scene = new Scene(loadFXML("allRecipesPage"), 1280, 800);
        window.setScene(scene);
        window.show();
    }

    static void setRoot(String pageName) throws IOException {
        scene.setRoot(loadFXML(pageName));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}