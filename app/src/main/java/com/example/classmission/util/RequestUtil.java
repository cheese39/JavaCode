package com.example.classmission.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.example.classmission.WeatherActivity;
import com.example.classmission.json.AQI;
import com.example.classmission.json.ForecastList;
import com.example.classmission.json.Now;
import com.example.classmission.json.Suggestions;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public class RequestUtil {


    public static void requestNow(final String weatherId, WeatherActivity activity) {
        String nowUrl = "https://free-api.heweather.net/s6/weather/now?location=" +
                weatherId + "&key=b83c7f27f34740c79f1d7a464a9c60a9";
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (activity != null)
                    activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                            Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Now now = JsonUtil.handleNowResponse(responseText);
                if (null != now && "ok".equals(now.status)) {
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(activity)
                            .edit();
                    editor.putString("now","{\"HeWeather6\":["+ gson.toJson(now) +"]}");
                    editor.apply();
                    if (activity != null)
                        activity.runOnUiThread(() -> activity.showNowInfo(now));
                } else {
                    if (activity != null)
                        activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public static void requestForecastList(final String weatherId, WeatherActivity activity) {
        String detailUrl = "https://free-api.heweather.net/s6/weather/forecast?location=" +
                weatherId + "&key=b83c7f27f34740c79f1d7a464a9c60a9";
        HttpUtil.sendOkHttpRequest(detailUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (activity != null) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                            Toast.LENGTH_SHORT).show());
                    activity.refresh.setRefreshing(false);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                ForecastList forecastList = JsonUtil.handleForecastListResponse(responseText);
                if (null != forecastList && "ok".equals(forecastList.status)) {
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(activity)
                            .edit();
                    editor.putString("forecastList","{\"HeWeather6\":[" + gson.toJson(forecastList) + "]}");
                    editor.apply();
                    if (activity != null)
                        activity.runOnUiThread(() -> activity.showForecastListInfo(forecastList));
                } else {
                    if (activity != null)
                        activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show());
                }
                if (activity != null)
                    activity.refresh.setRefreshing(false);
            }
        });
    }

    public static void requestSuggestions(final String weatherId, WeatherActivity activity) {
        String nowUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location=" +
                weatherId + "&key=b83c7f27f34740c79f1d7a464a9c60a9";
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (activity != null)
                    activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                            Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Suggestions suggestions = JsonUtil.handleSuggestionsResponse(responseText);
                if (null != suggestions && "ok".equals(suggestions.status)){
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(activity)
                            .edit();
                    editor.putString("suggestions","{\"HeWeather6\":[" + gson.toJson(suggestions) + "]}");
                    editor.apply();
                    if (activity != null)
                        activity.runOnUiThread(() -> activity.showSuggestionsInfo(suggestions));
                } else {
                    if (activity != null)
                        activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public static void requestAQI(final String weatherId, WeatherActivity activity) {
        String nowUrl = "https://free-api.heweather.net/s6/air/now?location=" +
                weatherId + "&key=b83c7f27f34740c79f1d7a464a9c60a9";
        HttpUtil.sendOkHttpRequest(nowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (activity != null)
                    activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                            Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                AQI aqi = JsonUtil.handleAQIResponse(responseText);
                if (null != aqi && "ok".equals(aqi.status)) {
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(activity)
                            .edit();
                    editor.putString("aqi","{\"HeWeather6\":[" + gson.toJson(aqi) + "]}");
                    editor.apply();
                    if (activity != null)
                        activity.runOnUiThread(() -> activity.showAQIInfo(aqi));
                } else {
                    if (activity != null)
                        activity.runOnUiThread(() -> Toast.makeText(activity, "获取天气信息失败",
                                Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
