package com.example.classmission.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Suggestions {
    public String status;

    @SerializedName("lifestyle")
    public List<LifeStyle> suggestions;

    public class  LifeStyle{
        public String txt;
    }
}
