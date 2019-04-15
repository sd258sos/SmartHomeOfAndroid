package com.example.luxianglin.smarthomeofandroid.Socket;

import com.example.luxianglin.smarthomeofandroid.Layout.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by luxianglin on 2016/11/1.
 */

public class ChatService implements Runnable {
    Socket internet = null;
    BufferedReader reader = null;
    PrintWriter writer = null;
    MainActivity mainActivity;
    public static String result = "";
    public String ipaddress = "";

    public ChatService(String ip) {
        ipaddress = ip;
    }

    public void run() {
        try {
            internet = new Socket(ipaddress, 1320);
            mainActivity.sockets = new ArrayList<Socket>();
            mainActivity.sockets.add(internet);
            println("连接服务器成功：端口1320");
            reader = new BufferedReader(new InputStreamReader(internet.getInputStream()));
            writer = new PrintWriter(internet.getOutputStream(), true);
        } catch (IOException e) {
            println("连接服务器失败：端口1320");
            println(e.toString());
            e.printStackTrace();
        }
        String msg = "";
        while (true) {
            try {
                if (reader != null) {
                    msg = reader.readLine();
                    if (msg.startsWith("ChatData:")) {
                        result = msg.substring(9);
                    }
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
