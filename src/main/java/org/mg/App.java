package org.mg;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.*;

public class App extends Application {

    private static final Font textFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
    private static final Font headerFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);

    public static Scene scene;

    @Override
    public void start(Stage window) throws IOException {
        scene = new Scene(loadFXML("AllRecipesPage"), 1280, 800);
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

            document.add(new Paragraph(allRecipeNames, headerFont));
            for (String ingredient: shoppingList.split("\n")) {
                document.add(new Paragraph(ingredient, textFont));
            }

            document.close();

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void exportRecipes(ObservableList<Recipe> recipes) {
        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Your Recipes As?");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showSaveDialog(null);

            if (selectedFile == null) return;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()));

            document.open();

            for (Recipe recipe: recipes) {
                document.add(new Paragraph(recipe.getRecipeName()+" ("+recipe.getCategoryOption()+")", headerFont));
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("Ingredients:", headerFont));

                for (String ingredient: recipe.getRecipeIngredients().split("\n")) {
                    if (ingredient.contains(":")) {
                        document.add(Chunk.NEWLINE);
                    }
                    document.add(new Paragraph(ingredient, textFont));
                }
                document.add(Chunk.NEWLINE);

                document.add(new Paragraph("Instructions:", headerFont));
                for (String instruction: recipe.getRecipeInstructions().split("\n")) {
                    document.add(new Paragraph(instruction, textFont));
                }

                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

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