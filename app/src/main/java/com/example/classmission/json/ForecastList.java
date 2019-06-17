package com.example.classmission.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastList {
    public String status;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    public class Forecast {
        public String date;

        @SerializedName("tmp_max")
        public String max;

        @SerializedName("tmp_min")
        public String min;

        @SerializedName("cond_txt_d")
        public String info;
    }
}
