package com.example.luxianglin.smarthomeofandroid.Layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.luxianglin.smarthomeofandroid.Adapter.ChatMessage;
import com.example.luxianglin.smarthomeofandroid.Adapter.ChattingAdapter;
import com.example.luxianglin.smarthomeofandroid.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 123 on 2016/10/31.
 */

public class ChatFragment extends Fragment {
    protected static final String TAG = "MainActivity";
    private ChattingAdapter chatHistoryAdapter;
    private List<ChatMessage> messages = new ArrayList<ChatMessage>();

    private ListView chatHistoryLv;
    private Button sendBtn;
    private EditText textEditor;
    private ImageView sendImageIv;
    private ImageView captureImageIv;
    LocalBroadcastManager broadcastManager;
    IntentFilter intentFilter;
    BroadcastReceiver mReceiver;
    public static String sendmessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//更改title请求
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_chat_layout, container, false);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.chatting_title_bar);

        chatHistoryLv = (ListView) view.findViewById(R.id.chatting_history_lv);
        setAdapterForThis();

        sendBtn = (Button) view.findViewById(R.id.send_button);
        textEditor = (EditText) view.findViewById(R.id.text_editor);
        sendBtn.setOnClickListener(l);
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    // 设置adapter
    private void setAdapterForThis() {
        receiveMSG();
        chatHistoryAdapter = new ChattingAdapter(getActivity(), messages);
        chatHistoryLv.setAdapter(chatHistoryAdapter);
    }


    public void receiveMSG() {
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ChatReceive");//建议把它写一个公共的变量，这里方便阅读就不写了。
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receivedata = intent.getStringExtra("receivedata");
                try {
                    String codereceive=new String(receivedata.getBytes(),"UTF8");
                    String[] datas = codereceive.split("@", 2);
                    messages.add(new ChatMessage(datas[0], getDate(), datas[1], true));
                    chatHistoryAdapter.notifyDataSetChanged();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 按键时间监听
     */
    private View.OnClickListener l = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v.getId() == sendBtn.getId()) {
                String str = textEditor.getText().toString();//获取当前输入内容
                sendmessage = str;
                String sendStr;
                if (str != null
                        && (sendStr = str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "")
                        .replaceAll("\f", "")) != "") {
                    sendMessage(sendStr);
                    Intent intent = new Intent("android.intent.action.ChatSend");
                    intent.putExtra("SendMSG", sendStr.toString().trim());
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
                textEditor.setText("");
            }
        }

        // 模拟发送消息
        private void sendMessage(String sendStr) {
            try {
                String codestr = new String(sendStr.getBytes(), "UTF8");
                messages.add(new ChatMessage("", getDate(), codestr, false));
                chatHistoryAdapter.notifyDataSetChanged();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        //add int SD card
        //listen programe
    };


    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

}