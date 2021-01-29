package com.example.visitorapp100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String menu0 = "Notification";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        final ListView listView2 = findViewById(R.id.settingList);
        listView2.setOnItemClickListener(this);

        List<String> setlist = new ArrayList<>();

        setlist.add(menu0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                NotificationActivity.this,
                android.R.layout.simple_list_item_1,
                setlist
        );

        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener((this::onItemClick));

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TextView txt5 = findViewById(R.id.textView5);
        //txt5.setText(String.valueOf(position));


    }



}