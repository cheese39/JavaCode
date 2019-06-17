package com.example.classmission.json;

import com.google.gson.annotations.SerializedName;

public class AQI {
    public String status;

    @SerializedName("air_now_city")
    public AQICity city;

    public class AQICity {

        public String aqi;

        public String pm25;

    }

}
