package com.apptronics.matrix.rest;


import com.apptronics.matrix.model.FCMMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by DevOpsTrends on 5/26/2017.
 */

public interface ApiInterface {



    @POST("sendFCM")
    Call<String> sendFCM(@Body FCMMessage fcmMessage);


}