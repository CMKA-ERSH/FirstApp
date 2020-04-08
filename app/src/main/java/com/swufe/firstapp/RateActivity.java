package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {
    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView)findViewById(R.id.showOut);
    }

    public void onClick(View btn){
        //获取用户输入内容
        String str = rmb.getText().toString();

        float r = 0;
        //判断用户输入是否为空
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            //提示用户输入
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }

        //判断用户需要转换成何种汇率
        float val = 0;
        if(btn.getId() == R.id.btn_dollar){
            val = r * (1/6.7f);
        }else if(btn.getId() == R.id.btn_euro){
            val = r * (1/11.0f);
        }else{
            val = r * 500;
        }
        show.setText(String.format("%.2f",val));

    }

    public void openOne(View btn){
        //打开一个页面Activity
        Log.i("open","openOne:");
        Intent hello = new Intent(this,Second2Activity.class);
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://baidu.com"));
        startActivity(web);
    }
}
