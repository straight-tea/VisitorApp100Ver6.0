package com.example.visitorapp100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.app.PendingIntent;
import com.example.visitorapp100.augmentedimage.AugmentedImageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener{

    String htmlData = "default";
    String debugTxt = "debug";
    int warningCount = -1;
    int cautionCount = -1;
    int displayWarningCount = 0;
    int displayCautionCount = 0;

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
    Boolean notify = true;

    //list格納用変数
    String listLineName;
    String listSection;
    String listStatus;
    String listCause;
    int viewCount = 0;

    String[] hitLineFrom = new String[6];
    String[] hitLineTo = new String[6];
    Boolean nonSelected = true;

    //通知関連
    private AlarmManager am;
    private PendingIntent pending;
    private int requestCode = 1;

    LocationManager locationManager;
    //山口県内の駅名と路線名を格納
    String[][] stationList = {
            //[0][N] は　山陰本線 N+1は福岡側から何駅目か
            {"Shimonoseki","Hatabu","Ayaragi","Kajikuri-Gōdaichi","Yasuoka","Fukue","Yoshimi","Umegatō","Kuroi-mura","Kawatana-onsen",
                    "Kogushi","Yutama","Ukahongō","Bagato-Futami","Takibe","Kottoi","Agawa","Nagato-Awano","Igami","Hitomaru",
                    "Nagato-Furuichi","Kiwado","Nagato-shi","Senzaki","Nagato-Misumi","Ii","Sammi","Tamae","Hagi","Higashi-Hagi",
                    "Koshigahama","Nagato-ōi","Nago","Kiyo","Utagō","Susa","Esaki"},
            //[1][[N] は　山陽本線
            {"Shimonoseki","Hatabu","Shin-Shimonoseki","Chōfu","Ozuki","Habu","Asa","Onoda","Ube","Kotō",
                    "Hon-Yura","Kagawa","Shin-Yamaguchi","Yotsutsuji","Daido","Hōfu","Tonomi","Heta","Fukugawa","Shin'nan'yō",
                    "Tokuyama","Kushigahama","Kudamatsu","Hikari","Shimata","Iwata","Tabuse","Yanai","Yanai-minato","ōbatake",
                    "Kōjiro","Yū","Fujū","Minami-Iwakuni","Iwakuni","Waki"},
            //[2][N] は　宇部線
            {"Ube","Iwahana","Inō","Ube-Shinkawa","Kotoshiba","Higashi-Shinkawa","Ubemisaki","Kusae","Tokiwa","Tokonami",
                    "Maruo","Kiwa","Ajisu","Iwakura","Suō-Sayama","Fukamizo","Kami-Kagawa","Shin-Yamaguchi"},
            //[3][N] は　小野田線
            {"Onoda","Mede","Minami-Nakagawa","Minami-Onoda","Onoda Port","Suzumeda","Nagato-Nagasawa","Tsumazaki","Ino","Ube-Shinkawa","Hamagōchi","Nagato-Motoyama"},
            //[4][N] は　山口線 駅名不足
            {"Shin-Yamaguchi","Suō-Shimogō","Kamigō","Nihozu","ōtoshi","Yabara","Yuda-Onsen","Yamaguchi"},
            //[5][N] は　岩徳線　駅名不足
            {"Tokuyama","Iwakuni"}
    };

    String[] spinnerList = {" ","Shimonoseki","Hatabu","Ayaragi","Kajikuri-Gōdaichi","Yasuoka","Fukue","Yoshimi","Umegatō","Kuroi-mura","Kawatana-onsen",
            "Kogushi","Yutama","Ukahongō","Bagato-Futami","Takibe","Kottoi","Agawa","Nagato-Awano","Igami","Hitomaru",
            "Nagato-Furuichi","Kiwado","Nagato-shi","Senzaki","Nagato-Misumi","Ii","Sammi","Tamae","Hagi","Higashi-Hagi",
            "Koshigahama","Nagato-ōi","Nago","Kiyo","Utagō","Susa","Esaki","Shin-Shimonoseki","Chōfu","Ozuki","Habu","Asa","Onoda","Ube","Kotō",
            "Hon-Yura","Kagawa","Shin-Yamaguchi","Yotsutsuji","Daido","Hōfu","Tonomi","Heta","Fukugawa","Shin'nan'yō",
            "Tokuyama","Kushigahama","Kudamatsu","Hikari","Shimata","Iwata","Tabuse","Yanai","Yanai-minato","ōbatake",
            "Kōjiro","Yū","Fujū","Minami-Iwakuni","Iwakuni","Waki","Iwahana","Inō","Ube-Shinkawa","Kotoshiba","Higashi-Shinkawa","Ubemisaki","Kusae","Tokiwa","Tokonami",
            "Maruo","Kiwa","Ajisu","Iwakura","Suō-Sayama","Fukamizo","Kami-Kagawa","Mede","Minami-Nakagawa","Minami-Onoda","Onoda Port","Suzumeda","Nagato-Nagasawa","Tsumazaki",
            "Hamagōchi","Nagato-Motoyama","Suō-Shimogō","Kamigō","Nihozu","ōtoshi","Yabara","Yuda-Onsen","Yamaguchi"};

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

        Spinner spinner1 = findViewById(R.id.spinnerFrom);
        Spinner spinner2 = findViewById(R.id.spinnerTo);

        txtDate = findViewById(R.id.textDate);
        Arrays.sort(spinnerList);
        cautionCount = 0;
        warningCount = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 10sec
        calendar.add(Calendar.SECOND, 10);

        Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);
        intent.putExtra("RequestCode",requestCode);

        pending = PendingIntent.getBroadcast(
                getApplicationContext(),requestCode, intent, 0);

        // アラームをセットする
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (am != null) {
            am.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(),
                    "Notification ON", Toast.LENGTH_SHORT).show();

            Log.d("debug", "start");
        }


        //ボタンの処理
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        FloatingActionButton fab2 = findViewById(R.id.floatingCameraButton);
        FloatingActionButton fab3 = findViewById(R.id.floatingGPSButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //本来の挙動
                /*
                checkWarningCount();
                getWarningInfo(warningCount);

                checkCautionCount();
                getCautionInfo(cautionCount);
                */
                checkDate();

                //デモ用

                addItemDev4();
                addItemDev1();
                addItemDev2();

                //txt2.setText("  Caution:  "+String.valueOf(displayCautionCount));
                txt2.setText("  Caution:  "+ cautionCount);
                //txt3.setText("  Warning:  "+String.valueOf(displayWarningCount));
                txt3.setText("  Warning:  "+ warningCount);

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
                //GPS位置情報取得
                locationStart();
                spinner1.setSelection(41);



                /*
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        MainActivity.this,
                        "MyChannel_Id");

                // 通知のアイコン
                builder.setSmallIcon(android.R.drawable.ic_dialog_info);


                // 通知のタイトル
                builder.setContentTitle("通知タイトル");

                // 通知の内容
                builder.setContentText("通知の内容");


                // 通知をタップした際にアクティビティを起動する
                /*
                // --- ここを削除すると通知の表示のみとなる
                Intent intent = new Intent(MainActivity.this,NotificationActivity.class);
                intent.putExtra("DATA","通知から起動されました。");

                PendingIntent pen = PendingIntent.getActivity(MainActivity.this,
                        0, // 0は識別子。何でも良い
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // オブジェクトを再生成
                builder.setContentIntent(pen);
                builder.setAutoCancel(true);
                // --- ここを削除すると通知の表示のみとなる

                 */
                /*
                // 通知の作成
                Notification notification = builder.build();

                // 通知サービスで通知を実行する
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, notification); // 0は識別子。何でも良い
                Log.d("Notify", "処理してる");

                */


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

        //目的地　出発地spinner



        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,spinnerList );
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,spinnerList);

        spinner1.setAdapter(fromAdapter);
        spinner2.setAdapter(toAdapter);

        // リスナーを登録
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            //駅名選択で路線を特定
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String chooseFromStation = (String)spinner.getSelectedItem();
                nonSelected = false;

                for(int i=0;i<6;i++){
                    hitLineFrom[i] = checkLineName(i,chooseFromStation);
                }

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //
                nonSelected = true;
            }
        });

        // リスナーを登録
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            //駅名選択で路線を特定
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String chooseToStation = (String)spinner.getSelectedItem();

                for(int i=0;i<6;i++){
                    hitLineTo[i] = checkLineName(i,chooseToStation);
                }

            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                //アイテム未選択フラグ
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

            case R.id.appendixButton:
                // ボタンをタップした際の処理を記述
                break;

            case R.id.NotificationButton:
                // ボタンをタップした際の処理を記述
                //Intent intent3 = new Intent(MainActivity.this,NotificationActivity.class);
                //startActivityForResult(intent3,RESULT_SUBACTIVITY);
                if(notify){
                    notify = false;
                    Toast toast = Toast.makeText(this, "Notification OFF", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    notify = true;
                    Toast toast = Toast.makeText(this, "Notification ON", Toast.LENGTH_SHORT);
                    toast.show();

                }


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
        //txt1.setText(chkCaution[1]);
    }

    //情報取得日時表示
    public void checkDate(){
        String temp4 = htmlData;
        String[] tempDate = temp4.split("Current as of:",0);
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
        int wCount = 0;
        infoWarning = true;

        String temp3 = htmlData;
        String[] getWarning = temp3.split("<div class=\"line-state state-3\">",0);

        while (wCount<count){
            String[] tempData1 = getWarning[1].split("<div class=\"row mt-4 state-3\">",0);
            String[] tempData2 = tempData1[wCount].split("</i>",0);
            List<String> tempList = Arrays.asList(tempData2);
            int tempListCount = tempList.size();
            String[] tempData3 = tempData2[tempListCount-1].split("</h2>");
            listLineName = tempData3[0].trim();

            String[] tempData4 = tempData1[wCount+1].split("Section",0);
            String[] tempData5 = tempData4[1].split("<dd>",0);
            String[] tempData6 = tempData5[1].split("&nbsp",0);


            listSection = tempData6[0].trim();

            String[] tempData7 = tempData5[2].split("</dd>",0);


            listCause = tempData7[0];

            listStatus ="Warning";
            //原因の書き換えはここ
            if(listCause.equals("")){
                listCause = "Others";
            }
            addItem();
            wCount++;
        }



    }

    public void getCautionInfo(int count){
        int cCount = 0;
        infoWarning = false;

        String temp3 = htmlData;
        String[] getCaution = temp3.split("<div class=\"line-state state-2\">",0);
        //String[] getCaution = temp3.split("<div class=\"col-12 status\">",0);
        //String[] getCaution = temp3.split("<div class=\"row mt-4 state-2\">",0);
        //HP上の黄色検出可能
        //txt1.setText(String.valueOf(getCaution.length));




        while (cCount<count){
            String[] tempCData1 = getCaution[1].split("<div class=\"col-12 status\">",0);
            String[] tempCData2 = tempCData1[cCount].split("</i>",0);

            List<String> tempCList = Arrays.asList(tempCData2);
            int tempCListCount = tempCList.size();
            String[] tempCData3 = tempCData2[tempCListCount-1].split("</h2>");
            listLineName = tempCData3[0].trim();

            txt1.setText(String.valueOf(getCaution.length));
            /*
            String[] tempCData4 = tempCData1[cCount+1].split("Section",0);
            String[] tempCData5 = tempCData4[1].split("<dd>",0);
            String[] tempCData6 = tempCData5[1].split("&nbsp",0);
            listSection = tempCData6[0].trim();
            String[] tempCData7 = tempCData5[2].split("</dd>",0);
            listCause = tempCData7[0];

             */
            listStatus = "Caution";
            //原因の書き換えはここ
            if(listCause.equals("")){
                listCause = "Others";
            }
            addItem();
            cCount++;
        }





    }

    public String checkLineName(int lineNumber,String name){
        String answer ="";
        for(int i=0;i<stationList[lineNumber].length;i++){
            if(stationList[lineNumber][i].equals(name)){
                switch (lineNumber) {
                    case 0:
                        answer = "San-in Line";
                        break;
                    case 1:
                        answer = "San-yō Line";
                        break;
                    case 2:
                        answer = "Ube Line";
                        break;
                    case 3:
                        answer = "Onoda Line";
                        break;
                    case 4:
                        answer = "Yamaguchi Line";
                        break;
                    case 5:
                        answer = "Gantoku Line";
                        break;
                    default:
                        break;
                }
            }
        }

        return answer;
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

        //検索対象路線のデータのみ表示
        int ic=0;
        if(nonSelected){
            trafficList.add(
                    new TrafficInfo(
                            listLineName,
                            listSection, listStatus,listCause));
            adapter.notifyDataSetChanged();
        }else{
            while(ic<6){
                if(hitLineFrom[ic].equals(listLineName)||hitLineTo[ic].equals(listLineName)){
                    if(listStatus.equals("Caution")){
                        displayCautionCount++;
                    }else{
                        displayWarningCount++;
                    }
                    trafficList.add(
                            new TrafficInfo(
                                    listLineName,
                                    listSection, listStatus,listCause));
                    adapter.notifyDataSetChanged();
                }

                ic++;
            }

        }



    }

