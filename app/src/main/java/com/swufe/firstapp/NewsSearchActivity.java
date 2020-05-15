package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsSearchActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {

    String TAG = "NewsSearch";
    Handler handler;
    EditText keywords;
    List<HashMap<String, String>> list2 = new ArrayList<>();
    private SimpleAdapter listItemAdapter;


    @SuppressLint("HandlerLeak")
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_search);

        SharedPreferences sharedPreferences = getSharedPreferences("mynews", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String update = sharedPreferences.getString("yyyy-MM-dd", "");
        Log.i(TAG, "onCreate: update = " + update);
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String today_string = sdf.format(today);
        Log.i(TAG, "onCreate: today = " + today);
        Log.i(TAG, "onCreate: today_string = "+ today_string);


        keywords = (EditText)findViewById(R.id.inpKeyword);
        keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //关键词匹配
                TextView keyword = (TextView)NewsSearchActivity.this.findViewById(R.id.inpKeyword);
                if(s.length() > 0){
                    String key = keyword.getText().toString();
                    List<HashMap<String, String>> compare = new ArrayList<HashMap<String, String>>();
                    int flag = 0;
                    for(int i = 0; i < list2.size(); i++){
                        String title = list2.get(i).get("title");

                        assert title != null;
                        if(title.contains(key)){
                            flag = 1;
                            HashMap<String, String> comparing  = new HashMap<String, String>();
                            comparing.put("title", list2.get(i).get("title"));
                            comparing.put("href", list2.get(i).get("href"));
                            compare.add(comparing);

                        }

                    }
                    if(flag == 0){
                        Toast.makeText(NewsSearchActivity.this,"找不到公告",Toast.LENGTH_SHORT).show();
                    }
//                    Log.i(TAG, "afterTextChanged: compare = " + compare);
                    listItemAdapter = new SimpleAdapter(NewsSearchActivity.this, compare,//listItems 数据源
                            R.layout.news_item,// ListItem 的 XML 布局实现
                            new String[] {"title", "href"},
                            new int[] {R.id.title, R.id.href}
                    );
                    setListAdapter(listItemAdapter);
                }else{
                    listItemAdapter = new SimpleAdapter(NewsSearchActivity.this, list2,//listItems 数据源
                            R.layout.news_item,// ListItem 的 XML 布局实现
                            new String[] {"title", "href"},
                            new int[] {R.id.title, R.id.href}
                    );
                    setListAdapter(listItemAdapter);
                }
            }
        });

        this.setListAdapter(listItemAdapter);


        try {
            if(update.equals("")||getDayLength(today_string, update) > 7){
                Thread t = new Thread(this);
                t.start();
                Log.i(TAG, "onCreate: 更新");
            }else{
                int list2_size = sharedPreferences.getInt("list2_size", Integer.parseInt("0"));
                for(int i = 0; i < list2_size; i++){
                    String title = sharedPreferences.getString("title" + i, "");
                    String href = sharedPreferences.getString("href" + i, "");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("title", title);
                    map.put("href", href);
                    list2.add(map);
                }
                listItemAdapter = new SimpleAdapter(NewsSearchActivity.this, list2,//listItems 数据源
                        R.layout.news_item,// ListItem 的 XML 布局实现
                        new String[] {"title", "href"},
                        new int[] {R.id.title, R.id.href}
                );
                setListAdapter(listItemAdapter);
                Log.i(TAG, "onCreate: 不更新");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 5){
                    list2 = (List<HashMap<String, String>>)msg.obj;
//                    Log.i(TAG, "handleMessage: list2 = " + list2);

                    SharedPreferences sp = getSharedPreferences("mynews",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("yyyy-MM-dd", today_string);
                    for(int i = 0; i < list2.size(); i++){

                        editor.putString("title"+i, list2.get(i).get("title"));
//                        Log.i(TAG, "handleMessage: title" + i + "=" + list2.get(i).get("title"));
                        editor.putString("href"+i, list2.get(i).get("href"));
                    }
                    editor.putInt("list2_size", list2.size());
                    editor.apply();

                    listItemAdapter = new SimpleAdapter(NewsSearchActivity.this, list2,//listItems 数据源
                            R.layout.news_item,// ListItem 的 XML 布局实现
                            new String[] {"title", "href"},
                            new int[] {R.id.title, R.id.href}
                    );
                    setListAdapter(listItemAdapter);
                    Log.i(TAG, "handleMessage: ");
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
    }
    //获取数据
    public void run() {
        //保存获取的数据
        List<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG, "run = " + doc.title());
            //获取ul中的数据
            Elements uls = doc.getElementsByTag("ul");
            Element ul = uls.get(17);
            Elements as = ul.select("a");

            for(Element a:as){
                String title = a.attr("title");
                String href = a.attr("href");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", title);
                map.put("href", "https://it.swufe.edu.cn/" + href.substring(3));
                retList.add(map);

            }

            for(int i = 56; i >= 1; i--){
                doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg/"+i+".htm").get();

                //获取ul中的数据
                uls = doc.getElementsByTag("ul");
                ul = uls.get(17);
                as = ul.select("a");

                for(Element a:as){
                    String title = a.attr("title");
                    String href = a.attr("href");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("title", title);
                    map.put("href", "https://it.swufe.edu.cn/" + href.substring(6));
                    retList.add(map);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        for(int i = 0; i < retList.size(); i++){
//            Log.i(TAG, "run: retList" + retList.get(i));
//        }
        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        msg.obj = retList;
        handler.sendMessage(msg);
    }


    public static int getDayLength(String start_date,String end_date) throws Exception{

        Date fromDate = getStrToDate(start_date,"yyyy-MM-dd"); //开始日期
        Date toDate = getStrToDate(end_date,"yyyy-MM-dd"); //结束日期
        long from = fromDate.getTime();
        long to = toDate.getTime();

        //一天等于多少毫秒：24*3600*1000

        int day = (int)((to-from)/(24*60*60*1000));
        return day;
        }



        public static Date getStrToDate(String date,String fomtter) throws Exception{
            DateFormat df = new SimpleDateFormat(fomtter);
            return df.parse(date);
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> map = (HashMap<String, String>)getListView().getItemAtPosition(position);
            String href = map.get("href");
            Intent intent= new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(href);
            intent.setData(content_url);
            startActivity(intent);
        }
}
