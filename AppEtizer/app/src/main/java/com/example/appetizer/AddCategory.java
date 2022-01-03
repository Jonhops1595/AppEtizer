package com.example.appetizer;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCategory extends AppCompatActivity {
    EditText category;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category_layout);
        category = (EditText) findViewById(R.id.CategoryName);
        save = (Button) findViewById(R.id.SaveButton);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int duration = Toast.LENGTH_LONG;
                String result;
                DBHelper db = new DBHelper(getApplicationContext());
                ArrayList<Object> values = new ArrayList<Object>();

                //Category to be added
                values.add( category.getText().toString());
                //Column name
                String[] columns = {databaseContract.categoryTable.COLUMN_CATEGORY};
                //Adding into database
                long newRowId = db.addTableRow(databaseContract.categoryTable.TABLE_NAME,
                        columns,
                        values);

                if(newRowId >= 0){
                    result ="Category Created";
                }
                else{
                    result = "ERROR" ;
                }
                Toast toast = Toast.makeText(getApplicationContext(), result, duration);
                toast.show();
                category.setText("");
                category.requestFocus();
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