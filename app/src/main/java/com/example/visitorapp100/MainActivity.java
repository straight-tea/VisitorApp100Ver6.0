package com.example.visitorapp100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visitorapp100.augmentedimage.AugmentedImageActivity;
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

public class MainActivity extends AppCompatActivity implements LocationListener{

    String htmlData = "default";
    String debugTxt = "debug";
    int warningCount = -1;
    int cautionCount = -1;
    Boolean infoWarning = true;
    // true = warning, false = caution
    static final int RESULT_SUBACTIVITY = 1000;

    ListView listView;
    List<TrafficInfo> trafficList = new ArrayList<TrafficInfo>();
    static InfoAdapter adapter;
    private TextView txt1;
    public static final String STATION_NAME = "Kotoshiba";
    String useStation = "START";

    TextView txtDate;

    //list格納用変数
    String listLineName;
    String listSection;
    String listCause;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txt1 = findViewById(R.id.text01);
        txt1.setMovementMethod(new ScrollingMovementMethod());
        httpGet(txt1);
        //デバッグ用テキスト　画面中央
        txt1.setVisibility(View.INVISIBLE);

        TextView txt2 = findViewById(R.id.textCaution);
        TextView txt3 = findViewById(R.id.textWarning);

        txtDate = findViewById(R.id.textDate);



        //ボタンの処理
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        FloatingActionButton fab2 = findViewById(R.id.floatingCameraButton);
        FloatingActionButton fab3 = findViewById(R.id.floatingGPSButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txt1.setText(htmlData);
                checkWarningCount();
                checkCautionCount();
                checkDate();

                //論文用　4項目
                addItemDev1();
                addItemDev2();
                cautionCount = 2;
                warningCount = 0;

                txt2.setText("  Caution:  "+String.valueOf(cautionCount));
                txt3.setText("  Warning:  "+String.valueOf(warningCount));

                /*

                if(infoWarning){
                    infoWarning = false;
                }else{
                    infoWarning = true;
                }

                 */
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AugmentedImageActivity.class);
                startActivity(intent);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWarningInfo(warningCount);
                //addItem();
            }
        });

        //GPS位置情報取得

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        }
        else{
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, (LocationListener) this);

        }

        //spinner

        Spinner spinner1 = findViewById(R.id.spinnerFrom);
        Spinner spinner2 = findViewById(R.id.spinnerTo);

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
                startActivityForResult(intent3,RESULT_SUBACTIVITY);
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

    //情報取得日時表示
    public void checkDate(){
        String temp4 = htmlData;
        String[] tempDate = temp4.split("<p class=\"time\">Current as of:",0);
         String[] infoDate = tempDate[1].split("<br/>",0);
        // infoDate[0] に　日付/月/年 at 時刻 が格納
        String[] separateDate = infoDate[0].split("/",0);
        Log.d("DATE", separateDate[1]);
        if("01".equals(separateDate[1]))separateDate[1]="Jan";
        if("02".equals(separateDate[1]))separateDate[1]="Feb";
        if("03".equals(separateDate[1]))separateDate[1]="Mar";
        if("04".equals(separateDate[1]))separateDate[1]="Apr";
        if("05".equals(separateDate[1]))separateDate[1]="May";
        if("06".equals(separateDate[1]))separateDate[1]="Jun";
        if("07".equals(separateDate[1]))separateDate[1]="Jul";
        if("08".equals(separateDate[1]))separateDate[1]="Aug";
        if("09".equals(separateDate[1]))separateDate[1]="Sep";
        if("10".equals(separateDate[1]))separateDate[1]="Oct";
        if("11".equals(separateDate[1]))separateDate[1]="Nov";
        if("12".equals(separateDate[1]))separateDate[1]="Dec";


        txtDate.setText(separateDate[0]+"/"+separateDate[1]+"/"+separateDate[2]);

    }

    public void getWarningInfo(int count){
        String temp3 = htmlData;
        String[] getWarning = temp3.split("row mt-4 state-3",0);

        //運休情報抽出
        String[] divideInfo1 = temp3.split("class=\"line-state state-3\">",2);
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

        listLineName = divideInfoLine2[0];
        listSection = divideInfoSection2[0];
        listCause = divideInfoCause[1];

        txt1.setText(getWarning[0]);

        addItem();

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
                        listLineName,
                        listSection, "status",listCause));
        adapter.notifyDataSetChanged();

    }

    protected void addItemDev1(){
        infoWarning =false;
        trafficList.add(
                new TrafficInfo(
                        "Ube Line","Between Ube and Shin-Yamaguchi","Operation Stopped Partially","Heavy Rain"));
        adapter.notifyDataSetChanged();

    }

    protected void addItemDev2(){
        infoWarning =false;
        trafficList.add(
                new TrafficInfo(
                        "San-yō Line","Between Shimonoseki and Shin-Yamaguchi","Operation Stopped Partially","Heavy Rain"));
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

    //ARActivity からのデータ受け取り
    protected void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);

        if(resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY &&
                null != intent) {
            String res = intent.getStringExtra(MainActivity.STATION_NAME);
            txt1.setText(res);

        }

    }

    //位置情報取得
    private void locationStart(){
        Log.d("debug","locationStart()");

        // LocationManager インスタンス生成
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, (LocationListener) this);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // 緯度の表示
        //TextView textView1 = (TextView) findViewById(R.id.text_view1);
        String str1 = "Latitude:"+location.getLatitude();
        //textView1.setText(str1);

        // 経度の表示
        //TextView textView2 = (TextView) findViewById(R.id.text_view2);
        String str2 = "Longtude:"+location.getLongitude();
        //textView2.setText(str2);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




}

