package org.mg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
                            (String) json.get("instructions")
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