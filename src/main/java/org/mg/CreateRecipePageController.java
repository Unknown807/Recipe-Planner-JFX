package org.mg;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class CreateRecipePageController implements Initializable {

    private ObservableList<String> categoryItems = observableArrayList("Dessert", "Main Course", "Appetizer", "Side Dish");

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
        categoryOptions.setItems(categoryItems);
    }

    @FXML
    private void addRecipe() throws IOException {
        String recipeNameVal = recipeName.getText();
        if (recipeNameVal.isBlank())
            return;

        String recipeCategory = categoryOptions.getValue();
        if (recipeCategory == null)
            return;

        String ingredients = ingredientsText.getText();
        if (ingredients.isBlank())
            return;

        String instructions = instructionsText.getText();
        if (instructions.isBlank())
            return;

        JSONObject recipeData = new JSONObject();
        recipeData.put("name", recipeNameVal);
        recipeData.put("category", recipeCategory);
        recipeData.put("ingredients", ingredients);
        recipeData.put("instructions", instructions);

        FileWriter file = new FileWriter("./"+recipeNameVal+".json");
        file.write(recipeData.toJSONString());
        file.close();

    }

    @FXML
    private void switchToAllRecipesPage() throws IOException {
        App.setRoot("AllRecipesPage");
    }

    @FXML
    private void cancelRecipe() throws IOException{
        recipeName.clear();
        ingredientsText.clear();
        instructionsText.clear();
        categoryOptions.valueProperty().set(null);

        this.switchToAllRecipesPage();
    }
}
