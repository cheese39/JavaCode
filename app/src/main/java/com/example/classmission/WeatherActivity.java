package com.example.classmission;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.classmission.json.AQI;
import com.example.classmission.json.ForecastList;
import com.example.classmission.json.Now;
import com.example.classmission.json.Suggestions;
import com.example.classmission.service.AutoUpdate;
import com.example.classmission.util.JsonUtil;
import com.example.classmission.util.RequestUtil;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    private DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout refresh;
    //设置初始显示登封的天气
    private String mWeather = "CN101010100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏与图片融为一体
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            setContentView(R.layout.activity_weather);
        }

        weatherLayout = findViewById(R.id.weather_layout);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        refresh = findViewById(R.id.swipe_refresh);
        refresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nowString = prefs.getString("now", null);
        String forecastListsString = prefs.getString("forecastList", null);
        String aqiString = prefs.getString("aqi", null);
        String suggestionsString = prefs.getString("suggestions", null);
        if (null != nowString && null != forecastListsString &&
                null != aqiString && null != suggestionsString) {
            //有缓存时直接解析天气数据
            Now now = JsonUtil.handleNowResponse(nowString);
            ForecastList forecastList = JsonUtil.handleForecastListResponse(forecastListsString);
            AQI aqi = JsonUtil.handleAQIResponse(aqiString);
            Suggestions suggestions = JsonUtil.handleSuggestionsResponse(suggestionsString);
            mWeather = now.basic.weatherId;
            showAQIInfo(aqi);
            showSuggestionsInfo(suggestions);
            showForecastListInfo(forecastList);
            showNowInfo(now);
        } else {
            //无缓存是去服务器查询天气
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeather);
        }

        //加载bing每日一图作为背景图片
        String bingPic = prefs.getString("bing_pic", null);
        Uri uri = Uri.parse("http://cn.bing.com/th?id=OHR.RioGrande_ROW8281260304_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg");
        Glide.with(this).load(uri).into(bingPicImg);

        //将城市选择作为滑动菜单实现
        navButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        //注册刷新天气事件
        refresh.setOnRefreshListener(() -> requestWeather(mWeather));
    }

    /*
     *根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) {
        mWeather = weatherId;
        clearInfo();
        RequestUtil.requestAQI(weatherId, this);
        RequestUtil.requestNow(weatherId, this);
        RequestUtil.requestSuggestions(weatherId, this);
        RequestUtil.requestForecastList(weatherId, this);
    }

    /**
     * 处理并展示now实体类中的数据。
     */
    public void showNowInfo(Now now) {
        String degree = now.now.temperature + "℃";
        String weatherInfo = now.info;
        String cityName = now.basic.cityName;
        String updateTime = now.update.updateTime.split(" ")[1];
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);

        weatherLayout.setVisibility(View.VISIBLE);
        //设置后台自动更新服务
        Intent i = new Intent(this, AutoUpdate.class);
        startService(i);
    }

    /**
     * 处理并展示ForecastList实体类中的数据。
     */
    public void showForecastListInfo(ForecastList forecastList) {
        forecastLayout.removeAllViews();
        for (ForecastList.Forecast forecast : forecastList.forecastList) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forecastLayout.addView(view);
        }
    }


    /**
     * 处理并展示AQI实体类中的数据。
     */

    public void showAQIInfo(AQI aqi){
        aqiText.setText(aqi.city.aqi);
        pm25Text.setText(aqi.city.pm25);
    }
    /**
     * 处理并展示Suggestions实体类中的数据。
     */
    public void showSuggestionsInfo(Suggestions suggestions) {
        String comfort = "舒适度：" + suggestions.suggestions.get(0).txt;
        String carWash = "洗车指数：" + suggestions.suggestions.get(6).txt;
        String sport = "运动建议：" + suggestions.suggestions.get(3).txt;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
    }
    /**
     * 将界面清空，防止接受now和detail数据可能发生的数据不一致性
     */
    private void clearInfo() {
        String blanket = "";
        titleCity.setText(blanket);
        titleUpdateTime.setText(blanket);
        degreeText.setText(blanket);
        weatherInfoText.setText(blanket);
        forecastLayout.removeAllViews();
        aqiText.setText(blanket);
        pm25Text.setText(blanket);
        comfortText.setText(blanket);
        carWashText.setText(blanket);
        sportText.setText(blanket);
    }

    /*
     *关闭滑动窗口
     */
    public void closeDrawers() {
        if (drawerLayout != null)
            drawerLayout.closeDrawers();
    }
}
