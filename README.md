# Recipe-Tracker-JFX

JDK 15.0.1

## Attributions

For release icon in version 1.0 and 1.1:

Made by <a href="https://www.flaticon.com/authors/surang" title="surang">surang</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>

For release icon in version 1.2:

Made by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>

## Dependencies

* json-simple - 1.1.1
* javafx-fxml - 15.0.1
* javafx-controls - 15.0.1
* itextpdf - 5.5.10

For more info look in the pom.xml file

## Description

The program was made to help in organising a collection of cooking recipes you might have. It allows you to add/remove/view recipes, filter them by their category (Main, Side Dish, Dessert, etc) and export one or more recipes into a pdf; having the option to tally their total ingredients to see how much you would need when shopping.

## How to Use

When you start the program you will be greeted with the screen below (initially there will be nothing in the table).

![alt text](/imgs/img1.JPG)

You can select any recipe(s) from the table and click 'Remove Recipe' to permanently delete it or view recipe to see it (can also double click), you can also filter the types of recipes there are. The first thing you'll want to do is to click 'Add Recipe' for a new recipe.

![alt text](/imgs/img2.JPG)

Here you have to type a name for your recipe, its category, the ingredients for it and how you make it. You can also add an image, but that part is optional.

![alt text](/imgs/img3.JPG)

After that it will be added to the table and you can view it. On the left you can see the ingredients and the right instructions, just above there is a dropdown which will display the image you choose when pressed.

![alt text](/imgs/img4.JPG)

If you didn't add an image initially or want to change your image, just click on the image and you will get a prompt to locate the picture you want.

![alt text](/imgs/img5.JPG)

Going back to the table, if you select one or more recipes and click the 'Export Recipes' button it will let you save a pdf file containing all the extracted ingredients and instructions for each selected recipe in a sequential order. Useful if you want to give someone a quick copy of some of your recipes. Note that tbsp and tsp are all converted into grams in the shopping list.

![alt text](/imgs/img6.JPG)

Likewise if you select one or more recipes and click 'Generate Shopping List' it will go through all the recipes you selected and tally their ingredients together into one simple shopping list. Then the idea would be you take the pdf with you on your smartphone when shopping.

## Ingredient Formatting

For the program to be able to generate a nice shopping list with all your recipes' ingredients, it requires you to follow a simple structure when writing ingredients in.

* Each ingredient has to be on its own separate line
* Units like mg, g, kg, ml, l, tbsp and tsp, should be written '\<amount\>\<unit\> \<name of ingredient\>'. I.e '225g plain flour' or '1 tbsp salt'
* You can use fractions (1/2, 1/4, 3/8) and decimals (1.2kg, 0.5ml) but make sure to format it the same as the above
* Two other ways to write an ingredient would be: '1 Lemon Juice' or 'Onions'. In this case write it like '\<amount\> \<ingredient\>' or '\<ingredient\>'
* You can also have spaces between the amount and the unit, '30 kg Meat' and '30kg Meat' are both valid
