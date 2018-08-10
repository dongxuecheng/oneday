package com.myday.dong.myday;


import java.io.Serializable;

public class Info implements Serializable {
    private String info;
    private String memo;
    private int hour;
    private int minute;

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    private int alarm;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public Info(String info, String memo, int hour, int minute,int status,int alarm) {
        this.info = info;
        this.memo = memo;
        this.hour = hour;
        this.minute = minute;
        this.status=status;
        this.alarm=alarm;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
