package com.apptronics.matrix.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.apptronics.matrix.R;
import com.apptronics.matrix.adapter.LogsAdapter;
import com.apptronics.matrix.model.LogDisplay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LogsActivity extends AppCompatActivity {

    ArrayList<LogDisplay> logsArray;
    DatabaseReference databaseReference;
    LogsAdapter logsAdapter;
    ListView logsList;
    String team,task,uid;
    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logsList=findViewById(R.id.logsList);
        logsAdapter = new LogsAdapter(LogsActivity.this,R.layout.log_list_item);
        logsList.setAdapter(logsAdapter);
        emptyView=findViewById(R.id.empty);
        logsList.setEmptyView(emptyView);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        team=getIntent().getStringExtra("team");
        task=getIntent().getStringExtra("task");
        getSupportActionBar().setTitle(task+" Logs");

        databaseReference.child("teams").child(team).child("tasks").child(task).child("logs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot logs) {
                logsArray=new ArrayList<>();
                for(DataSnapshot userLog:logs.getChildren()){
                    uid=userLog.getKey();
                    for(DataSnapshot timeLog:userLog.getChildren()){
                        Timber.i("time %s",timeLog.getKey());
                        String timeStamp = timeLog.getKey();
                        String workDescription = (String) timeLog.child("description").getValue();
                        String timeWorked = (String) timeLog.child("time").getValue();
                        LogDisplay logDisplay = new LogDisplay(timeStamp,timeWorked,workDescription,uid);
                        logsArray.add(logDisplay);
                        logsAdapter.add(logDisplay);
                    }
                }
                logsList.setAdapter(logsAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
