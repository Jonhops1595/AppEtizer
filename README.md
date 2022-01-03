# AppEtizer
Project for CS 300; Software Engineering
Recipe Database app that runs on Android devices

This app allows users to add, delete, and edit recipes from a SQLite Database.
Our homescreen displays any recipes that are in the database. When a user clicks on them, 
the app takes them to the recipe layout screen where it displays the details of the recipe such 
as name, categories, ingredients, and directions. Users can favorite recipes which will show 
up first on the home screen. Users also have the option to add custom categories to sort their recipes.
On the home screen there is a search bar where users can search for a particular recipe by
either the catorgory name or the recipe name. 

When testing the app, make sure that after the amount you type a ',' and same for after the 
ingredient. This helps to differentiate the amount and ingredients.

There are some bugs we were unable to get working. Our scanner classes (Scan and TesseractOCR)
were unable to be successfully implemented so they are not in the program. We were also unable
to implement adding custom pictures to the recipes so all recipe pictures are the default picture. 
Another feature we found to be incomplete was if you select the "favorite" category for a recipe 
when you first create it and then edit it so that it is no longer favorited, the recipe disappears 
from the home screen. This only happens if you assign the favorite category to a recipe when you 
first create it. Also, if you search for a recipe using the search bar and no 
recipes come up, you get stuck and have to do another search that will be in the database. When
this occurs the floating action button will seize to work until a recipe is opened and the
homescreen is refreshed. We also could not figure out how to clear the select catergories spinner
in the input recipe section, so when you save one recipe and go to input another the categories
selected for the previous recipe are still selected. Lastly, we did not get to implement the screen 
readjustments when the phone is turned horizontally.
