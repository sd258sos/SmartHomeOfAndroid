package com.example.luxianglin.smarthomeofandroid.Layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.luxianglin.smarthomeofandroid.R;

/**
 * Created by luxianglin on 2016/10/30.
 */

public class ConFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_con_layout, container, false);
        Switch lamp = (Switch) view.findViewById(R.id.lamp);
        Switch warn = (Switch) view.findViewById(R.id.warnLight);
        Switch fan = (Switch) view.findViewById(R.id.fan);
        Button Copen = (Button) view.findViewById(R.id.Open);
        Button Cstop = (Button) view.findViewById(R.id.Stop);
        Button Cclose = (Button) view.findViewById(R.id.Close);
        lamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent("android.intent.action.Control");
                intent.putExtra("DeviceType","Lamp");
                intent.putExtra("lamp", b);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            }
        });
        warn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent("android.intent.action.Control");
                intent.putExtra("DeviceType","Warn");
                intent.putExtra("warn", b);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            }
        });
        fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent intent = new Intent("android.intent.action.Control");
                intent.putExtra("DeviceType","Fan");
                intent.putExtra("fan", b);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });
        Copen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.Control");
                intent.putExtra("DeviceType","Curtain");
                intent.putExtra("curtain",1);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });
        Cstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.Control");
                intent.putExtra("DeviceType","Curtain");
                intent.putExtra("curtain",2);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });
        Cclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.Control");
                intent.putExtra("DeviceType","Curtain");
                intent.putExtra("curtain",0);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
