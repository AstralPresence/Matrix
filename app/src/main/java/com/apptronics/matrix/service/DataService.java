package com.apptronics.matrix.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.apptronics.matrix.model.FCMMessage;
import com.apptronics.matrix.rest.ApiClient;
import com.apptronics.matrix.rest.ApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class DataService extends IntentService {

    public DataService(String name) {
        super(name);
    }

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Timber.i("topic %s",intent.getStringExtra("topic"));

        FCMMessage fcmMessage = new FCMMessage(
                intent.getStringExtra("topic").replace(' ','_'),
                intent.getStringExtra("title"),
                intent.getStringExtra("body")
        );
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<String> callTT = apiService.sendFCM(fcmMessage);

        try {
            Response<String> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    Timber.i("success sent fcm");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
