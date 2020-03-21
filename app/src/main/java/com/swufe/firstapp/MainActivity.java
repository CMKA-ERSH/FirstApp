package com.swufe.firstapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView showText;
    EditText inp;
    Button btn1;
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showText = findViewById(R.id.showText);
        inp = findViewById(R.id.inpText);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

    }
    public void converse(View btn1){
        double c = Double.parseDouble(inp.getText().toString());
        temperature(c);
    }
    public void converseminus(View btn2){
        double cMinus = 0 - Double.parseDouble(inp.getText().toString());
        temperature(cMinus);
    }
    private void temperature(double i){
        Log.i("converse","temperature = "+i+"degrees Celsius");
        double F =i*1.8+32;
        showText.setText(""+F);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
