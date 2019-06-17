package com.example.classmission.json;

import com.google.gson.annotations.SerializedName;

public class Now {
    public String status;

    public Nowl now;

    public class Nowl{
        @SerializedName("tmp")
        public String temperature;
    }
    public Basic basic;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;

    }

    @SerializedName("cond_txt")
    public String info;
}
