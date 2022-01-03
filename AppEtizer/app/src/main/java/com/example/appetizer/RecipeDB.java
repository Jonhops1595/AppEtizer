package com.example.appetizer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "recipe.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String AUTO_INCREMENT = " AUTOINCREMENT NOT NULL";
    private static final String COMMA = ",";



    /*
    Defining the SQL Create Table Instructions
     */
    private static final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + databaseContract.recipeTable.TABLE_NAME + " (" +
            databaseContract.recipeTable.COLUMN_ID + INT_TYPE + " PRIMARY KEY" +  AUTO_INCREMENT + COMMA +
            databaseContract.recipeTable.COLUMN_NAME + TEXT_TYPE + COMMA +
            databaseContract.recipeTable.COLUMN_COOK_TIME + TEXT_TYPE + COMMA +
            databaseContract.recipeTable.COLUMN_INSTRUCTIONS + TEXT_TYPE + COMMA +
            databaseContract.recipeTable.COLUMN_IMAGE + TEXT_TYPE + " )";

    private static final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + databaseContract.ingredientTable.TABLE_NAME + " (" +
            databaseContract.ingredientTable.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + AUTO_INCREMENT +COMMA +
            databaseContract.ingredientTable.COLUMN_INGREDIENT + TEXT_TYPE + " )";

    private static final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + databaseContract.categoryTable.TABLE_NAME + " (" +
            databaseContract.categoryTable.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + AUTO_INCREMENT + COMMA +
            databaseContract.categoryTable.COLUMN_CATEGORY + TEXT_TYPE + " )";

    private static final String SQL_CREATE_RECIPE_USES_TABLE = "CREATE TABLE " + databaseContract.recipeUsesTable.TABLE_NAME + " (" +
            databaseContract.recipeUsesTable.COLUMN_ID + INT_TYPE + COMMA +
            databaseContract.recipeUsesTable.COLUMN_ING_ID + INT_TYPE  + COMMA +
            databaseContract.recipeUsesTable.COLUMN_QUANTITY + TEXT_TYPE + COMMA +
            " PRIMARY KEY (" + databaseContract.recipeUsesTable.COLUMN_ID + COMMA + databaseContract.recipeUsesTable.COLUMN_ING_ID + ")" + COMMA +
            " FOREIGN KEY (" + databaseContract.recipeUsesTable.COLUMN_ID + ") REFERENCES " +
            databaseContract.recipeTable.TABLE_NAME + "(" + databaseContract.recipeTable.COLUMN_ID + ")" + COMMA +
            " FOREIGN KEY (" + databaseContract.recipeUsesTable.COLUMN_ING_ID + ") REFERENCES " +
            databaseContract.ingredientTable.TABLE_NAME + "(" + databaseContract.ingredientTable.COLUMN_ID + ")" + " )";

    private static final String SQL_CREATE_RECIPE_IS_TABLE = "CREATE TABLE " + databaseContract.recipeIsTable.TABLE_NAME + " (" +
            databaseContract.recipeIsTable.COLUMN_ID + INT_TYPE + COMMA +
            databaseContract.recipeIsTable.COLUMN_CAT_ID + INT_TYPE + COMMA +
            " PRIMARY KEY (" + databaseContract.recipeIsTable.COLUMN_ID + COMMA + databaseContract.recipeIsTable.COLUMN_CAT_ID + ")" + COMMA +
            " FOREIGN KEY (" + databaseContract.recipeIsTable.COLUMN_ID + ") REFERENCES " +
            databaseContract.recipeTable.TABLE_NAME + "(" + databaseContract.recipeTable.COLUMN_ID +")" + COMMA +
            " FOREIGN KEY (" + databaseContract.recipeIsTable.COLUMN_CAT_ID + ") REFERENCES " +
            databaseContract.categoryTable.TABLE_NAME + "(" + databaseContract.categoryTable.COLUMN_ID +")" + ")";

    private static final String SQL_CREATE_INGS_HAVE_TABLE = "CREATE TABLE " + databaseContract.ingsHaveTable.TABLE_NAME + " (" +
            databaseContract.ingsHaveTable.COLUMN_ING_ID + INT_TYPE + COMMA +
            databaseContract.ingsHaveTable.COLUMN_CAT_ID + INT_TYPE + COMMA +
            " PRIMARY KEY (" + databaseContract.ingsHaveTable.COLUMN_ING_ID + COMMA + databaseContract.ingsHaveTable.COLUMN_CAT_ID + ")" + COMMA +
            " FOREIGN KEY (" + databaseContract.ingsHaveTable.COLUMN_ING_ID + ") REFERENCES " +
            databaseContract.ingredientTable.TABLE_NAME + "(" + databaseContract.ingredientTable.COLUMN_ID +")" + COMMA +
            " FOREIGN KEY (" + databaseContract.ingsHaveTable.COLUMN_CAT_ID + ") REFERENCES " +
            databaseContract.categoryTable.TABLE_NAME + "(" + databaseContract.categoryTable.COLUMN_ID +")" + ")";

    private static final String SQL_DELETE = "DROP TABLE IF EXISTS " + databaseContract.recipeIsTable.TABLE_NAME +
            COMMA + databaseContract.ingredientTable.TABLE_NAME +
            COMMA + databaseContract.categoryTable.TABLE_NAME +
            COMMA + databaseContract.recipeUsesTable.TABLE_NAME +
            COMMA + databaseContract.recipeIsTable.TABLE_NAME +
            COMMA + databaseContract.ingsHaveTable.TABLE_NAME;

    private static String SQL_CREATE_FAVORITES = "INSERT INTO " + databaseContract.categoryTable.TABLE_NAME +
            "(" + databaseContract.categoryTable.COLUMN_CATEGORY + ")" +
            "VALUES ('Favorite'),('Breakfast'),('Lunch'),('Dinner'),('Vegan'),('Vegeterian');";

    public RecipeDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createTables(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_USES_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_IS_TABLE);
        db.execSQL(SQL_CREATE_INGS_HAVE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        createTables(db);
    }

    //query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)

}
