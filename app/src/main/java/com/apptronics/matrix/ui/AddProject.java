package com.apptronics.matrix.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apptronics.matrix.R;
import com.apptronics.matrix.model.NewProject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import timber.log.Timber;

public class AddProject extends AppCompatActivity {

    EditText projectName,projectType;
    DatabaseReference databaseReference;
    TextInputLayout prName,prType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        projectName=(EditText)findViewById(R.id.input_project_name);
        projectType=(EditText)findViewById(R.id.input_project_type);
        prName=findViewById(R.id.txtInpProjectName);
        prType=findViewById(R.id.txtInpProjectType);

        databaseReference= FirebaseDatabase.getInstance().getReference();

        Button create = findViewById(R.id.btn_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String projectText = String.valueOf(projectName.getText());
                String projectTypeText = String.valueOf(projectType.getText());
                if(!validate(projectText,projectTypeText)){
                    return;
                }
                ArrayList<String> uids = getIntent().getStringArrayListExtra("uids");
                Timber.i("users %d",uids.size());
                databaseReference.child("teams").child(projectText).setValue(new NewProject(projectTypeText,uids))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddProject.this,"Created project "+projectText,Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddProject.this,MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(AddProject.this,"Failed to create project",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        Timber.i("add project created");
    }

    private boolean validate(String projectText,String projectTypeText) {

        boolean valid=true;

        if(projectText==null||projectText.isEmpty()){
            prName.setError("please enter project name");
            valid=false;
        }
        if(projectTypeText==null||projectTypeText.isEmpty()){
            prType.setError("please enter project name");
            valid=false;
        }
        return valid;
    }
}
