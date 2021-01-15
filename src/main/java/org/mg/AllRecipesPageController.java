package org.mg;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String capitaliseWord(String str) {
        char[] words = str.toCharArray();
        words[0] = Character.toUpperCase(words[0]);
        for (int i=1; i < words.length; i++) {
            if (Character.isWhitespace(words[i-1])) {
                words[i] = Character.toUpperCase(words[i]);
            }
        }
        return new String(words);
    }

    private double convertToDouble(String str) {
        if (str.contains("/")) {
            String[] ops = str.split("/");
            return Double.parseDouble(ops[0])/Double.parseDouble(ops[1]);
        }
        else if (!(str.contains("."))) {
            str += ".0";
        }

        return Double.parseDouble(str);
    }

    private String removeTrailingZeros(double num) {
        return (""+num).replaceAll("\\.0+$", "");
    }

    @FXML
    private void generateShoppingList() {
        ObservableList<Recipe> recipes = this.recipeTable.getSelectionModel().getSelectedItems();
        if (recipes.size() < 1)
            return;

        HashMap<String, HashMap<String, Double>> ingTotal = new HashMap<>();

        for (Recipe r: recipes) {
            for (String ingredient: r.getRecipeIngredients().split("\n")) {
                if (ingredient.isBlank()) continue;

                boolean foundAlready = false;
                String[] ingredientParts;

                String ingKey = "recipeTotal";
                double newIngAmt = 1.0;
                String ingName = ingredient.trim().toLowerCase();
                HashMap<String, Double> ingAmts = ingTotal.getOrDefault(ingName, new HashMap<>() {{
                    put("recipeTotal", 0.0);
                    put("type", 0.0);
                }});

                for (String ingFormat: Arrays.asList("mg", "g", "kg", "ml", "l", "tbsp", "tsp")) {
                    Pattern pattern = Pattern.compile("(\\d+.\\d+|\\d+|\\d+/\\d+)\\s*" + ingFormat + " .+", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(ingredient);
                    boolean matchFound = matcher.find();

                    if (matchFound) {

                        ingredientParts = ingredient.split(ingFormat);
                        ingKey = ingFormat;
                        newIngAmt = this.convertToDouble(ingredientParts[0].trim());
                        ingName = ingredientParts[1].trim().toLowerCase();

                        double msType;
                        if ("mgkg".contains(ingFormat)) msType = 1.0;
                        else if ("ml".contains(ingFormat)) msType = 2.0;
                        else msType = 3.0;

                        ingAmts = ingTotal.getOrDefault(ingName, new HashMap<>() {{
                            put("mg", 0.0);
                            put("g", 0.0);
                            put("kg", 0.0);
                            put("ml", 0.0);
                            put("l", 0.0);
                            put("tbsp", 0.0);
                            put("tsp", 0.0);
                            put("type", 2.0);
                            put("msType", msType);
                        }});

                        foundAlready = true;
                        break;
                    }
                }

                if (!(foundAlready)) {
                    Pattern pattern = Pattern.compile("(\\d+.\\d+|\\d+|\\d+/\\d+) .+");
                    Matcher matcher = pattern.matcher(ingredient);
                    boolean matchFound = matcher.find();

                    if (matchFound) {
                        ingredientParts = ingredient.split(" ", 2);
                        newIngAmt = this.convertToDouble(ingredientParts[0].trim());
                        ingName = ingredientParts[1].trim().toLowerCase();
                        ingKey = "total";

                        ingAmts = ingTotal.getOrDefault(ingName, new HashMap<>() {{
                            put("total", 0.0);
                            put("type", 1.0);
                        }});
                    }
                }

                ingAmts.put(ingKey, ingAmts.get(ingKey)+newIngAmt);
                ingTotal.put(ingName, ingAmts);

            }

        }

        this.prettifyShoppingList(ingTotal);
    }

    private void prettifyShoppingList(HashMap<String, HashMap<String, Double>> ingMap) {
        String shoppingList = "";

        for (Map.Entry<String, HashMap<String, Double>> ingredients: ingMap.entrySet()) {
            double total = 0.0;
            String ingredient = this.capitaliseWord(ingredients.getKey());
            HashMap<String, Double> ms = ingredients.getValue();
            String type = String.valueOf(ms.get("type"));

            String formattedIngredient = "";

            switch (type) {
                case "0.0":
                    formattedIngredient = ingredient+" (for "+this.removeTrailingZeros(ms.get("recipeTotal"))+" recipes)";
                    break;
                case "1.0":
                    formattedIngredient = ingredient+" (x"+this.removeTrailingZeros(ms.get("total"))+")";
                    break;
                case "2.0":
                    if (ms.get("msType") == 1.0) {
                        double kg = ms.get("kg");
                        double g = ms.get("g");
                        double mg = ms.get("mg");
                        String suffix = "mg";

                        if (kg != 0.0 || g >= 1000.0) {
                            g = g*0.001;
                            mg = mg*0.000001;
                            suffix = "kg";
                        } else if (g != 0.0 || mg >= 1000.0) {
                            mg = mg*0.001;
                            suffix = "g";
                        }

                        String sum = this.removeTrailingZeros(kg+g+mg);
                        formattedIngredient = ingredient+" ("+sum+suffix+")";

                    } else if (ms.get("msType") == 2.0) {
                        double ml = ms.get("ml");
                        double l = ms.get("l");
                        String suffix = "ml";

                        if (l != 0.0 || ml >= 1000.0) {
                            ml = ml * 0.001;
                            suffix = "l";
                        }

                        String sum = this.removeTrailingZeros(l+ml);
                        formattedIngredient = ingredient+" ("+sum+suffix+")";

                    } else {
                        double tbspToGrams = ms.get("tbsp")*15.0;
                        double tspToGrams = ms.get("tsp")*4.261;
                        String sum = this.removeTrailingZeros(Math.round((tbspToGrams+tspToGrams)*10.0)/10.0);
                        formattedIngredient = ingredient+" ("+sum+"g rounded)";
                    }
                    break;
            }

            shoppingList += formattedIngredient+"\n";
        }

        App.saveShoppingList(shoppingList);

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

        this.convertToDouble("7/6");

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