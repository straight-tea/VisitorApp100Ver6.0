package com.example.visitorapp100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // わかりやすいようにタイトルを変更する
        setTitle("NotificationActivity");

        Intent intent = getIntent();
        String data = intent.getStringExtra("DATA");

        TextView tv = findViewById(R.id.textView2);

        // 通知から起動された場合
        if(data != null) {
            tv.setText(data);
            // ボタンから起動された場合
        }else{
            tv.setText("ボタンから起動しました。");
        }

    }





}