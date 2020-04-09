package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Rate2Activity extends AppCompatActivity {
    EditText dollar;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate2);

        dollar = (EditText)findViewById(R.id.dollar);
        show = (TextView)findViewById(R.id.showOut_2);
    }

    @SuppressLint("DefaultLocale")
    public void onClick(View btn) {
        //获取用户输入内容
        String str = dollar.getText().toString();

        float r = 0;
        //判断用户输入是否为空
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            //提示用户输入
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }

        float val = r * 6.7f;
        show.setText(String.format("%.2f",val));

    }
    }
