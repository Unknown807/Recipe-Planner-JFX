package org.mg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.json.simple.JSONObject;

public class ViewRecipePageController implements Initializable {

    public String initialIngredients;
    public String initialInstructions;
    public String initialCategory;

    public String initialImagePath;
    public String newImagePath;

    public void setInitialCategory(String initialCategory) {
        this.initialCategory = initialCategory;
    }

    public void setInitialIngredients(String initialIngredients) {
        this.initialIngredients = initialIngredients;
    }

    public void setInitialInstructions(String initialInstructions) {
        this.initialInstructions = initialInstructions;
    }

    public void setInitialImagePath(String initialImagePath) {
        this.initialImagePath = initialImagePath;
        this.newImagePath = initialImagePath;
    }

    @FXML
    public Label recipeNameLabel;

    @FXML
    public TextArea ingredientsText;

    @FXML
    public TextArea instructionsText;

    @FXML
    public ImageView recipeImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    @FXML
    private void switchToAllRecipesPage() throws IOException {
        App.setRoot("AllRecipesPage");
    }

    @FXML
    private void saveRecipeChange() throws IOException {
        Boolean toSave = false;

        String ingredients = this.ingredientsText.getText();
        if (!(ingredients.equals(this.initialIngredients)))
            toSave = true;

        String instructions = this.instructionsText.getText();
        if (!(instructions.equals(this.initialInstructions)))
            toSave = true;

        if (!(this.initialImagePath.equals(this.newImagePath)))
            toSave = true;

        if (toSave) {
            Alert confirmEdit = new Alert(Alert.AlertType.CONFIRMATION);
            confirmEdit.setTitle("Confirm");
            confirmEdit.setHeaderText("Recipe Edited");
            confirmEdit.setContentText("Do you want to save the changes you made?");

            Optional<ButtonType> result = confirmEdit.showAndWait();
            if (result.get() == ButtonType.OK) {
                saveRecipeData();
            }
        }

        this.switchToAllRecipesPage();
    }

    @FXML
    public void saveRecipeData() throws IOException {
        App.saveJSONFile(this.recipeNameLabel.getText(),
                this.initialCategory,
                this.ingredientsText.getText(),
                this.instructionsText.getText(),
                this.newImagePath);
    }

    @FXML
    private void changeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose New Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {

                File newLocation = new File(selectedFile.getName());
                Files.copy(selectedFile.toPath(), newLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.newImagePath = selectedFile.getName();

                FileInputStream inputStream = new FileInputStream(this.newImagePath);
                Image image = new Image(inputStream);
                this.recipeImage.setImage(image);

            } catch (IOException ioe) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("File I/O Error");
                error.setHeaderText(null);
                error.setContentText("There was an error in using the selected image file, please try a different file");
                error.showAndWait();
                return;
            }
        }
    }

}