package com.apptronics.matrix.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.apptronics.matrix.R;
import com.apptronics.matrix.model.NewTask;
import com.apptronics.matrix.service.DataService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import timber.log.Timber;

public class AddTask extends AppCompatActivity {

    private DatePicker datePicker;
    private Calendar calendar;
    EditText taskEditText,deadlineEditText;
    DatabaseReference databaseReference;
    TextInputLayout taskNameTIL,taskDeadlineTIL;
    long deadline;
    int mYear,mMonth,mDay;
    String projectName;
    String taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        taskEditText=findViewById(R.id.taskDescription);
        deadlineEditText=findViewById(R.id.taskDeadline);

        taskNameTIL=findViewById(R.id.taskTIL);
        taskDeadlineTIL=findViewById(R.id.deadlineTIL);

        calendar = Calendar.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference= FirebaseDatabase.getInstance().getReference();

        Button create = findViewById(R.id.btn_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskName = String.valueOf(taskEditText.getText());
                String taskDeadline = String.valueOf(deadlineEditText.getText());
                if(!validate(taskName,taskDeadline)){
                    return;
                }
                ArrayList<String> uids = getIntent().getStringArrayListExtra("uids");
                projectName=getIntent().getStringExtra("team");
                Timber.i("users %d",uids.size());
                databaseReference.child("teams").child(projectName).child("tasks").child(taskName).setValue(new NewTask(uids,uids,deadline))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent fcmIntent = new Intent(AddTask.this, DataService.class);
                                    fcmIntent.putExtra("topic",projectName);
                                    fcmIntent.putExtra("title","New task in "+projectName);
                                    fcmIntent.putExtra("body",taskName);
                                    startService(fcmIntent);

                                    Toast.makeText(AddTask.this,"Created task "+taskName, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddTask.this,MainActivity.class));

                                    finish();
                                } else {
                                    Toast.makeText(AddTask.this,"Failed to create project",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        deadlineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.i("clicked deadline");
                setDate();
            }
        });
    }


    public void setDate() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        deadline=(year*10000)+(monthOfYear*100)+dayOfMonth;
                        deadlineEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private boolean validate(String taskText,String taskDeadline) {

        boolean valid=true;

        if(taskText==null||taskText.isEmpty()){
            taskNameTIL.setError("please enter project name");
            valid=false;
        }
        if(taskDeadline==null||taskDeadline.isEmpty()){
            taskDeadlineTIL.setError("please enter project name");
            valid=false;
        }
        return valid;
    }
}
