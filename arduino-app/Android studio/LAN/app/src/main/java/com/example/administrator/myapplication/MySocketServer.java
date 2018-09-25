package com.example.administrator.myapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySocketServer  extends MainActivity  {

    public boolean isEnable = false;
    private final WebConfig webConfig;//配置信息类
    private final ExecutorService threadPool;//线程池
    public ServerSocket socket;
    Socket remotePeer;
    public static  DataInputStream bInputStream;
    public static  DataOutputStream bOutputStream;
    public boolean flag = false;

    public MySocketServer(WebConfig webConfig) {
        this.webConfig = webConfig;
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * 开启server
     */
    public void startServerAsync() {
        isEnable = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doProcSync();
            }
        }).start();
    }

    /**
     * 关闭server
     */
    public void stopServerAsync() throws IOException {
        if (!isEnable) {
            return;
        }
        flag = false;
        isEnable = true;
        socket.close();
        socket = null;
    }

    public void doProcSync() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(webConfig.getPort());
            System.out.print(webConfig.toString());
            socket = new ServerSocket();
            socket.bind(socketAddress);
            System.out.print(socketAddress.getAddress());
            while(true) {
                remotePeer = socket.accept();
                bInputStream = new DataInputStream(remotePeer.getInputStream());      ///输入流
                bOutputStream = new DataOutputStream(remotePeer.getOutputStream());   ///输出流
                bOutputStream.write("连接成功".getBytes(), 0, "连接成功".getBytes().length);    //100          ///发送数组
                flag = true;
                MainActivity.massage = "连接成功";
                MainActivity.send_massage = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onDestory() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();                                                        ///关闭套接字
                bInputStream.close();                                                  ///关闭输入流
                bOutputStream.close();                                                 ///关闭输出流
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send_b(final byte[] s){
            new Thread(){
                public void run(){
                    try {
                        if (remotePeer != null && !remotePeer.isClosed()) {
                            if (flag == true) {
                                bOutputStream.write(s, 0, s.length);
                                bOutputStream.flush();                                    ///清空缓冲区数据
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }.start();
    }

}