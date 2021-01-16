package org.mg;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.*;

public class App extends Application {

    public static Scene scene;

    @Override
    public void start(Stage window) throws IOException {
        scene = new Scene(loadFXML("allRecipesPage"), 1280, 800);
        window.setTitle("Recipe Tracker");
        window.setScene(scene);
        window.show();
    }

    static void setRoot(String pageName) throws IOException {
        scene.setRoot(loadFXML(pageName));
    }

    public static void saveJSONFile(String name, String category, String ingredients, String instructions, String imagePath) throws IOException {
        JSONObject recipeData = new JSONObject();
        recipeData.put("name", name);
        recipeData.put("category", category);
        recipeData.put("ingredients", ingredients);
        recipeData.put("instructions", instructions);
        recipeData.put("imagePath", imagePath);

        FileWriter file = new FileWriter("./"+name+".json");
        file.write(recipeData.toJSONString());
        file.close();

    }

    public static void saveShoppingList(String shoppingList, String allRecipeNames) {
        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Your Shopping List As?");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showSaveDialog(null);

            if (selectedFile == null) return;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()));

            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);

            document.add(new Paragraph(allRecipeNames, headerFont));
            for (String ingredient: shoppingList.split("\n")) {
                document.add(new Paragraph(ingredient, font));
            }

            document.close();

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }

    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}