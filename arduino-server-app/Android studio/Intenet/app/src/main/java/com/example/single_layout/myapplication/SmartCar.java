package com.example.single_layout.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.HttpUtils;
import com.example.administrator.myapplication.MainActivity;
import com.example.administrator.myapplication.R;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import static com.example.administrator.myapplication.MainActivity.Url;

public class SmartCar extends AppCompatActivity implements View.OnClickListener {

    TextView state;
    String state_s = "";
    boolean longClicked = true;
    Button test1;
    Button test2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartcar_activity);

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
                        //                   while (longClicked) {
                                switch (id) {
                                    case R.id.go:
                                        state_s = HttpUtils.post1(Url, bytetos(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x01, (byte) 0x2a}));
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                    case R.id.left:
                                        state_s = HttpUtils.post1(Url, bytetos(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x02, (byte) 0x2a}));
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                    case R.id.right:
                                        state_s = HttpUtils.post1(Url, bytetos(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x2a}));
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                    case R.id.back:
                                        state_s = HttpUtils.post1(Url, bytetos(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x2a}));
                                        phHandler.sendEmptyMessage(1);
                                        break;
                                }
                    }
                }).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    longClicked = false;
                    state_s =HttpUtils.post1(Url, bytetos(new byte[]{(byte) 0xaa, (byte) 0x03, (byte) 0x02, (byte) 0x05, (byte) 0x2a}));
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

    public String bytetos(byte[] s) {
        String str = Integer.toHexString(s[0] & 0xff);
        for (int i = 1; i < s.length; i++)
            if(s[i] > 15)
                str = str + " " + Integer.toHexString(s[i] & 0xff);
            else
                str = str + " 0" + Integer.toHexString(s[i] & 0xff);

        return str;
    }
}
