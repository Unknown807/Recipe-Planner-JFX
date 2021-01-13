package org.mg;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static javafx.collections.FXCollections.observableArrayList;

public class AllRecipesPageController implements Initializable {

    private ObservableList<Recipe> allRecipes;

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
    private void viewRecipe() throws IOException {
        Recipe chosenRecipe = recipeTable.getSelectionModel().getSelectedItem();
        if (chosenRecipe == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewRecipePage.fxml"));
        Region root = (Region) loader.load();

        ViewRecipePageController controller = loader.getController();

        controller.recipeNameLabel.setText(chosenRecipe.getRecipeName());

        controller.setInitialCategory(chosenRecipe.getCategoryOption());

        controller.setInitialIngredients(chosenRecipe.getRecipeIngredients());
        controller.ingredientsText.setText(chosenRecipe.getRecipeIngredients());

        controller.setInitialInstructions(chosenRecipe.getRecipeInstructions());
        controller.instructionsText.setText(chosenRecipe.getRecipeInstructions());

        String imagePath = chosenRecipe.getImagePath();
        imagePath = (imagePath.isEmpty()) ? "./default_image.png" : imagePath;
        Path path = Paths.get(imagePath);
        if (!(Files.exists(path)))
            imagePath = "./default_image.png";

        controller.setInitialImagePath(imagePath);
        FileInputStream inputStream = new FileInputStream(imagePath);
        Image image = new Image(inputStream);

        controller.recipeImage.setImage(image);

        App.scene.setRoot(root);
    }

    @FXML
    private void removeRecipes() throws IOException {
        ObservableList<Recipe> recipesToRemove = this.recipeTable.getSelectionModel().getSelectedItems();
        if (recipesToRemove.size() < 1)
            return;

        Alert confirmRemoval = new Alert(Alert.AlertType.CONFIRMATION);
        confirmRemoval.setTitle("Confirm");
        confirmRemoval.setHeaderText("Recipe Removal");
        confirmRemoval.setContentText("Are you sure you want to remove the selected recipes?");

        Optional<ButtonType> result = confirmRemoval.showAndWait();
        if (result.get() == ButtonType.OK) {

            boolean deleted = false;

            for (Recipe recipe: recipesToRemove){
                File file = new File("./"+recipe.getRecipeName()+".json");

                deleted = file.delete();

                if (!(deleted)) {
                    System.out.println(file.exists());
                    System.out.println(recipe.getRecipeName());
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("File Deletion Error");
                    failure.setHeaderText(null);
                    failure.setContentText("The file "+recipe.getRecipeName()+" could not be deleted");
                    failure.showAndWait();
                } else {
                    Platform.runLater(() -> this.allRecipes.remove(recipe));
                }
            }

            this.recipeTable.getSelectionModel().clearSelection();

            if (deleted) {
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
        this.filterOptions.setItems(observableArrayList("Dessert", "Main Course", "Appetizer", "Side Dish", "All"));

        this.recipeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.nameColumn.setCellValueFactory(new PropertyValueFactory<>("recipeName"));
        this.categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryOption"));

        // Populate with all current recipes
        this.allRecipes = observableArrayList();

        String[] dirContents = (new File(".")).list();
        JSONParser parser = new JSONParser();
        for (String file : dirContents) {
            if (file.endsWith(".json")) {
                try {
                    FileReader fr = new FileReader("./" + file);
                    Object recipeFile = parser.parse(fr);

                    JSONObject json = (JSONObject) recipeFile;

                    this.allRecipes.add(new Recipe(
                            (String) json.get("name"),
                            (String) json.get("category"),
                            (String) json.get("ingredients"),
                            (String) json.get("instructions"),
                            (String) json.get("imagePath")
                    ));

                    fr.close();
                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FilteredList<Recipe> filteredData = new FilteredList<>(this.allRecipes, p -> true);

        this.filterOptions.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate(recipe -> {
                if (newValue.equals("All"))
                    return true;

                if (recipe.getCategoryOption().equals(newValue))
                    return true;

                return false;
            });
        });

        SortedList<Recipe> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(this.recipeTable.comparatorProperty());
        this.recipeTable.setItems(sortedData);

    }
}