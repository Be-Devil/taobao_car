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

import java.io.IOException;
import java.lang.reflect.Method;

import android.widget.Button;
import android.widget.Toast;

import com.example.single_layout.myapplication.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Intent intent;

    public static String massage = "";
    public static boolean send_massage = false;

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);

        Thread1.start();
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
                intent = new Intent(MainActivity.this, SmartCar.class);
                startActivity(intent);
                break;
        }
    }

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
    };

    @SuppressLint("WrongConstant")
    public void 弹幕(String x) {
        Toast.makeText(MainActivity.this, x, 500).show();
    }


}
