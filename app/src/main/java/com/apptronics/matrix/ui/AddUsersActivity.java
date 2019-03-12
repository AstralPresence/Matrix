package com.apptronics.matrix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.apptronics.matrix.R;
import com.apptronics.matrix.adapter.UsersAdapter;
import com.apptronics.matrix.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import timber.log.Timber;

public class AddUsersActivity extends AppCompatActivity implements View.OnClickListener{

    ListView usersList;
    String team;
    DatabaseReference databaseReference;
    UsersAdapter usersAdapter;
    ArrayList<String> users,uids,selectedUsers;
    ArrayList<User> userArrayList;
    Button addMembers;
    boolean isTask=false,isEdit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button on top
        usersList=findViewById(R.id.usersList);
        usersAdapter=new UsersAdapter(this,R.layout.user_list_item);
        usersList.setAdapter(usersAdapter);

        addMembers=findViewById(R.id.addUsers);

        databaseReference= FirebaseDatabase.getInstance().getReference();

        if("edit".equals(getIntent().getStringExtra("action"))){
            isEdit=true;
        }

        Intent intent = getIntent();
        team=intent.getStringExtra("team");
        userArrayList = new ArrayList<>();
        if(team==null||team.isEmpty()||isEdit){ //add project
            if(!isEdit){
                getSupportActionBar().setTitle("Add Project Members");
            }
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(usersAdapter==null){
                        usersAdapter=new UsersAdapter(AddUsersActivity.this,R.layout.user_list_item);
                    }
                    for(DataSnapshot user: dataSnapshot.getChildren()){

                        User t_user = new User((String)user.child("name").getValue(),(String)user.child("email").getValue(),null,null);
                        t_user.uid=user.getKey();
                        usersAdapter.add(t_user);
                        userArrayList.add(t_user);
                        Timber.i("added to adapter %s",t_user.uid);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(isEdit){ ///edit users

                getSupportActionBar().setTitle("Edit Users "+team);
                databaseReference.child("teams").child(team).child("UIDs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        selectedUsers = new ArrayList<>();
                        for(DataSnapshot user: dataSnapshot.getChildren()){
                            selectedUsers.add((String)user.getValue());
                        }
                        //load selected entries
                        usersAdapter.clear();

                        for(User user:userArrayList){
                            if(selectedUsers.contains(user.name)){
                                user.selected=true;
                            } else {
                                user.selected=false;
                            }
                            usersAdapter.add(user);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        } else {

            //add task or edit team users
            isTask=true;
            getSupportActionBar().setTitle("Assign Task to Users");

            //populate with project users
            databaseReference.child("teams").child(team).child("UIDs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    users = new ArrayList<>();
                    for(DataSnapshot user: dataSnapshot.getChildren()){
                        users.add((String)user.getValue());
                    }
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot dbUser: dataSnapshot.getChildren()){
                                if(users.contains(dbUser.getKey())){
                                    User t_user = new User((String)dbUser.child("name").getValue(),(String)dbUser.child("email").getValue(),null,null);
                                    t_user.uid=dbUser.getKey();
                                    usersAdapter.add(t_user);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        addMembers.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        uids=usersAdapter.getUids();
        Intent intent;

        if(isEdit){

            databaseReference.child("teams").child(team).child("UIDs").setValue(uids).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AddUsersActivity.this,"users edited",Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        Toast.makeText(AddUsersActivity.this,"could not edit users",Toast.LENGTH_LONG).show();

                    }
                }
            });

        } else {

            if(isTask){
                intent = new Intent(AddUsersActivity.this,AddTask.class);
                intent.putExtra("team",team);
            } else {
                intent = new Intent(AddUsersActivity.this,AddProject.class);
            }

            intent.putStringArrayListExtra("uids",uids);
            if(uids.size()==0){
                Toast.makeText(this,"Please add Members",Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this,uids.size()+" members added",Toast.LENGTH_LONG).show();
            startActivity(intent);
        }

    }
}
