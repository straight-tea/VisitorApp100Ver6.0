package com.example.visitorapp100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String htmlData = "default";
    String debugTxt = "debug";
    int warningCount = -1;
    int cautionCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView txt1 = findViewById(R.id.text01);
        txt1.setMovementMethod(new ScrollingMovementMethod());
        httpGet(txt1);

        //検索ボタンの処理
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txt1.setText(htmlData);
               checkWarningCount();
               checkCautionCount();
                txt1.setText(String.valueOf(cautionCount));
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
            }
        });


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
}