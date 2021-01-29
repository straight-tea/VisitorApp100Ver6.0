package com.example.visitorapp100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String htmlData = "default";
    String debugTxt = "debug";
    int warningCount = -1;
    int cautionCount = -1;
    Boolean infoWarning = true;
    // true = warning, false = caution

    ListView listView;
    List<TrafficInfo> trafficList = new ArrayList<TrafficInfo>();
    static InfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView txt1 = findViewById(R.id.text01);
        txt1.setMovementMethod(new ScrollingMovementMethod());
        httpGet(txt1);
        txt1.setVisibility(View.INVISIBLE);

        TextView txt2 = findViewById(R.id.textCaution);
        TextView txt3 = findViewById(R.id.textWarning);



        //検索ボタンの処理
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txt1.setText(htmlData);
               checkWarningCount();
               checkCautionCount();
                txt2.setText("  Caution:  "+String.valueOf(cautionCount));
                txt3.setText("  Warning:  "+String.valueOf(warningCount));
                if(infoWarning){
                    infoWarning = false;
                }else{
                    infoWarning = true;
                }
            }
        });

        //spinner

        Spinner spinner1 = findViewById(R.id.spinnerFrom);
        Spinner spinner2 = findViewById(R.id.spinnerTo);

        // ARCore button
        Button arButton = findViewById(R.id.cameraButton);
        arButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //別アクティビティの起動
                //Intent intent = new Intent(MainActivity.this, AugmentedImageActivity.class);
                //startActivity(intent);

                switch(v.getId()){
                    case R.id.cameraButton:
                        addItem();
                        break;
                }
            }
        });

        //情報表示リスト

        findViews();
        setListeners();
        setAdapters();

    }

    //メニューの処理
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.helpButton:
                // ボタンをタップした際の処理を記述
                break;

            case R.id.NotificationButton:
                // ボタンをタップした際の処理を記述
                Intent intent3 = new Intent(MainActivity.this,NotificationActivity.class);
                startActivity(intent3);
                break;


        }
        return true;
    }

    public void checkWarningCount(){
        String temp1 = htmlData;
        String[] chkWarning = temp1.split("row mt-4 state-3",0);
        List<String> warningList = Arrays.asList(chkWarning);
        warningCount = warningList.size();
        //文字列分割による検出のため検出数は配列の長さ-1
        warningCount = warningCount -1;
    }

    public void checkCautionCount(){
        String temp2 = htmlData;
        String[] chkCaution = temp2.split("row mt-4 state-2",0);
        List<String> cautionList = Arrays.asList(chkCaution);
        cautionCount = cautionList.size();
        cautionCount = cautionCount-1;
    }

    public void getWarningInfo(int count){
        String temp3 = htmlData;
        String[] getWarning = temp3.split("row mt-4 state-3",0);

        String[] divideInfo1 = temp3.split("<div class=\"line-state-container\">",2);
        String[] divideInfo2 = divideInfo1[1].split("class=\"line-state state-2\">",2);

        String[] divideInfo3 = divideInfo2[1].split("<div class=\"row mt-4 state-2\">",2);
        String[] divideInfoLine = divideInfo3[0].split("</i>",2);
        String[] divideInfoLine2 = divideInfoLine[1].split("</h2>",2);

        String[] divideInfo4 = divideInfo3[1].split("<div class=\"col-6 flex-middle\">",2);
        String[] divideInfoStatus = divideInfo4[0].split("<p>",2);
        String[] divideInfoStatus2 = divideInfoStatus[1].split("</p>",2);

        String[] divideInfo5 = divideInfo4[1].split("</dd>",2);
        String[] divideInfoSection = divideInfo5[0].split("<dd>",2);
        String[] divideInfoSection2 = divideInfoSection[1].split("&nbsp;",2);

        String[] divideInfo6 = divideInfo5[1].split("</dd>",2);
        String[] divideInfoCause = divideInfo6[0].split("<dd>",2);

    }

    public void httpGet(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // JRWest Chugoku Area HP
                    URL url = new URL("https://global.trafficinfo.westjr.co.jp/en/chugoku/");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    String str = InputStreamToString(con.getInputStream());
                    Log.d("HTTP", str);
                    htmlData = str;


                } catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();

    }

    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    protected void findViews(){
        listView = findViewById(R.id.infoList);
    }

    protected void setListeners(){

    }


    protected void setAdapters(){
    /*adapter = new ArrayAdapter<Book>(
      this,
      android.R.layout.simple_list_item_1,
      dataList);*/
        adapter = new InfoAdapter();
        listView.setAdapter(adapter);
    }

    protected void addItem(){
        trafficList.add(
                new TrafficInfo(
                        "LineName",
                        "Section", "status","cause"));
        adapter.notifyDataSetChanged();

    }

    public class InfoAdapter  extends BaseAdapter {

        @Override
        public int getCount() {
            return trafficList.size();
        }

        @Override
        public Object getItem(int position) {
            return trafficList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            TextView textView1;
            TextView textView2;
            TextView textView3;
            View v = convertView;

            if(v==null&&infoWarning){
                LayoutInflater inflater =
                        (LayoutInflater)
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.infocard_warning, null);
            }else if(v==null&&(!infoWarning)){
                LayoutInflater inflater =
                        (LayoutInflater)
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.infocard_caution, null);
            }

            TrafficInfo info = (TrafficInfo) getItem(position);
            if(info != null){
                textView1 = (TextView) v.findViewById(R.id.textView1);
                textView2 = (TextView) v.findViewById(R.id.textView2);
                textView3 = (TextView) v.findViewById(R.id.textView3);

                textView1.setText(info.lineName);
                textView2.setText(info.section);
                textView3.setText(info.cause);

            }
            return v;
        }


    }

}

