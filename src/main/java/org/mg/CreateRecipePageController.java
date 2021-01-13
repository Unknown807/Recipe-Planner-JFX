package org.mg;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.stage.FileChooser;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class CreateRecipePageController implements Initializable {

    private ObservableList<String> categoryItems = observableArrayList("Dessert", "Main Course", "Appetizer", "Side Dish");
    private String imagePath = "";

    @FXML
    private Button imageChooser;

    @FXML
    private ComboBox<String> categoryOptions;

    @FXML
    private TextField recipeName;

    @FXML
    private TextArea ingredientsText;

    @FXML
    private TextArea instructionsText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.categoryOptions.setItems(this.categoryItems);
    }

    @FXML
    private void switchToAllRecipesPage() throws IOException {
        App.setRoot("AllRecipesPage");
    }

    @FXML
    private void addRecipe() throws IOException {
        boolean flag = true;
        String message = "";

        String recipeNameVal = this.recipeName.getText();
        Path path = Paths.get("./"+recipeNameVal+".json");

        String recipeCategory = this.categoryOptions.getValue();
        String ingredients = this.ingredientsText.getText();
        String instructions = this.instructionsText.getText();

        if (recipeNameVal.isBlank()) {
            flag = false;
            message = "The recipe name cannot be left blank";
        } else if (Files.exists(path)){
            flag = false;
            message = "A recipe with the same name already exists";
        } else if (recipeCategory == null) {
            flag = false;
            message = "You haven't selected a recipe category";
        } else if (ingredients.isBlank()) {
            flag = false;
            message = "The ingredients cannot be left blank";
        } else if (instructions.isBlank()) {
            flag = false;
            message = "The instructions cannot be left blank";
        }

        if (!(flag)) {
            Alert error = new Alert(Alert.AlertType.WARNING);
            error.setTitle("Incorrect Input");
            error.setHeaderText(null);
            error.setContentText(message);
            error.showAndWait();
            return;
        }

        App.saveJSONFile(recipeNameVal, recipeCategory, ingredients, instructions, this.imagePath);

        this.switchToAllRecipesPage();
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            this.imagePath = selectedFile.getAbsolutePath();
            this.imageChooser.setText(selectedFile.getName());
        }

    }

}
