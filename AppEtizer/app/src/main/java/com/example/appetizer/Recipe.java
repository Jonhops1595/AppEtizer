package com.example.appetizer;

/*

Recipe Class holds all the information for the recipe; Displayed for the user

*/


import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class Recipe extends AppCompatActivity {

    int id;
    ImageView recipeImage;
    String name;
    TextView instructions;
    String instructionStr;
    TextView topInfo;
    String topInfoStr;
    TextView ingredients;
    String ingredientStr;

    boolean ifFav;
    MenuItem favStar;
    int starResource = android.R.drawable.btn_star_big_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_layout);
        recipeImage = findViewById(R.id.recipeImage);
        instructions = (TextView) findViewById(R.id.instructionsText);
        topInfo = (TextView) findViewById((R.id.topInfo));
        ingredients = (TextView) findViewById((R.id.ingText));


        Intent intent = getIntent();

        id = intent.getIntExtra("id",0);
        //System.out.println(id);
        //Quiries the Database for the recipe info given the recipe ID
        DBHelper db = new DBHelper(getApplicationContext());


        //System.out.println(selection);
        //Gets the cursor
        Cursor recipeCurser = db.getRecipeInfo(id); //Curser that gets everything


        /*Cursor recipeCurser = db.queryTable( //Curser that gets just image, instructions, name, id
                table,
                columns,
                selection
        );*/


        recipeCurser.moveToFirst();
        try {
            //Gets the info from the recipe row
            ingredientStr = "";
            name = recipeCurser.getString(1);
            instructionStr = recipeCurser.getString(2);
            recipeImage.setImageResource(recipeCurser.getInt(3));
            topInfoStr = (recipeCurser.getString(6)) + "\n";
            //Gets all the ingredients and amounts
            while (!recipeCurser.isLast()){
                String amount = recipeCurser.getString(4);
                String ingredient = recipeCurser.getString(5);
                ingredientStr += amount + " " + ingredient + "\n";
                recipeCurser.moveToNext();
            }
            //Gets last iteration of ingredients and amounts
            String amount = recipeCurser.getString(4);
            String ingredient = recipeCurser.getString(5);
            ingredientStr += amount + " " + ingredient + "\n";
        }
        catch(CursorIndexOutOfBoundsException exception) {
            name = intent.getStringExtra("name");
            recipeImage.setImageResource(intent.getIntExtra("image", 0));
            instructionStr = "Empty Text";
            ingredientStr = "Empty Text";
            topInfoStr = "0:00";
        }

        instructions.setText(instructionStr);
        ingredients.setText(ingredientStr);


        //Gets all the categories
        Cursor catCursor = db.getCategories(id);
        while(catCursor.moveToNext())
            topInfoStr += catCursor.getString(0) + "\n";


        topInfo.setText(topInfoStr);

        //Checking if Recipe is Favorite
        favStar = findViewById(R.id.favItem);

        //Use Recipe ID and see if it is paired with 1 in RecipeIs table
        String tableName = databaseContract.recipeIsTable.TABLE_NAME;
        String[] favColumns = {
                databaseContract.recipeIsTable.COLUMN_ID,
                databaseContract.recipeIsTable.COLUMN_CAT_ID
        };
        String select = "catId = 1 AND id = " + id;
        Cursor favCursor = db.queryTable(tableName,favColumns,select);
        try{ //Means it is favorite
            favCursor.moveToFirst();
            favCursor.getInt(1);
            ifFav = true;

        }
        catch(CursorIndexOutOfBoundsException e){ //Means it is not favorite
            ifFav = false;
        }
        //set bool value update star image
        displayFav();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        getSupportActionBar().setTitle(name);
        menu.findItem(R.id.favItem).setIcon(starResource);
        return true;
    }

    //Displays the star that reflects if the recipe is favorited in the database
    private void displayFav(){
        if (ifFav)
            starResource = android.R.drawable.btn_star_big_on;
        else
            starResource = android.R.drawable.btn_star_big_off;
        invalidateOptionsMenu();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()) {
            case R.id.toMain:
                intent = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(intent);
                return true;
            case R.id.edit:
                intent = new Intent(getApplicationContext(),EditRecipe.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
            case R.id.favItem:
                //Switch in DB
                if(ifFav) { //Going to be false so delete
                    String tableName = databaseContract.recipeIsTable.TABLE_NAME;
                    String[] idNames = {databaseContract.recipeIsTable.COLUMN_ID,
                        databaseContract.recipeIsTable.COLUMN_CAT_ID};
                    int[] ids = {id,1};
                    DBHelper db = new DBHelper(getApplicationContext());
                    db.deleteTableRow(tableName,idNames,ids);
                }
                else { //Going to be true so add
                    String tableName = databaseContract.recipeIsTable.TABLE_NAME;
                    String[] columnNames = {databaseContract.recipeIsTable.COLUMN_ID,
                        databaseContract.recipeIsTable.COLUMN_CAT_ID};
                    ArrayList<Object> vals = new ArrayList<Object>();
                    vals.add(id);
                    vals.add(1);
                    DBHelper db = new DBHelper(getApplicationContext());
                    db.addTableRow(tableName,columnNames,vals);
                }
                //Toggle fav in category
                ifFav = !ifFav;
                //Display
                displayFav();
                return true;
            default:
                return true;
        }
    }

}
