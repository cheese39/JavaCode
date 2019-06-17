package com.example.classmission.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.classmission.json.Now;
import com.example.classmission.util.JsonUtil;
import com.example.classmission.util.RequestUtil;

public class AutoUpdate extends Service {
    public AutoUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdate.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     *更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nowString = prefs.getString("now", null);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit();
        editor.putString("now", null);
        editor.putString("forecastList", null);
        editor.putString("suggestions", null);
        editor.putString("aqi", null);
        editor.apply();
        if (null != nowString) {
            Now now = JsonUtil.handleNowResponse(nowString);
            String weatherId = now.basic.weatherId;
        }
    }
}
