package com.example.luxianglin.smarthomeofandroid.Socket;

import com.example.luxianglin.smarthomeofandroid.Layout.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by luxianglin on 2016/9/25.
 */
public class NetService implements Runnable {
    Socket internet = null;
    BufferedReader reader = null;
    PrintWriter writer = null;
    MainActivity mainActivity;
    public static String result = "";
    public String ipaddress = "";

    public NetService(String ip) {
        ipaddress = ip;
    }

    public void run() {
        try {
            internet = new Socket(ipaddress, 1228);
            mainActivity.sockets = new ArrayList<Socket>();
            mainActivity.sockets.add(internet);
            println("连接服务器成功：端口1228");
            reader = new BufferedReader(new InputStreamReader(internet.getInputStream()));
            writer = new PrintWriter(internet.getOutputStream(), true);
        } catch (IOException e) {
            println("连接服务器失败：端口1228");
            println(e.toString());
            e.printStackTrace();
        }
        String msg = "";
        while (true) {
            try {
                if (reader != null) {
                    msg = reader.readLine();
                    result = msg;
                }
            } catch (IOException e) {
                println("服务器断开连接");
                break;
            }
            if (msg != null && msg.trim() != "") {
                println(">>" + msg);
            }
        }
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Socket getsocket() {
        return internet;
    }

    public void sendMsg(String msg) {
        try {
            writer.println(msg);
        } catch (Exception e) {
            println(e.toString());
        }
    }

    public void println(String s) {
        if (s != null) {
            System.out.println(s + "\n");
        }
    }
}
