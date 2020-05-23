package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    private static final String TAG = "RateList";
    String data[] = {"one", "two", "three"};
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);

        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");
        Log.i("List", "lastRateDateStr = " + logDate);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 7){
                    List<String> list2 = (List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this, android.R.layout.simple_list_item_1, list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        //获取数据，放入list代入主线程
        List<String> retList = new ArrayList<String>();
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run", "curDateStr = " + curDateStr + "logDate = " + logDate);

        if(curDateStr.equals(logDate)){
            //相等则不从网络获取数据
            Log.i("run", "日期相等，从数据库获取数据");
            RateManager manager = new RateManager(this);
            for(RateItem item: manager.listAll()){
                retList.add(item.getCurName() + "→" + item.getCurRate());
            }
        }else{
            //不相等，从网络获取数据
            Log.i("run", "日期不等，从网络中获取在线数据");
            Document doc = null;
            try {
                Thread.sleep(3000);
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();

                Log.i(TAG, "run = " + doc.title());
                Elements tables = doc.getElementsByTag("table");

                Element table = tables.get(0);
                //Log.i(TAG, "run: table = " + table);
                //获取TD中的数据
                Elements tds = table.getElementsByTag("td");
                List<RateItem> rateList = new ArrayList<RateItem>();
                for(int j = 0; j < tds.size();j+=6){
                    Element td1 = tds.get(j);
                    Element td2 = tds.get(j+5);

                    String str1 = td1.text();
                    String val = td2.text();

                    Log.i(TAG, "run: " + str1 + "→" + val);
                    retList.add(str1 + "→" + val);
                    rateList.add(new RateItem(str1, val));
                }

                //写入数据库
                RateManager manager = new RateManager(this);
                manager.deleteAll();
                manager.addAll(rateList);
                //记录更新日期
                SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(DATE_SP_KEY, curDateStr);
                edit.commit();
                Log.i("run", "更新日期结束: " + curDateStr);


            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);
    }
}
