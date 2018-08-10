package com.myday.dong.myday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


import java.util.Calendar;

public class AlarmService extends Service {

    private Calendar calendar;
    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        calendar=Calendar.getInstance();
        Info info=(Info)intent.getSerializableExtra("info_data");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent notificationintent = new Intent(AlarmService.this, NotificationReceiver.class);
                notificationintent.putExtra("Title",info.getInfo());
                notificationintent.setAction("alarm");
                PendingIntent sender = PendingIntent.getBroadcast(AlarmService.this, info.getHour()*60+info.getMinute(), notificationintent, 0);
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                if(info.getAlarm()==1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((info.getHour() - calendar.get(Calendar.HOUR_OF_DAY)) * 3600 + (info.getMinute() - calendar.get(Calendar.MINUTE)) * 60) * 1000, sender);
                    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((info.getHour() - calendar.get(Calendar.HOUR_OF_DAY)) * 3600 + (info.getMinute() - calendar.get(Calendar.MINUTE)) * 60) * 1000, sender);
                    }else {
                        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((info.getHour() - calendar.get(Calendar.HOUR_OF_DAY)) * 3600 + (info.getMinute() - calendar.get(Calendar.MINUTE)) * 60) * 1000, sender);
                    }
                }else{
                    am.cancel(sender);
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
