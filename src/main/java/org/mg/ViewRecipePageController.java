package org.mg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ViewRecipePageController implements Initializable {

    public String initialIngredients;
    public String initialInstructions;

    public String initialImagePath;
    public String newImagePath;

    public void setInitialImagePath(String initialImagePath) {
        this.initialImagePath = initialImagePath;
        this.newImagePath = initialImagePath;
    }

    public void setInitialIngredients(String initialIngredients) {
        this.initialIngredients = initialIngredients;
    }

    public void setInitialInstructions(String initialInstructions) {
        this.initialInstructions = initialInstructions;
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
        if (!(this.ingredientsText.getText().equals(this.initialIngredients)))
            toSave = true;

        if (!(this.instructionsText.getText().equals(this.initialInstructions)))
            toSave = true;

        if (!(this.initialImagePath.equals(this.newImagePath)))
            toSave = true;

        if (toSave) {
            System.out.println("Different ingredients/instructions");
        }

        this.switchToAllRecipesPage();
    }

    @FXML
    private void changeImage() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            this.newImagePath = selectedFile.getAbsolutePath();
            FileInputStream inputStream = new FileInputStream(this.newImagePath);
            Image image = new Image(inputStream);
            this.recipeImage.setImage(image);
        }
    }

}