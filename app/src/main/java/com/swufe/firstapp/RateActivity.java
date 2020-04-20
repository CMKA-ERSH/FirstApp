package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {
    private static final String TAG = "Rate";
    EditText rmb;
    TextView show;

    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView)findViewById(R.id.showOut);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        Log.i(TAG,"onCreate:sp dollarRate=" + dollarRate);
        Log.i(TAG,"onCreate:sp euroRate=" + euroRate);
        Log.i(TAG,"onCreate:sp wonRate=" + wonRate);

    }

    @SuppressLint("DefaultLocale")
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
            val = r * dollarRate;
        }else if(btn.getId() == R.id.btn_euro){
            val = r * euroRate;
        }else{
            val = r * wonRate;
        }
        show.setText(String.format("%.2f",val));

    }

    public void openOne(View btn){
        //打开一个页面Activity
        Log.i("open","openOne:");
        openConfig();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openConfig();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_value_key", dollarRate);
        config.putExtra("euro_value_key", euroRate);
        config.putExtra("won_value_key", wonRate);

        Log.i(TAG, "openOne:dollarRate = " + dollarRate);
        Log.i(TAG, "openOne:euroRate = " + euroRate);
        Log.i(TAG, "openOne:wonRate = " + wonRate);

        //startActivity(config);
        startActivityForResult(config, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            assert data != null;
            Bundle bundle = data.getExtras();
            assert bundle != null;
            dollarRate = bundle.getFloat("key_dollar", 0.0f);
            euroRate = bundle.getFloat("key_euro", 0.0f);
            wonRate = bundle.getFloat("key_won", 0.0f);
            Log.i(TAG,"onActivityResult:dollarRate=" + dollarRate);
            Log.i(TAG,"onActivityResult:euroRate=" + euroRate);
            Log.i(TAG,"onActivityResult:wonRate=" + wonRate);

            //将新设置的汇率写入SP
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);

            editor.apply();
            Log.i(TAG, "onActivityResult:数据已保存到sharedPreference");


        }
    }
}
