package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Second2Activity extends AppCompatActivity {
    TextView score;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button reset_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second2);

        score = (TextView)findViewById(R.id.score);



        }
        public void btnadd1(View btn) {
            showScore(1);
        }

        public void btnadd2(View btn) {
            showScore(2);
        }

        public void btnadd3(View btn) {
            showScore(3);
        }
        public void resetbtn(View btn) {
            Log.i("reset","score=0");
            score.setText(""+0);
        }

        private void showScore(int i){
            Log.i("show","int = "+i);
            String oldScore = (String)score.getText();
            int newScore = Integer.parseInt(oldScore)+i;
            score.setText(""+newScore);
        }
}
