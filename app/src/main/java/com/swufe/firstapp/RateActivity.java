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
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RateActivity extends AppCompatActivity implements Runnable {
    private static final String TAG = "Rate";
    EditText rmb;
    TextView show;
    Handler handler;

    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView)findViewById(R.id.showOut);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String update = sharedPreferences.getString("date", "");
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String today_string = sdf.format(today);
        Log.i(TAG, "onCreate: today = " + today);
        Log.i(TAG, "onCreate: today_string = "+ today_string);

        if(!today_string.equals(update)){
            //开启子线程
            Thread t = new Thread(this);
            t.start();
            Log.i(TAG, "onCreate: 更新");
        }else{
            Log.i(TAG, "onCreate: 不更新");
        }

        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);

        Log.i(TAG,"onCreate:sp dollarRate=" + dollarRate);
        Log.i(TAG,"onCreate:sp euroRate=" + euroRate);
        Log.i(TAG,"onCreate:sp wonRate=" + wonRate);



        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");

                    Log.i(TAG, "handleMessage: dollar = " + dollarRate);
                    Log.i(TAG, "handleMessage: euro = " + euroRate);
                    Log.i(TAG, "handleMessage: won = " + wonRate);

                    SharedPreferences sp = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.putString("date", today_string);
                    editor.apply();
                }
                super.handleMessage(msg);
            }
        };
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
        }else if(item.getItemId()==R.id.open_list){
            //打开列表
            Intent list = new Intent(this, RateListActivity.class);
             startActivity(list);
            //测试数据库
//            RateItem item1 = new RateItem("aaaa","123");
//            RateManager manager = new RateManager(this);
//            manager.add(item1);
//            manager.add(new RateItem("bbbb", "23.5"));
//            Log.i(TAG, "onOptionsItemSelected: 写入完毕");
//
//            //查询
//            List<RateItem> testList = manager.listAll();
//            for(RateItem i: testList){
//                Log.i(TAG, "onOptionsItemSelected: 取出数据[id = "+i.getId()+"] Name = "+i.getCurName()+"] Rate = "+i.getCurRate());
//            }
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

    @Override
    public void run() {
        Log.i(TAG,"run:run.......");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //保存获取的数据
            Bundle bundle = new Bundle();


            //获取网络数据
//            URL url = null;
//            try {
//                url = new URL("http://www.usd-cny.com/bankofchina.htm");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            try {
//                assert url != null;
//                HttpURLConnection http = (HttpURLConnection) url.openConnection();
//                InputStream in = http.getInputStream();
//
//                String html = inputStream2String(in);
//                Log.i(TAG, "run:html = " + html);
//                Document doc = Jsoup.parse(html);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            Document doc = null;
            try {
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();

                Log.i(TAG, "run = " + doc.title());
                Elements tables = doc.getElementsByTag("table");
//                int j = 1;
//                for(Element table : tables){
//                    Log.i(TAG, "run: table[" + j + "] = " + table);
//                    j++;
//                }

                Element table = tables.get(0);
                //Log.i(TAG, "run: table = " + table);
                //获取TD中的数据
                Elements tds = table.getElementsByTag("td");
                for(int j = 0; j < tds.size();j+=6){
                    Element td1 = tds.get(j);
                    Element td2 = tds.get(j+5);
                    Log.i(TAG, "run: text = " + td1.text() + "→" + td2.text());
                    String str1 = td1.text();
                    String val = td2.text();



                    if("美元".equals(str1)){
                        bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                    } else if("欧元".equals(str1)){
                        bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                    } else if("韩元".equals(str1)){
                        bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        //bundle中保存获取的汇率

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        msg.obj = bundle;
        handler.sendMessage(msg);
        }


    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for( ; ; ){
            int rsz = in.read(buffer, 0, buffer.length);
            if(rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();

    }

}
