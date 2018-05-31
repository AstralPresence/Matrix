package com.apptronics.matrix.model;

import com.google.gson.annotations.SerializedName;

public class FCMMessage {

    @SerializedName("topic")
    private String topic;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    public FCMMessage(String topic,String title,String body) {
        this.title=title;
        this.topic = topic;
        this.body=body;
    }

}

