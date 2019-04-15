package com.example.luxianglin.smarthomeofandroid.Layout;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luxianglin.smarthomeofandroid.DataVisualization.ControlStatics;
import com.example.luxianglin.smarthomeofandroid.DataVisualization.EnvRecommend;
import com.example.luxianglin.smarthomeofandroid.DataVisualization.EnvironmentStatics;
import com.example.luxianglin.smarthomeofandroid.R;

public class DataVisualFragment extends Fragment {
    private CardView envbtn, rembtn, conbtn;
    private ImageView envImage, remImage, conImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_datavisual_layout, container, false);
        envbtn = (CardView) view.findViewById(R.id.cv_main_env);
        rembtn = (CardView) view.findViewById(R.id.cv_main_rem);
        conbtn = (CardView) view.findViewById(R.id.cv_main_con);
        envImage = (ImageView) view.findViewById(R.id.iv_main_env);
        remImage = (ImageView) view.findViewById(R.id.iv_main_rem);
        conImage = (ImageView) view.findViewById(R.id.iv_main_con);
        envbtn.setOnClickListener(OnClickListener);
        rembtn.setOnClickListener(OnClickListener);
        conbtn.setOnClickListener(OnClickListener);
        Glide.with(this).load(R.mipmap.heng_3).into(envImage);
        Glide.with(this).load(R.mipmap.heng_1).into(remImage);
        Glide.with(this).load(R.mipmap.heng_4).into(conImage);
        return view;
    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cv_main_env:
                    Intent t1 = new Intent(getContext(), EnvironmentStatics.class);
                    startActivity(t1, ActivityOptions.makeSceneTransitionAnimation(getActivity(), envbtn, "env").toBundle());
                    break;
                case R.id.cv_main_rem:
                    Intent t2 = new Intent(getContext(), EnvRecommend.class);
                    startActivity(t2, ActivityOptions.makeSceneTransitionAnimation(getActivity(), rembtn, "rem").toBundle());
                    break;
                case R.id.cv_main_con:
                    Intent t3 = new Intent(getContext(), ControlStatics.class);
                    startActivity(t3, ActivityOptions.makeSceneTransitionAnimation(getActivity(), conbtn, "con").toBundle());
                    break;
                default:
                    Toast.makeText(getActivity(), "点击无效，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
