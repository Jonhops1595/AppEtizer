package com.example.appetizer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delete_Category extends AppCompatActivity {

    String[] categories;
    int[] ids;
    Cursor activitycursor;
    Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__category);
        DBHelper db = new DBHelper(getApplicationContext());
        String[] columns = {databaseContract.categoryTable.COLUMN_ID, databaseContract.categoryTable.COLUMN_CATEGORY};
        String[] column ={databaseContract.categoryTable.COLUMN_CATEGORY};
        TextView sel = (TextView) findViewById(R.id.deleted);


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
        ListView listView = (ListView)findViewById(R.id.delete_category_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.delete_row__item, R.id.listDeleteItem, categories);

        listView.setAdapter(adapter);
        listView.setEmptyView(sel);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String [] idNames = {databaseContract.categoryTable.COLUMN_ID};
                int[] selid={1};
                for(int z = 0; z < categories.length;z++){
                    if(categories[z] == selectedItem){
                        selid[0]= ids[z];
                    }
                }
                db.deleteTableRow(databaseContract.categoryTable.TABLE_NAME, idNames, selid);
                
                String result = "Category Deleted";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(), result,duration);
                toast.show();


                Intent intent = new Intent(getApplicationContext(), Delete_Category.class);
                startActivity(intent);

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