//Feb 1st 6:55 運行状況よりでもデータ作成

    protected void addItemDev1(){
        infoWarning =false;
        boolean tempflag = true;

        int iv=0;
       while (iv<6){
           if(hitLineTo[iv].equals("San-yō Line")||hitLineFrom[iv].equals("San-yō Line")){
               infoWarning =false;
               trafficList.add(
                       new TrafficInfo(
                               "San-yō Line","Between Yanai and Kudamatsu","Caution","Maintenance Work"));
               cautionCount++;
               tempflag = false;
               adapter.notifyDataSetChanged();
           }


           iv++;
       }

       if(tempflag){
           infoWarning =false;
           trafficList.add(
                   new TrafficInfo(
                           "San-yō Line","Between Yanai and Kudamatsu","Caution","Maintenance Work"));
           cautionCount++;
           adapter.notifyDataSetChanged();
       }



    }

    protected void addItemDev2(){
        infoWarning =false;
        boolean tempflag2 = true;
        int iv=0;
        while (iv<6){
            if(hitLineTo[iv].equals("San-in Line")||hitLineFrom[iv].equals("San-in Line")){
                infoWarning =false;
                trafficList.add(
                        new TrafficInfo(
                                "San-in Line","Between Hamasaka and Tottori","Caution","Others"));
                cautionCount++;
                tempflag2 = false;
                adapter.notifyDataSetChanged();
            }
            iv++;
        }
        if(tempflag2){
            infoWarning =false;
            trafficList.add(
                    new TrafficInfo(
                            "San-in Line","Between Hamasaka and Tottori","Caution","Others"));
            cautionCount++;
            adapter.notifyDataSetChanged();

        }


    }

    protected void addItemDev3(){
        infoWarning =false;
        trafficList.add(
                new TrafficInfo(
                        "Imbi Line","Between Tottori and Chizu","Caution","Collision with Animal"));
        adapter.notifyDataSetChanged();
    }

    protected void addItemDev4(){

        boolean tempflag4 = true;
        int iv=0;
        while (iv<6){
            if(hitLineTo[iv].equals("Kitsuki Line")||hitLineFrom[iv].equals("Kitsuki Line")){
                infoWarning =true;
                trafficList.add(
                        new TrafficInfo(
                                "Kitsuki Line","Between Izumo-Yokota and Bingo-Ochiai","Warning","Heavy Snow"));
                warningCount++;
                tempflag4 = false;
                adapter.notifyDataSetChanged();
            }
            iv++;
        }

        if (tempflag4){
            infoWarning =true;
            trafficList.add(
                    new TrafficInfo(
                            "Kitsuki Line","Between Izumo-Yokota and Bingo-Ochiai","Warning","Heavy Snow"));
            warningCount++;
            adapter.notifyDataSetChanged();
        }

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

            if(infoWarning) {
                if(v==null){
                    LayoutInflater inflater =
                            (LayoutInflater)
                                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.infocard_warning, null);
                }
            }else{
                if(v==null){
                    LayoutInflater inflater =
                            (LayoutInflater)
                                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.infocard_caution, null);
                }
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
            viewCount++;
            if(viewCount>=warningCount+cautionCount){
                viewCount = 0;
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

    //通知に関する機能
    public void createNotify(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 10sec
        calendar.add(Calendar.SECOND, 10);

        Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);
        intent.putExtra("RequestCode",requestCode);

        pending = PendingIntent.getBroadcast(
                getApplicationContext(),requestCode, intent, 0);

        // アラームをセットする
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (am != null) {
            am.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(),
                    "alarm start", Toast.LENGTH_SHORT).show();

            Log.d("debug", "start");
        }

    }

    private void normalNotification() {


    }




}


