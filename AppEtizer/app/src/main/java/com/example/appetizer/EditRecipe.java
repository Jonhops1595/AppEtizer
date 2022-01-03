package com.example.appetizer;

/*
Editing Class

*/


import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class EditRecipe extends AppCompatActivity {
    EditText recipeNameTxtBx, totalTimeTxtBx, ingredientTxtBx, directionsTxtBx;
    String recipeName, totalTime, ingredient, directions;
    Button saveBtn;
    ImageButton photoBtn;
    Drawable defaultBackground;
    DropDownCategories[] select_qualification;
    ArrayList<DropDownCategories> listVOs;
    int id;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editing_recipes);

        recipeNameTxtBx = findViewById(R.id.RecipeName);
        totalTimeTxtBx = findViewById(R.id.totaltime);
        ingredientTxtBx = findViewById(R.id.Ingredients);
        directionsTxtBx = findViewById(R.id.Directions);
        saveBtn = findViewById(R.id.SaveButton);
        photoBtn = findViewById(R.id.recipePhoto);
        defaultBackground = photoBtn.getBackground();

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);


        //-------------------------- Getting Current Recipe Info --------------------------
        DBHelper db = new DBHelper(getApplicationContext());
        Cursor recipeCursor = db.getRecipeInfo(id);

        recipeCursor.moveToFirst();
        try {
            ingredient = "";
            recipeName = recipeCursor.getString(1);
            directions = recipeCursor.getString(2);
            totalTime = (recipeCursor.getString(6));

            while (!recipeCursor.isLast()) {
                String amount = recipeCursor.getString(4);
                String ing = recipeCursor.getString(5);
                ingredient += amount + "," + ing + ",";
                recipeCursor.moveToNext();
            }
            String amount = recipeCursor.getString(4);
            String ing = recipeCursor.getString(5);
            ingredient += amount + "," + ing + "";


        } catch (CursorIndexOutOfBoundsException exception) {
            recipeName = intent.getStringExtra("name");
            directions = "Empty Text";
            ingredient = "Empty Text";
            totalTime = "0:00";
        }

        Cursor cursor = db.dumpTable(databaseContract.categoryTable.TABLE_NAME);
        int maxCategories = cursor.getCount();
        maxCategories++;
        select_qualification = new DropDownCategories[maxCategories];
        select_qualification[0] = new DropDownCategories(-1, "Select Categories"); //Top or Title of the DropDown

        cursor.moveToFirst();
        for (int i = 1; i < maxCategories; i++) { //Populating List
            int catId = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("category"));
            DropDownCategories targetCat;
            targetCat = new DropDownCategories(catId, name);
            //Find out if it's selected in the DB

            Cursor selectedCategories = db.rawQuery("SELECT " + databaseContract.categoryTable.COLUMN_CATEGORY + " FROM " +
                    databaseContract.categoryTable.TABLE_NAME + " INNER JOIN " +
                    databaseContract.recipeIsTable.TABLE_NAME + " ON " +
                    databaseContract.categoryTable.TABLE_NAME + "." + databaseContract.categoryTable.COLUMN_ID + "=" +
                    databaseContract.recipeIsTable.TABLE_NAME + "." + databaseContract.recipeIsTable.COLUMN_CAT_ID +
                    " WHERE " + databaseContract.recipeIsTable.TABLE_NAME + "." + databaseContract.recipeIsTable.COLUMN_ID + " = " + id + " AND " +
                    databaseContract.categoryTable.TABLE_NAME + "." + databaseContract.categoryTable.COLUMN_ID + " = " + catId);
            if (selectedCategories.moveToNext()){
                targetCat.setSelected(true);
            }

            select_qualification[i] = targetCat;
            cursor.moveToNext();
        }


        //Creating Spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            //System.out.println(select_qualification[i].getTitle());
            listVOs.add(select_qualification[i]);
        }

        CustomAdapter customAdapter = new CustomAdapter(EditRecipe.this, 0,
                listVOs);
        spinner.setAdapter(customAdapter);
