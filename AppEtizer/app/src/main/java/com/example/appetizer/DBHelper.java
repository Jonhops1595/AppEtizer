package com.example.appetizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper {

    SQLiteDatabase db, dbWritable;
    RecipeDB rDB;

    //getApplicationContext() is the parameter
    //Is Required to create the database reference
    public DBHelper(Context context){
        rDB = new RecipeDB(context);
        SQLiteDatabase database = rDB.getReadableDatabase();
        db = database;
        database = rDB.getWritableDatabase();
        dbWritable = database;
    }

    public void deleteDB(Context context){
        context.deleteDatabase(rDB.DATABASE_NAME);
    }

    /*
    Using databaseContract for tableName: databaseContract./table name/.TABLE_NAME
    i.e. databaseContract.recipeTable.TABLE_NAME
    */
    public Cursor dumpTable(String tableName){
        return db.query(tableName, null, null, null, null, null, null);
    }

    /*
    Using databaseContract for table name and cols: databaseContract./table name/.TABLE_NAME
    i.e. databaseContract.recipeTable.TABLE_NAME

    i.e. [databaseContract.recipeTable.COLUMN_NAME]

    selection can be a SQL WHERE clause without the WHERE keyword
    i.e. "id = 5"
    selection can be left null
     */
    public Cursor queryTable(String tableName, String[] columns,@Nullable String selection){
        return db.query(tableName, columns, selection, null, null, null, null);
    }

    /*
    Using databaseContract for tableNames: [databaseContract./table name/.TABLE_NAME . . .]
    tableNames must have 3 tables

    Using databaseContract for columns: [databaseContract./table name/.COLUMN_NAME . . .]
    columns can be left null for all columns

    where can be a SQL WHERE clause without the WHERE keyword
    i.e. "id = 5"
    where can be left null
     */
    public Cursor joinTables(String[] tableNames, @Nullable String[] columns, @Nullable String where){
        if (tableNames.length != 3) throw new IllegalArgumentException("String[] tableNames must have length 3");
        //SELECT CLAUSE
        String query = "SELECT ";

        //If no columns specified return all columns
        //Else return specified columns
        if (columns == null) query += " *";
        else{
            for (String col : columns) {
                query += " " + col + ",";
            }
            //Removing the the extra comma
            query = query.substring(0, query.length() - 1);
        }

        //FROM CLAUSE
        query += " FROM";
        query +=" " + tableNames[0] + " NATURAL JOIN " + tableNames[1];
        query +=" NATURAL JOIN " + tableNames[2];


        //WHERE CLAUSE
        if (where != null){
            query += " WHERE " + where;
        }
        System.out.println(query);

        return db.rawQuery(query, null, null);

    }
    
    public Cursor rawQuery(String query){
        return db.rawQuery(query, null, null);
    }

    /*
    Returns the cursor to retrieve one recipe and all it's information for the Recipe class
    Column order:
    1. Recipe Name
    2. Instructions String
    3. Image code
    4. Quantity
    5. Cook Time
    6. Ingredient
     */
    public Cursor getRecipeInfo(int recipeId) {
        String query = "SELECT recipe.id,name, instructions, image, quantity, ingredient, cookTime FROM recipe NATURAL JOIN recipeUses INNER JOIN ingredient ON recipeUses.ingId = ingredient.id WHERE recipe.id = " + recipeId;
        return db.rawQuery(query, null, null);
    }

    /*
    Returns the cursor to retrieve all the recipes with the given category ID with a distinct recipe ID
    1. Recipe ID
    2. Recipe Name
    3. Recipe Image
    4. Category Name
     */
    public Cursor getSearchRecipes(String search){
        String query = "SELECT DISTINCT recipe.id, recipe.name, image " +
                "FROM recipe " +
                "NATURAL JOIN recipeIs " +
                "INNER JOIN category ON catId = category.id " +
                "WHERE name LIKE '%" + search + "%' " +
                "OR category LIKE '%" + search + "%'";

        return db.rawQuery(query,null,null);
    }
    
    /*
        Returns the cursor to retrieve all the categories with the given recipeID
        1. Category Name
     */
    public Cursor getCategories(int recipeID){
        String query = "SELECT category " +
                "FROM category c " +
                "INNER JOIN recipeIs r ON c.id = r.catId " +
                "WHERE r.id = " + recipeID;

        return db.rawQuery(query,null,null);

    }

    //Returns the cursor to retrieve all recipes that are in the order by catID, if a recipe has more than one category then
    //it only returns the recipe once
    public Cursor getFavoriteRecipe(){
        String query = "SELECT DISTINCT r.id, r.image, r.name " +
                "FROM recipe r " +
                "NATURAL JOIN recipeIs " +
                "ORDER BY catId";

        return db.rawQuery(query, null, null);
    }
    /*
    Using databaseContract for tableName: databaseContract./table name/.TABLE_NAME
    Using databaseContract for columns: [databaseContract./table name/.COLUMN_NAME . . .]
    args are the values associated with the columns, ArrayList is used so that int and String can be in same List
    DO NOT INCLUDE "ID" WHEN INSERTING, that is taken care of
     */
    public int addTableRow(String tableName, String[] columnNames, ArrayList<Object> args){
        if (columnNames.length != args.size()) throw new IllegalArgumentException("columnNames and args must be the same length");
        ContentValues values = new ContentValues();

        for (int i = 0; i < columnNames.length; i++){
            try{
                values.put(columnNames[i],
                         Integer.parseInt( (String) args.get(i)));
            }
            catch (NumberFormatException e){
                values.put(columnNames[i], (String) args.get(i));
            } catch (ClassCastException e){
                values.put(columnNames[i], (int) args.get(i));
            }

        }

        int newId = (int) dbWritable.insert(tableName, null, values);

        //Return false if data was not inserted correctly
        return newId;
    }

      public boolean deleteTableRow(String tableName, String[] idNames, int[] ids){
        String query = "DELETE FROM " + tableName + " WHERE ";

        query += idNames[0] + " = " + ids[0];
        if (idNames.length > 1) query += " AND " + idNames[1] + " = " + ids[1];
        try {
            db.execSQL(query);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    
    public void deleteRecipe(int id){
        String[] recipeId = {databaseContract.recipeTable.COLUMN_ID};
        int[] ids = {id};
        deleteTableRow(databaseContract.recipeIsTable.TABLE_NAME, recipeId, ids);
        deleteTableRow(databaseContract.recipeUsesTable.TABLE_NAME, recipeId, ids);
        deleteTableRow(databaseContract.recipeTable.TABLE_NAME, recipeId, ids);
    }
    

 /*
    Using databaseContract for tableName: databaseContract./table name/.TABLE_NAME
    Using databaseContract for idNames: [databaseContract./table name/.COLUMN_NAME . . .]
    Ids in an array to support columns with composite keys
    Using databaseContract for columns: [databaseContract./table name/.COLUMN_NAME . . .]
    args are the values associated with the columns, ArrayList is used so that int and String can be in same List
    DO NOT INCLUDE "ID" WHEN INSERTING, that is taken care of
     */
    public boolean updateTableRow(String tableName, String[] idNames, int[] ids, String[] columnNames, ArrayList<Object> args){
        if (columnNames.length != args.size()) throw new IllegalArgumentException("columnNames and args must be the same length");
        if (idNames.length != ids.length) throw new IllegalArgumentException(("idNames and ids must be the same length!"));

        String whereClause = idNames[0] + " = " + ids[0];
        if (idNames.length > 1) whereClause += " AND " + idNames[1] + " = " + ids[1];

        Cursor c = queryTable(tableName, columnNames, whereClause);
        if (c.getCount() == 0){ return false; }

        String query = "UPDATE " + tableName + " SET ";
        for(int i = 0; i < columnNames.length - 1; i++){
            query += columnNames[i] + " = '" + args.get(i) +"', ";
        }query += columnNames[columnNames.length-1] + " = '" + args.get(args.size() - 1) +"' ";

        query += "WHERE ";
        query += whereClause;

        query += ";";

        db.execSQL(query);
        return true;

        /*
        ContentValues values = new ContentValues();

        for (int i = 0; i < columnNames.length; i++){
            try{
                values.put(columnNames[i],
                        Integer.parseInt( (String) args.get(i)));
            }
            catch (NumberFormatException e){
                values.put(columnNames[i], (String) args.get(i));
            } catch (ClassCastException e){
                values.put(columnNames[i], (int) args.get(i));
            }

        }

        String whereClause = "'" + idNames[0] + "' = " + ids[0];
        if (idNames.length > 1) whereClause += " AND '" + idNames[1] + "' = " + ids[1];

        int rowsAffected = dbWritable.update(tableName, values, whereClause, null);

            return rowsAffected == 1;

         */
    }




}
