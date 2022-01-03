package com.example.appetizer;

import android.provider.BaseColumns;

public class databaseContract {

    public databaseContract(){}

    public static abstract class recipeTable implements BaseColumns {
        public static final String TABLE_NAME = "recipe";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COOK_TIME = "cookTime";
        public static final String COLUMN_INSTRUCTIONS = "instructions";
        public static final String COLUMN_IMAGE = "image";
    }

    public static abstract class ingredientTable implements BaseColumns {
        public static final String TABLE_NAME = "ingredient";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_INGREDIENT = "ingredient";
    }

    public static abstract class categoryTable implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CATEGORY = "category";

    }

    public static abstract class recipeUsesTable implements BaseColumns {
        public static final String TABLE_NAME = "recipeUses";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ING_ID = "ingId";
        public static final String COLUMN_QUANTITY = "quantity";

    }

    public static abstract class recipeIsTable implements BaseColumns {
        public static final String TABLE_NAME = "recipeIs";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CAT_ID = "catId";
    }

    public static abstract class ingsHaveTable implements BaseColumns {
        public static final String TABLE_NAME  = "ingsHave";
        public static final String COLUMN_ING_ID = "ingId";
        public static final String COLUMN_CAT_ID = "catId";
    }
}
