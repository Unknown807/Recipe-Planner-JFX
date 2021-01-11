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
import javafx.scene.control.TableView;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static javafx.collections.FXCollections.observableArrayList;

public class AllRecipesPageController implements Initializable {

    @FXML
    private ComboBox<String> filterOptions;

    @FXML
    private TableView recipeTable;

    @FXML
    private void switchToCreateRecipePage() throws IOException {
        App.setRoot("CreateRecipePage");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterOptions.setItems(observableArrayList("Dessert", "Main Course", "Appetizer", "Side Dish"));
        recipeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Populate with all current recipes
        ArrayList<String[]> recipeData = new ArrayList<>();

        String[] dirContents = (new File(".")).list();
        JSONParser parser = new JSONParser();
        for (String file : dirContents) {
            if (file.endsWith(".json")) {
                try {
                    Object recipeFile = parser.parse(new FileReader("./" + file));

                    JSONObject json = (JSONObject) recipeFile;

                    String[] recipe = new String[] {
                            (String) json.get("name"),
                            (String) json.get("category"),
                    };

                    recipeData.add(recipe);

                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



    }
}