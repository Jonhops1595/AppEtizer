package com.example.appetizer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    GridView gridview;

    String[] names;
    int[] images;
    int[] ids;
    Cursor activityCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBHelper db = new DBHelper(getApplicationContext());

        //Sets cursor to query the whole table
        activityCursor = db.getFavoriteRecipe();

        //displays the queried recipes in the gridview on the homescreen
        display();

        //floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this, Add_Item.class));
            }
        });
    }

    //custom adapter for creating the gridview
    public class CustomAdapter extends BaseAdapter {

        private final int[] id;
        private String[] recipeName;
        private int[] recipePhoto;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter(String[] recipeName, int[] recipePhoto, int[] id, Context context) {
            this.recipeName = recipeName;
            this.recipePhoto = recipePhoto;
            this.id = id;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return recipePhoto.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.home_recipe_item, parent, false);
            }

            TextView name = view.findViewById(R.id.name);
            ImageView image = view.findViewById(R.id.imageView);

            name.setText(recipeName[position]);
            try {
                image.setImageResource(recipePhoto[position]);
            } catch (Exception e) {

            }


            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);

        //search bar
        MenuItem myActionMenuItem = menu.findItem( R.id.app_bar_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        //listens for when the user uses the search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // sets the cursor to the cursor that the doMySearch method returns
                activityCursor = doMySearch(query);
                System.out.println(query);
                //calls display and displays the searched items
                display();
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        return true;
    }

    //method to query the database using the name that the user inputed and returns a cursor
    private Cursor doMySearch(String query) {


        DBHelper db = new DBHelper(getApplicationContext());

        Cursor cursor = db.getSearchRecipes(query);

        return cursor;
    }

    //Displays the desired recipes in the gridview
    private void display(){
        int maxRecipes = activityCursor.getCount();
        names = new String[maxRecipes];
        images = new int[maxRecipes];
        ids = new int[maxRecipes];

        activityCursor.moveToFirst();
        for (int i = 0; i < maxRecipes; i++) {
            names[i] = activityCursor.getString(activityCursor.getColumnIndex(databaseContract.recipeTable.COLUMN_NAME));
            images[i] = activityCursor.getInt(activityCursor.getColumnIndex(databaseContract.recipeTable.COLUMN_IMAGE));
            ids[i] = activityCursor.getInt(activityCursor.getColumnIndex(databaseContract.recipeTable.COLUMN_ID));
            activityCursor.moveToNext();
        }

        setContentView(R.layout.home_screen);

        gridview = findViewById(R.id.simpleGridView);

        CustomAdapter customAdapter = new CustomAdapter(names, images, ids, this);

        gridview.setAdapter(customAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedID = ids[position];

                startActivity(new Intent(HomeScreen.this, Recipe.class).putExtra("id", selectedID));
            }
        });
    }
}
