package com.example.single_layout.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.MainActivity;
import com.example.administrator.myapplication.MySocketServer;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.WebConfig;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class SmartCar extends AppCompatActivity implements View.OnClickListener {

    TextView state;
    String state_s = "";
    boolean longClicked = true;
    Button test1;
    Button test2;
    WifiManager wifiManager;
    public static MySocketServer mySocketServer;

    @SuppressLint({"WifiManagerLeak", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartcar_activity);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WebConfig webConfig = new WebConfig();
        webConfig.setPort(1811);
        webConfig.setMaxParallels(10);
        mySocketServer = new MySocketServer(webConfig);
        mySocketServer.startServerAsync();

        ImageView go = (ImageView) findViewById(R.id.go);
        go.setOnTouchListener(mImageViewTouchHandler);
        ImageView left = (ImageView) findViewById(R.id.left);
        left.setOnTouchListener(mImageViewTouchHandler);
        ImageView right = (ImageView) findViewById(R.id.right);
        right.setOnTouchListener(mImageViewTouchHandler);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnTouchListener(mImageViewTouchHandler);

        state = (TextView) findViewById(R.id.state);

        Button Return = (Button) findViewById(R.id.Return);
        Return.setOnClickListener(this);
        Button mo = (Button) findViewById(R.id.mo);
        mo.setOnClickListener(this);
        test1 = (Button) findViewById(R.id.test1);
        test1.setOnClickListener(this);
        test2 = (Button) findViewById(R.id.test2);
        test2.setOnClickListener(this);

        initVideoView();

    }

    /****************************************/
    /*
    Button控制
     */
    /****************************************/
    public ImageView.OnTouchListener mImageViewTouchHandler = new ImageView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int id = v.getId();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                longClicked = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                                switch (id) {
                                    case R.id.go:
                                        mySocketServer.send_b(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x01, (byte) 0x2a});
                                        state_s = "前进";
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                    case R.id.left:
                                        mySocketServer.send_b(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x02, (byte) 0x2a});
                                        state_s = "左转";
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                    case R.id.right:
                                        mySocketServer.send_b(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x2a});
                                        state_s = "右转";
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                    case R.id.back:
                                        mySocketServer.send_b(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x2a});
                                        state_s = "后退";
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                }
                    }
                }).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    longClicked = false;
                    mySocketServer.send_b(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x05, (byte) 0x2a});
                    state_s = "状态加载中...";
                    phHandler.sendEmptyMessage(1);

            }
            return true;
        }
    };


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Return:
                break;
            case R.id.mo:
                break;
            case R.id.test1:
                state_s = "test1按下";
                state.setText(state_s);
                break;
            case R.id.test2:
                state_s = "test2按下";
                state.setText(state_s);
                break;
        }

    }

    public void initVideoView() {
        TXCloudVideoView video_view = (TXCloudVideoView) findViewById(R.id.video_view);
        TXLivePlayer txLivePlayer;
        txLivePlayer = new TXLivePlayer(this);
        txLivePlayer.setPlayerView(video_view);
        TXLivePlayConfig txLivePlayConfig = new TXLivePlayConfig();
        txLivePlayConfig.setAutoAdjustCacheTime(true);
        txLivePlayConfig.setMinAutoAdjustCacheTime(1);
        txLivePlayConfig.setMaxAutoAdjustCacheTime(5);
        txLivePlayer.setConfig(txLivePlayConfig);
        txLivePlayer.enableHardwareDecode(true);
        ITXLivePlayListener itxLivePlayListener = new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                if (i == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
                    //开始播放去掉loading菊花
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        };
        txLivePlayer.setPlayListener(itxLivePlayListener);
        txLivePlayer.startPlay("rtmp://pili-live-rtmp-zj.realgamecloud.com/zengjjing/bangongshi", TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);

    }

    public Handler phHandler = new Handler() {
        @SuppressLint("WrongViewCast")
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                state.setText(state_s);
            }
        }
    };
}
