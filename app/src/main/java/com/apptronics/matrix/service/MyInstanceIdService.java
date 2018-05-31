package com.apptronics.matrix.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIdService extends FirebaseInstanceIdService {

    DatabaseReference databaseReference;

    @Override
    public void onTokenRefresh(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("users").child(uid).child("fcmId").setValue(refreshedToken);
    }
}
