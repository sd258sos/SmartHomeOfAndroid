package com.example.luxianglin.smarthomeofandroid.Gson.Env;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by luxianglin on 2016/10/7.
 */

public class EnvAnalysisGson {
    String temp;
    String humidity;
    String illumination;
    String smoke;
    String gas;
    String pm2_5;
    String co2;
    String airpressure;
    String magneticwindow;
    String glassdetection;
    String grating;
    String statehumanInfrared;
    String statehelpbutton;

    public EnvAnalysisGson(String data) {
        Gson gson = new Gson();
        Root root = gson.fromJson(data, Root.class);
        if (root != null) {
            List<Data> da = root.getData();
            for (int i = 0; i < da.size(); i++) {
                Data currentdata = da.get(i);
                temp = currentdata.getTemp();
                humidity = currentdata.getHumidity();
                illumination = currentdata.getIllumination();
                smoke = currentdata.getSmoke();
                gas = currentdata.getGas();
                pm2_5 = currentdata.getPM25();
                co2 = currentdata.getCo2();
                airpressure = currentdata.getAirPressure();
                magneticwindow = currentdata.getMagneticWindow();
                glassdetection = currentdata.getGlassDetection();
                grating = currentdata.getGrating();
                statehumanInfrared = currentdata.getStateHumanInfrared();
                statehelpbutton = currentdata.getStateHelpButton();
            }
        }

    }


    public String returnResult() {
        String data = temp + "," + humidity + "," + illumination + "," + smoke + "," + gas + "," + pm2_5 + "," + co2 + "," + airpressure;
        return data;
    }
}
