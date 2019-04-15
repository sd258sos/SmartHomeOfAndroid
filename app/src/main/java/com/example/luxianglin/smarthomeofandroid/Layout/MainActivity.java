package com.example.luxianglin.smarthomeofandroid.Layout;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.example.luxianglin.smarthomeofandroid.Gson.Env.EnvAnalysisGson;
import com.example.luxianglin.smarthomeofandroid.R;
import com.example.luxianglin.smarthomeofandroid.Socket.ChatService;
import com.example.luxianglin.smarthomeofandroid.Socket.NetService;
import com.example.luxianglin.smarthomeofandroid.Util.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luxianglin on 2016/10/30.
 */

public class MainActivity extends AppCompatActivity {
    private MyHorizontalScrollView title;
    private ViewPager viewPager;

    NetService netService;
    ChatService chatService;

    public static ArrayList<Socket> sockets = null;
    LocalBroadcastManager broadcastManager1, broadcastManager2;
    BroadcastReceiver mItemViewListClickReceiver, ChatReceiver;
    EnvAnalysisGson envAnalysisGson = null;
    String username;
    public static String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        title = (MyHorizontalScrollView) findViewById(R.id.title);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(10);//设置viewpager初始化加载Fragment的页面数
        title.setViewPager(viewPager);


        Intent intent = getIntent();
        final Bundle data = intent.getExtras();
        username = data.getString("user");
        ip = data.getString("IP");
        netService = new NetService(ip);
        chatService = new ChatService(ip);
        new Thread(netService).start();
        new Thread(chatService).start();


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                envAnalysisGson = new EnvAnalysisGson(NetService.result);
                Intent intent = new Intent("android.intent.action.Environment");
                intent.putExtra("data", envAnalysisGson.returnResult());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        };

        Timer timer = new Timer(true);
        timer.schedule(task, 1000, 2000);


        Thread ChatReceiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ChatService.result != null) {
                        if (!ChatService.result.equals("")) {
                            Intent intent = new Intent("android.intent.action.ChatReceive");
                            intent.putExtra("receivedata", ChatService.result);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            ChatService.result = "";
                        }
                    }
                }
            }
        });
        ChatReceiveThread.start();
        broadcastManager1 = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.Control");

        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String device = intent.getStringExtra("DeviceType");

                JSONObject sendData = new JSONObject();
                switch (device) {
                    case "Lamp":
                        boolean b1 = intent.getBooleanExtra("lamp", false);
                        SensorControl(sendData, "Lamp", b1);
                        netService.sendMsg(sendData.toString());
                        break;
                    case "Warn":
                        boolean b2 = intent.getBooleanExtra("warn", false);
                        SensorControl(sendData, "WarningLight", b2);
                        netService.sendMsg(sendData.toString());
                        break;
                    case "Fan":
                        boolean b3 = intent.getBooleanExtra("fan", false);
                        SensorControl(sendData, "Fan", b3);
                        netService.sendMsg(sendData.toString());
                        break;
                    case "Curtain":
                        int command = intent.getIntExtra("curtain", 0);
                        CurtainControl(sendData, command);
                        netService.sendMsg(sendData.toString());
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "无效控制操作", Toast.LENGTH_SHORT).show();
                }


            }
        };
        broadcastManager1.registerReceiver(mItemViewListClickReceiver, intentFilter);

        broadcastManager2 = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter senddata = new IntentFilter();
        senddata.addAction("android.intent.action.ChatSend");
        ChatReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("SendMSG");
                String user = username;
                if (message != null && (!message.equals(""))) {
                    chatService.sendMsg("SendMSG:" + user + "@" + message);
                    System.out.println("发送成功");
                }
            }
        };
        broadcastManager2.registerReceiver(ChatReceiver, senddata);
    }


    public void SensorControl(JSONObject data, String type, boolean command) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            data.put("CurrentTime", df.format(new Date()).toString());
            data.put("SenSorType", type);
            data.put("Custom", "");
            data.put("User", username);
            if (command) {
                data.put("Command", "1");
            } else {
                data.put("Command", "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void CurtainControl(JSONObject data, int command) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            data.put("CurrentTime", df.format(new Date()).toString());
            data.put("SenSorType", "Curtain");
            data.put("Custom", "");
            data.put("User", username);
            switch (command) {
                case 1:
                    data.put("Command", "1");
                    break;
                case 2:
                    data.put("Command", "2");
                    break;
                default:
                    data.put("Command", "0");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<String> pages = new ArrayList<String>();

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            pages.add("环境监测");
            pages.add("家电控制");
            pages.add("网络聊天");
            pages.add("数据可视");
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages.get(position);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new EnvFragment();
                case 1:
                    return new ConFragment();
                case 2:
                    return new ChatFragment();
                case 3:
                    return new DataVisualFragment();
                default:
                    return new EnvFragment();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        netService.sendMsg("exit");
        chatService.sendMsg("@exit@");

        for (int i = 0; i < sockets.size(); i++) {
            try {
                sockets.get(i).close();
                System.out.println("已经关闭socket：" + sockets.get(i).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();

        }

        return false;

    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    netService.sendMsg("exit");
                    chatService.sendMsg("@exit@");

                    System.exit(0);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}

