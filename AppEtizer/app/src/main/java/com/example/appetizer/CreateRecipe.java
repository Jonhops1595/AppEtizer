package com.example.appetizer;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;



public class CreateRecipe extends AppCompatActivity {
    EditText recname, total, ing, dir;
    Button save;
    ImageButton photo;
    Bitmap photoBitmap;
    DropDownCategories[] select_qualification;
    int recipeId;
    private static int RESULT_LOAD_IMAGE = 1;
    ArrayList<DropDownCategories> listVOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_recipe_layout);
        recname = (EditText) findViewById(R.id.RecipeName);
        total = (EditText) findViewById(R.id.totaltime);
        ing = (EditText) findViewById(R.id.Ingredients);
        dir = (EditText) findViewById(R.id.Directions);
        photo = (ImageButton) findViewById(R.id.recipePhoto);
        save = (Button) findViewById(R.id.SaveButton);


        DBHelper db = new DBHelper(getApplicationContext());

        //Creating + Populating Category Spinner
        Cursor cursor = db.dumpTable(databaseContract.categoryTable.TABLE_NAME);
        int maxCategories = cursor.getCount();
        maxCategories++;
        select_qualification = new DropDownCategories[maxCategories];
        select_qualification[0] = new DropDownCategories(-1, "Select Categories"); //Top or Title of the DropDown

        cursor.moveToFirst();
        for (int i = 1; i < maxCategories; i++) { //Populating List
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("category"));
            select_qualification[i] = new DropDownCategories(id, name);
            cursor.moveToNext();
        }


        //Creating Spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            System.out.println(select_qualification[i].getTitle());
            listVOs.add(select_qualification[i]);
        }

        CustomAdapter customAdapter = new CustomAdapter(CreateRecipe.this, 0,
                listVOs);
        spinner.setAdapter(customAdapter);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper db = new DBHelper(getApplicationContext());

                //Adding to recipe table
                addRecipe();
                //Adding ingredients into ingredient table
                addIngredients();
                //Adding categories to tables
                addCategories();


            //Sets texts back to default
                recname.setText("");
                total.setText("");
                ing.setText("");
                dir.setText("");
                recname.requestFocus();
            }
        });



        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i,"Pick Recipe Image"),1);

            }
        });
    }

    private void addRecipe(){


        DBHelper db = new DBHelper(getApplicationContext());
        ArrayList<Object> values = new ArrayList<Object>();
        values.add(recname.getText().toString()); //Recipe name, Arg 1
        values.add(dir.getText().toString()); //Instructions, Arg 2
        if(photoBitmap == null) {
            //add default image
            values.add(R.drawable.cookinupastorm);
        }
        else {
            //Recipe Image, Arg 3

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("recipeImages", Context.MODE_PRIVATE);
            File file = new File(directory, recname +".jpg");
            if (!file.exists()) {
                Log.d("path", file.toString());
                FileOutputStream fos = null;
                try{
                    fos = new FileOutputStream(file);
                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    //Somehow add file that we just created of the jpg to values() ????

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    values.add(R.drawable.cookinupastorm); //Add default image
                }
            }

        }
        values.add(total.getText().toString()); //Time, Arg 4

        //Columns
        String[] columns = {
                databaseContract.recipeTable.COLUMN_NAME,
                databaseContract.recipeTable.COLUMN_INSTRUCTIONS,
                databaseContract.recipeTable.COLUMN_IMAGE,
                databaseContract.recipeTable.COLUMN_COOK_TIME
        };


        int duration = Toast.LENGTH_LONG;
        String result;
        recipeId = db.addTableRow(databaseContract.recipeTable.TABLE_NAME, //Keep recipeID
                columns,
                values);
        if(recipeId >= 0){
            result = "Recipe Added";
        }
        else{
            result = "Error";
        }

        Toast toast = Toast.makeText(getApplicationContext(), result, duration);
        toast.show();

    }

    private void addIngredients() {
        //Inputs ingredients into ingredient table
                /*Input ingredients like this
                    Amount,ingredient
                 */

        DBHelper db = new DBHelper(getApplicationContext());
        String ingString = ing.getText().toString(); //Ingredient string

        String splitString[] = ingString.split(","); //Splits input
        int numOfLines = splitString.length / 2;
        String amount[] = new String[numOfLines]; //Amount of ingredient
        String ingredient[] = new String[numOfLines]; //Ingredients

        int counter = 0;
        for (int i = 0; i < splitString.length; i++, counter++) {
            amount[counter] = splitString[i];
            i++;
            ingredient[counter] = splitString[i];
        }

        //Prep to query ingredient table for ingredient ids or to input new ingredients

        String[] ingColumns = {
                databaseContract.ingredientTable.COLUMN_ID,
                databaseContract.ingredientTable.TABLE_NAME
        };

        int ingredientIds[] = new int[numOfLines]; //IDs for ingredients

        for (int j = 0; j < ingredient.length; j++) { //Gets all the ids for the ingredients or adds to the ingredient table if not found
            String selection = "ingredient = '" + ingredient[j] + "'";
            try {
                Cursor cursor = db.queryTable(
                        databaseContract.ingredientTable.TABLE_NAME,
                        ingColumns,
                        selection
                );
                cursor.moveToFirst();
                ingredientIds[j] = cursor.getInt(0);
            } catch (Exception e) {
                //Previous instance of ingredient not found, input as new
                ArrayList<Object> arrayListIng = new ArrayList<>();
                String ingAddColumns[] = {databaseContract.ingredientTable.COLUMN_INGREDIENT};
                arrayListIng.add(ingredient[j]); //ArrayList with ingredient
                ingredientIds[j] = db.addTableRow(
                        databaseContract.ingredientTable.TABLE_NAME,
                        ingAddColumns,
                        arrayListIng
                );
            }
        }

        //Adds to recipeUses table
        String[] recipeUsesColumns = {
                databaseContract.recipeUsesTable.COLUMN_ID,
                databaseContract.recipeUsesTable.COLUMN_ING_ID,
                databaseContract.recipeUsesTable.COLUMN_QUANTITY
        };

        for (int x = 0; x < amount.length; x++) { //Loops adds to recipeUses table
            ArrayList<Object> recipeHasInput = new ArrayList<>();
            recipeHasInput.add((int) recipeId); //Recipe id
            recipeHasInput.add((int) ingredientIds[x]); //Ingredient id
            recipeHasInput.add(amount[x]); //Amount of ingredient
            db.addTableRow(
                    databaseContract.recipeUsesTable.TABLE_NAME,
                    recipeUsesColumns,
                    recipeHasInput
            );
        }
    }

    private void addCategories(){
        DBHelper db = new DBHelper(getApplicationContext());

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
                inputArray.add(recipeId);
                inputArray.add(loopCategory.getId());
                db.addTableRow(
                        databaseContract.recipeIsTable.TABLE_NAME,
                        recipeIsColumns,
                        inputArray
                );
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK && requestCode == 1)

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                photoBitmap = BitmapFactory.decodeStream(inputStream);
                photo.setImageBitmap(photoBitmap);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.add_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.InputRecipe) {
            Intent intent = new Intent (getApplicationContext(), CreateRecipe.class);
            return true;
        }  if (id == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);
            return true;
        }

        //Clear the preferences file
        if (id == R.id.back) {
            Intent intent = new Intent(getApplicationContext(), Add_Item.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