/*
        Cursor selectedCategories = db.rawQuery("SELECT " + databaseContract.categoryTable.COLUMN_CATEGORY + " FROM " +
                databaseContract.categoryTable.TABLE_NAME + " INNER JOIN " +
                databaseContract.recipeIsTable.TABLE_NAME + " ON " +
                databaseContract.categoryTable.TABLE_NAME + "." + databaseContract.categoryTable.COLUMN_ID + "=" +
                databaseContract.recipeIsTable.TABLE_NAME + "." + databaseContract.recipeIsTable.COLUMN_CAT_ID +
                " WHERE " + databaseContract.recipeIsTable.TABLE_NAME + "." + databaseContract.recipeIsTable.COLUMN_ID + " = " + id);

        while(selectedCategories.moveToNext()) {
            Toast.makeText(getApplicationContext(), "THERE IS CATEGORY", Toast.LENGTH_SHORT).show();
             DropDownCategories selectedCategory = null;
             final int index;
            for (DropDownCategories category : select_qualification){
                if (category.getTitle() == selectedCategories.getString(0)){
                    selectedCategory = category;
                    //category.setSelected(true);
                    System.out.println("SELECTED CATEGORY = " + selectedCategory.getTitle() + "----------------");
                }
            }index = selectedCategory.getId();
            //spinner.setSelection(customAdapter.getPosition(selectedCategory));
            spinner.post(new Runnable() {
                @Override
                public void run() {
                    spinner.setSelection((index - 1));
                }
            });
        } */
        //Toast.makeText(getApplicationContext(), "UH OH SPAHGETTIO", Toast.LENGTH_SHORT).show();

        recipeNameTxtBx.setText(recipeName);
        ingredientTxtBx.setText(ingredient);
        totalTimeTxtBx.setText(totalTime);
        directionsTxtBx.setText(directions);

        //-------------------------- Updating New Recipe Info --------------------------
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] columns = {
                        databaseContract.recipeTable.COLUMN_NAME,
                        databaseContract.recipeTable.COLUMN_INSTRUCTIONS,
                        databaseContract.recipeTable.COLUMN_IMAGE,
                        databaseContract.recipeTable.COLUMN_COOK_TIME
                };
                ArrayList<Object> values = new ArrayList<>();

                recipeName = recipeNameTxtBx.getText().toString();
                recipeName.replaceAll("\\s", "");
                values.add(recipeName);

                values.add(directionsTxtBx.getText().toString());
                values.add(R.drawable.cookinupastorm);
                values.add(totalTimeTxtBx.getText().toString());

                String[] idColsArr = {databaseContract.recipeTable.COLUMN_ID};
                int[] idArr = {id};

                String updateResult = "";
                if (!db.updateTableRow(databaseContract.recipeTable.TABLE_NAME,
                        idColsArr,
                        idArr,
                        columns,
                        values
                )) {
                    updateResult = "Error";
                }

            //-------------------------- Updating Ingredient Info --------------------------
                String ingString = ingredientTxtBx.getText().toString();
                String[] splitString = ingString.split(",");
                int numOfLines = splitString.length / 2;
                String[] amount = new String[numOfLines];
                String[] ingredientArr = new String[numOfLines];

                for (int i = 0, j = 0; i < numOfLines; i++, j++) {
                    amount[i] = splitString[j];
                    j++;
                    ingredientArr[i] = splitString[j];
                }

                String[] ingColumns = {
                        databaseContract.ingredientTable.COLUMN_ID,
                        databaseContract.ingredientTable.TABLE_NAME
                };

                int ingredientIds[] = new int[numOfLines];

                for (int i = 0; i < ingredientArr.length; i++) {
                    String selection = "ingredient = '" + ingredientArr[i] + "'";
                    //Get the ids of Ingredients in the database, else make a new ingredient
                    try {
                        Cursor c = db.queryTable(
                                databaseContract.ingredientTable.TABLE_NAME,
                                ingColumns,
                                selection
                        );
                        c.moveToFirst();
                        ingredientIds[i] = c.getInt(0);
                    } catch (Exception e) {
                        ArrayList<Object> arrayListIng = new ArrayList<>();
                        String ingAddColumns[] = {databaseContract.ingredientTable.COLUMN_INGREDIENT};
                        arrayListIng.add(ingredientArr[i]);
                        ingredientIds[i] = db.addTableRow(
                                databaseContract.ingredientTable.TABLE_NAME,
                                ingAddColumns,
                                arrayListIng
                        );
                    }
                }

                //-------------------------- Updating RecipeUses Info --------------------------
                idColsArr = new String[]{databaseContract.recipeUsesTable.COLUMN_ID,
                        databaseContract.recipeUsesTable.COLUMN_ING_ID};

                String[] deleteCategories = {databaseContract.recipeUsesTable.COLUMN_ID};
                int[] deleteIds = {id};
                db.deleteTableRow(databaseContract.recipeUsesTable.TABLE_NAME,
                        deleteCategories,
                        deleteIds);

                for (int x = 0; x < amount.length; x++) {
                    ArrayList<Object> recipeHasInput = new ArrayList<>();




                        String[] recipeUsesColumns = new String[]{
                                databaseContract.recipeUsesTable.COLUMN_ID,
                                databaseContract.recipeUsesTable.COLUMN_ING_ID,
                                databaseContract.recipeUsesTable.COLUMN_QUANTITY
                        };
                        recipeHasInput.add(id); //Recipe id
                        recipeHasInput.add(ingredientIds[x]); //Ingredient id
                        recipeHasInput.add(amount[x]); //Amount of ingredient
                        db.addTableRow(
                                databaseContract.recipeUsesTable.TABLE_NAME,
                                recipeUsesColumns,
                                recipeHasInput
                        );
                   // }
                }

                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), Recipe.class);
                intent.putExtra("id", id);
                startActivity(intent);


                //-------------------------- Updating RecipeUses Info --------------------------
                deleteCategories = new String[] {databaseContract.recipeIsTable.COLUMN_ID};
                deleteIds = new int[] {id};
                db.deleteTableRow(databaseContract.recipeIsTable.TABLE_NAME,
                        deleteCategories,
                        deleteIds);

                //Category Entry into dataBase
                String[] recipeIsColumns = {
                        databaseContract.recipeIsTable.COLUMN_ID,
                        databaseContract.recipeIsTable.COLUMN_CAT_ID
                };

                //Make a ArrayList of selected vals
                ArrayList<String> selectedCategories = new ArrayList<String>();

                //Populate ArrayList
                //Loop through and add to ArrayList if checked
                for (DropDownCategories loopCategory : listVOs) {
                    if (loopCategory.isSelected()) {
                        ArrayList<Object> inputArray = new ArrayList<>();
                        inputArray.add(id);
                        inputArray.add(loopCategory.getId());
                        db.addTableRow(
                                databaseContract.recipeIsTable.TABLE_NAME,
                                recipeIsColumns,
                                inputArray
                        );
                    }
                }

            }
        });

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result;
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

                //DBHelper db = new DBHelper(getApplicationContext());
                //ArrayList<Object> values = new ArrayList<Object>();
                //values.add(RESULT_LOAD_IMAGE);
                //Boolean newRowId = db.addTableRow(databaseContract.recipeTable.TABLE_NAME,
                //null,
                //values);
                //if(newRowId){
                //result = "Image Added";
                //}
                //else{
                //result = "Error";
                //}
                //Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                //toast.show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null,null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            photoBtn.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        getSupportActionBar().setTitle("Edit Recipe Name");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DBHelper db = new DBHelper(getApplicationContext());
        if (item.getItemId() == R.id.goHomeEdit) {
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.backOption) {
            Intent intent = new Intent(getApplicationContext(), Recipe.class);
            intent.putExtra("id", id);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.deleteOption) {
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            //Go through DataBase and delete all accounts with recipeID
            db.deleteRecipe(id);
            startActivity(intent);
            return true;
        } else
            return true;

    }



}
