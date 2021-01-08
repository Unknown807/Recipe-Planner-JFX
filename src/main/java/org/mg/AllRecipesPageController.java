package org.mg;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import static javafx.collections.FXCollections.observableArrayList;

public class AllRecipesPageController implements Initializable {

//    @FXML
//    private void switchToSecondary() throws IOException {
//        App.setRoot("secondary");
//    }

    @FXML
    private ComboBox<String> filterOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filterOptions.setItems(observableArrayList("Dessert", "Main Course", "Appetizer", "Side Dish"));
    }
}
