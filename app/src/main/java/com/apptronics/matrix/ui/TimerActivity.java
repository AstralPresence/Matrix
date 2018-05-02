package com.apptronics.matrix.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apptronics.matrix.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    Timer timer;
    MyTimerTask myTimerTask;
    FloatingActionButton pause_resume;
    TextView time;
    Calendar calendar;
    boolean  timerOn=true;
    int sec, min, hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        pause_resume=findViewById(R.id.pause_resume);
        pause_resume.setOnClickListener(this);

        time=findViewById(R.id.timeText);

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

        return hourString+":"+minString+":"+secString;
    }
}
