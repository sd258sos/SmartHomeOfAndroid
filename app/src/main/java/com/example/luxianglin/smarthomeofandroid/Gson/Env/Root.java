package com.example.luxianglin.smarthomeofandroid.Gson.Env;

import java.util.List;

public class Root {
    private String UserName;

    private String Password;

    private String IP;

    private String Port;

    private String CurrentTime;

    private List<Data> Data;

    private List<Data_TimeAndAttendance> Data_TimeAndAttendance;

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPassword() {
        return this.Password;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getIP() {
        return this.IP;
    }

    public void setPort(String Port) {
        this.Port = Port;
    }

    public String getPort() {
        return this.Port;
    }

    public void setCurrentTime(String CurrentTime) {
        this.CurrentTime = CurrentTime;
    }

    public String getCurrentTime() {
        return this.CurrentTime;
    }

    public void setData(List<Data> Data) {
        this.Data = Data;
    }

    public List<Data> getData() {
        return this.Data;
    }

    public void setData_TimeAndAttendance(List<Data_TimeAndAttendance> Data_TimeAndAttendance) {
        this.Data_TimeAndAttendance = Data_TimeAndAttendance;
    }

    public List<Data_TimeAndAttendance> getData_TimeAndAttendance() {
        return this.Data_TimeAndAttendance;
    }

}