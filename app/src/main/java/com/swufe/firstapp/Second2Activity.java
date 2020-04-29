package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Second2Activity extends AppCompatActivity {
    TextView score;
    TextView scoreB;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button reset_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second2);

        score = (TextView)findViewById(R.id.score);
        scoreB = (TextView)findViewById(R.id.scoreB);
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String scorea = ((TextView)findViewById(R.id.score)).getText().toString();
        String scoreb = ((TextView)findViewById(R.id.scoreB)).getText().toString();

        outState.putString("teama_score", scorea);
        outState.putString("teamb_score", scoreb);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String scorea = savedInstanceState.getString("teama_score");
        String scoreb = savedInstanceState.getString("teamb_score");

        ((TextView)findViewById(R.id.score)).setText(scorea);
        ((TextView)findViewById(R.id.scoreB)).setText(scoreb);
    }

    public void btnadd1(View btn) {
            if(btn.getId() == R.id.btn_1){
                showScore(1);
            }else{
                showScoreB(1);
            }
        }

        public void btnadd2(View btn) {
            if(btn.getId() == R.id.btn_2){
                showScore(2);
            }else{
                showScoreB(2);
            }
        }

        public void btnadd3(View btn) {
            if(btn.getId() == R.id.btn_3){
                showScore(3);
            }else{
                showScoreB(3);
            }
        }
        public void resetbtn(View btn) {
            Log.i("reset","score=0");
            score.setText(""+0);
            scoreB.setText(""+0);
        }

        private void showScore(int i){
            Log.i("show","int = "+i);
            String oldScore = (String)score.getText();
            int newScore = Integer.parseInt(oldScore)+i;
            score.setText(""+newScore);
        }
        private void showScoreB(int i){
            Log.i("show","int = "+i);
            String oldScore = (String)scoreB.getText();
            int newScore = Integer.parseInt(oldScore)+i;
            scoreB.setText(""+newScore);
        }
}
