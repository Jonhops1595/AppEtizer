package com.example.appetizer;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class Add_Item extends AppCompatActivity implements View.OnClickListener{
    Button addCat, inputRec, editBtn, delBtn;
    ImageButton scanRec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__item);
        addCat = (Button) findViewById(R.id.AddCategory);
        inputRec = (Button) findViewById(R.id.InputRecipe);
        editBtn = (Button) findViewById(R.id.EditButton);
        delBtn = (Button) findViewById(R.id.DeleteCategory);
        scanRec = (ImageButton) findViewById(R.id.ScanRecipe);

        addCat.setOnClickListener(this);
        inputRec.setOnClickListener(this);
        scanRec.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.AddCategory:
                Intent intent = new Intent(this,AddCategory.class);
                startActivity(intent);
                break;
            case R.id.InputRecipe:
                Intent intent2 = new Intent(this,CreateRecipe.class);
                startActivity(intent2);
                break;
            case R.id.ScanRecipe:
                Intent intent3 = new Intent(this,scan.class);
                startActivity(intent3);
                break;
            case R.id.EditButton:
                Intent intent4 = new Intent(this,Edit_Category.class);
                startActivity(intent4);
                break;
            case R.id.DeleteCategory:
                Intent intent5 = new Intent(this,Delete_Category.class);
                startActivity(intent5);
                break;
        }
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
        if (id == R.id.back) {
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
