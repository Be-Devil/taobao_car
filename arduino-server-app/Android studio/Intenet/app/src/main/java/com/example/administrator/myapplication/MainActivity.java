package com.example.administrator.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.single_layout.myapplication.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Intent intent;
    public static String massage = "";
    public static boolean send_massage = false;
    public static String Url = "";
    public static String apikey = "";
    WifiManager wifiManager;
    private AutoCompleteTextView auUrl;
    private AutoCompleteTextView auapikey;

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Thread1.start();
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);

        auUrl = (AutoCompleteTextView) findViewById(R.id.url);
        auapikey = (AutoCompleteTextView) findViewById(R.id.APIKey);

        String[] auUrl_s = new String[]{"http://api.heclouds.com/cmds?device_id=36652672"};
        String[] auapikey_s = new String[]{"hz5oh6mVHAUBcjLE4wyN6X=aR=o="};

        ArrayAdapter<String> SauUrl = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, auUrl_s);
        ArrayAdapter<String> Sauapikey = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, auapikey_s);

        auUrl.setAdapter(SauUrl);     // 绑定adapter
        auapikey.setAdapter(Sauapikey);     // 绑定adapter

        auUrl.setThreshold(0);
        auapikey.setThreshold(0);

        auUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });

        auapikey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }

    protected void onDestroy() {

        super.onDestroy();
    }

    /****************************************/
    /*
    Button控制
     */
    /****************************************/

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                Url = auUrl.getText().toString();
                apikey = auapikey.getText().toString();
                intent = new Intent(MainActivity.this, SmartCar.class);
                startActivity(intent);
                break;
        }
    }

    /****************************************/
    /*
    other
     */
    /****************************************/
    public static void delay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Thread Thread1 = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if( send_massage == true) {
                    send_massage = false;
                    phHandler1.sendEmptyMessage(3);
                }
            }
        }

    });

    @SuppressLint("HandlerLeak")
    public Handler phHandler1 = new Handler() {
        @SuppressLint("WrongViewCast")
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                弹幕(massage);
            }
        }

        ;
    };

    @SuppressLint("WrongConstant")
    public void 弹幕(String x) {
        Toast.makeText(MainActivity.this, x, 500).show();
    }


}
