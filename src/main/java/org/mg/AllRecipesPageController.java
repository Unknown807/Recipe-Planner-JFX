package org.mg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static javafx.collections.FXCollections.observableArrayList;

public class AllRecipesPageController implements Initializable {

    @FXML
    private ComboBox<String> filterOptions;

    @FXML
    private TableView<Recipe> recipeTable;

    @FXML
    private TableColumn<Recipe, String> nameColumn;

    @FXML
    private TableColumn<Recipe, String> categoryColumn;

    @FXML
    private void switchToCreateRecipePage() throws IOException {
        App.setRoot("CreateRecipePage");
    }

    @FXML
    private void viewRecipe() {

    }

    @FXML
    private void removeRecipes() {
        ObservableList<Recipe> recipesToRemove = recipeTable.getSelectionModel().getSelectedItems();
        if (recipesToRemove.size() < 1)
            return;

        Alert confirmRemoval = new Alert(Alert.AlertType.CONFIRMATION);
        confirmRemoval.setTitle("Confirm");
        confirmRemoval.setHeaderText("Recipe Removal");
        confirmRemoval.setContentText("Are you sure you want to remove the selected recipes?");

        Optional<ButtonType> result = confirmRemoval.showAndWait();
        if (result.get() == ButtonType.OK) {

            ObservableList<Recipe> newItems = recipeTable.getItems();
            Boolean success = true;

            for (Recipe recipe: recipesToRemove) {
                File file = new File("./"+recipe.getRecipeName()+".json");
                if (!(file.delete())) {
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("File Deletion Error");
                    failure.setHeaderText(null);
                    failure.setContentText("The file "+recipe.getRecipeName()+" could not be deleted");
                    failure.showAndWait();
                    success = false;
                } else {
                    newItems.remove(recipe);
                }
            }

            recipeTable.getSelectionModel().clearSelection();

            if (success) {
                Alert finish = new Alert(Alert.AlertType.INFORMATION);
                finish.setTitle("Recipes Removed");
                finish.setHeaderText(null);
                finish.setContentText("The recipes have been removed");
                finish.showAndWait();
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterOptions.setItems(observableArrayList("Dessert", "Main Course", "Appetizer", "Side Dish"));
        recipeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("recipeName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryOption"));

        // Populate with all current recipes
        ObservableList<Recipe> recipeData = observableArrayList();

        String[] dirContents = (new File(".")).list();
        JSONParser parser = new JSONParser();
        for (String file : dirContents) {
            if (file.endsWith(".json")) {
                try {
                    Object recipeFile = parser.parse(new FileReader("./" + file));

                    JSONObject json = (JSONObject) recipeFile;

                    recipeData.add(new Recipe(
                            (String) json.get("name"),
                            (String) json.get("category"),
                            (String) json.get("ingredients"),
                            (String) json.get("instructions"),
                            (String) json.get("imagePath")
                    ));

                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        recipeTable.setItems(recipeData);

    }
}