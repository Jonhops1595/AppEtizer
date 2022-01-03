package com.example.appetizer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Edit_Category extends AppCompatActivity {

    String[] categories;
    int[] ids;
    Cursor activitycursor;
    Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__category);
        DBHelper db = new DBHelper(getApplicationContext());
        String[] columns = {databaseContract.categoryTable.COLUMN_ID, databaseContract.categoryTable.COLUMN_CATEGORY};
        String[] column ={databaseContract.categoryTable.COLUMN_CATEGORY};
        EditText editText = (EditText) findViewById(R.id.identifier);
        Button save = (Button) findViewById(R.id.ERsaveButton);


        activitycursor = db.queryTable(databaseContract.categoryTable.TABLE_NAME,columns,null);

        int numCat = activitycursor.getCount();
        categories = new String[numCat];
        ids = new int[numCat];
        activitycursor.moveToFirst();
        for(int z = 0; z < numCat; z++){
            categories[z] = activitycursor.getString(activitycursor.getColumnIndex(databaseContract.categoryTable.COLUMN_CATEGORY));
            ids[z] = activitycursor.getInt(activitycursor.getColumnIndex(databaseContract.categoryTable.COLUMN_ID));
            activitycursor.moveToNext();
        }

        List<String> allCat = new ArrayList<String>(Arrays.asList(categories));
        ListView listView = (ListView)findViewById(R.id.category_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.row_item, R.id.listItem, categories);

        listView.setAdapter(adapter);
        listView.setEmptyView(editText);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit;
                for(int z = 0; z < listView.getCount(); z++){
                    v = listView.getChildAt(z);
                    edit = (EditText) v.findViewById(R.id.listItem);

                    String newName;

                    newName = edit.getText().toString();

                    ArrayList<Object> nCat = new ArrayList<Object>(); //ArrayList to put values in
                    nCat.add(newName); //Adds new name to values

                    String [] idNames = {databaseContract.categoryTable.COLUMN_ID}; //id Name in DB
                    int [] inputIds = {ids[z]};  //ID of cat to change
                    String [] col = {databaseContract.categoryTable.COLUMN_CATEGORY};


                    db.updateTableRow(databaseContract.categoryTable.TABLE_NAME,
                            idNames,
                            inputIds,
                            col,
                            nCat);
                }

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

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
        if (id == R.id.home) {
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
