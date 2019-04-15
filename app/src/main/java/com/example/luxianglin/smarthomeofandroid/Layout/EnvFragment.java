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
import android.widget.TextView;

import com.example.luxianglin.smarthomeofandroid.R;

/**
 * Created by luxianglin on 2016/10/30.
 */

public class EnvFragment extends Fragment {
    LocalBroadcastManager broadcastManager;
    IntentFilter intentFilter;
    BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_env_layout, container, false);
        final TextView tem = (TextView) view.findViewById(R.id.Tem);
        final TextView hum = (TextView) view.findViewById(R.id.Hum);
        final TextView air = (TextView) view.findViewById(R.id.Air);
        final TextView ill = (TextView) view.findViewById(R.id.Ill);
        final TextView smo = (TextView) view.findViewById(R.id.Smo);
        final TextView ful = (TextView) view.findViewById(R.id.Ful);
        final TextView co = (TextView) view.findViewById(R.id.co2);
        final TextView pm = (TextView) view.findViewById(R.id.pm2_5);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.Environment");//建议把它写一个公共的变量，这里方便阅读就不写了。
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                String[] datas = data.split(",", -1);
                tem.setText(datas[0]+"°C");
                hum.setText(datas[1]+" %");
                ill.setText(datas[2]+"Lux");
                smo.setText(datas[3]+"ppm");
                ful.setText(datas[4]+"ppm");
                pm.setText(datas[5]+"ug/m3");
                co.setText(datas[6]+"ppm");
                air.setText(datas[7]+"pa");
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
