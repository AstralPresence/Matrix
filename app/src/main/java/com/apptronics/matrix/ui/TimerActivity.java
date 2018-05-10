package com.apptronics.matrix.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apptronics.matrix.R;
import com.apptronics.matrix.model.LogDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    Timer timer;
    MyTimerTask myTimerTask;
    FloatingActionButton pause_resume;
    TextView time, taskDesc;
    Calendar calendar;
    boolean  timerOn=true;
    int sec, min, hour;
    EditText progress;
    String uid,projectTeam,taskDescription,progressText;
    Button log;
    DatabaseReference database;
    RadioGroup radioGroup;
    int NOTIFICATION_ID=3;
    NotificationCompat.Builder mBuilder;
    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar toolbar = findViewById(R.id.toolbarT);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        uid=intent.getStringExtra("uid");
        projectTeam=intent.getStringExtra("team");
        taskDescription=intent.getStringExtra("task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(projectTeam);

        Timber.i("%s %s %s",uid,projectTeam,taskDescription);
        database= FirebaseDatabase.getInstance().getReference();

        progress=findViewById(R.id.input_progress_desc);
        radioGroup=findViewById(R.id.isTaskDone);
        log=(Button)findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressText= String.valueOf(progress.getText());
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                Toast.makeText(TimerActivity.this,"Logging to cloud",Toast.LENGTH_SHORT).show();
                database.child("teams").child(projectTeam).child("tasks").child(taskDescription)
                        .child("logs").child(uid).child(ts).setValue(new LogDB(progressText, (String) time.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            RadioButton button = findViewById(radioGroup.getCheckedRadioButtonId());
                            if(!((String)button.getText()).contains("progress")){//task is done remove id from ongoing and add to done
                                database.child("teams").child(projectTeam).child("tasks").child(taskDescription).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<String> onGoingUIDs = (ArrayList<String>) dataSnapshot.child("onGoingUIDs").getValue();
                                        onGoingUIDs.remove(uid);
                                        dataSnapshot.getRef().child("onGoingUIDs").removeValue();
                                        dataSnapshot.getRef().child("onGoingUIDs").setValue(onGoingUIDs);

                                        ArrayList<String> doneUIDs = (ArrayList<String>) dataSnapshot.child("doneUIDs").getValue();
                                        if(doneUIDs==null){
                                            doneUIDs=new ArrayList<>();
                                        } else {
                                            dataSnapshot.child("doneUIDs").getRef().removeValue();
                                        }
                                        doneUIDs.add(uid);
                                        dataSnapshot.getRef().child("doneUIDs").setValue(doneUIDs);

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());
                                        notificationManager.cancel(NOTIFICATION_ID);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            finish();
                        } else {
                            Toast.makeText(TimerActivity.this,"Failed logging to cloud",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        taskDesc=(TextView)findViewById(R.id.taskDescriiption);
        taskDesc.setText(taskDescription);

        pause_resume=findViewById(R.id.pause_resume);
        pause_resume.setOnClickListener(this);

        time=findViewById(R.id.timeText);

        intent1 = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        mBuilder = new NotificationCompat.Builder(this, "Matrix work")
                .setSmallIcon(R.drawable.thumbnail)
                .setContentTitle(taskDescription)
                .setContentText("work time 0:00:00")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        timer= new Timer();
        myTimerTask = new MyTimerTask();
        sec=0;min=0;hour=0;
        timer.schedule(myTimerTask,0,1000);


    }

    @Override
    public void onClick(View view) {

        if(timerOn){ //user has paused
            timer.cancel();
            timer=null;
            timerOn=false;
            pause_resume.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        } else {
            timerOn=true;
            pause_resume.setImageResource(R.drawable.ic_pause_black_24dp);
            timer= new Timer();
            myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask,0,1000);
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    time.setText(incrementTime());
                }});
        }

    }

    private String incrementTime() {
        sec++;
        if(sec==60){
            sec=0;
            min++;
            if(min==60){
                min=0;
                hour++;
            }
        }

        DecimalFormat formatter = new DecimalFormat("00");
        String secString = formatter.format(sec);
        String minString = formatter.format(min);
        String hourString = formatter.format(hour);
        String returnString = hourString+":"+minString+":"+secString;

        mBuilder.setContentText("work time "+returnString);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        return returnString;
    }

    @Override
    protected void onStop() {
        Timber.i("on stop called");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onBackPressed();
    }
}
