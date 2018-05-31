package com.apptronics.matrix.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptronics.matrix.R;
import com.apptronics.matrix.adapter.UsersAdapter;
import com.apptronics.matrix.model.Task;
import com.apptronics.matrix.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Maha Perriyava on 4/30/2018.
 */

public class MainActivity extends AppCompatActivity implements TasksFragment.OnFragmentInteractionListener {

    ViewPager mViewPager;
    MyPagerAdapter myPagerAdapter;
    TabLayout tabLayout;
    User user;
    Bundle bundle;
    DatabaseReference mDatabase;
    MenuItem menu;
    String teamSelected,uid;
    int teams_num=0;
    FloatingActionButton addTask;
    EditText taskEditText;
    ListView usersList;
    UsersAdapter usersAdapter;
    Button addMembers;
    PrimaryDrawerItem projectMenuItem;
    SecondaryDrawerItem createProjectTeamItem;

    TextView toolbarTitle;

    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;

    AccountHeader headerResult;
    Drawer drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        setupViewPager(mViewPager);
        addTask=(FloatingActionButton)findViewById(R.id.addTask);
        addTask.setOnClickListener(new View.
                                OnClickListener() {
            @Override
            public void onClick(View view) {
                if(teamSelected==null||teamSelected.isEmpty()){
                    Toast.makeText(MainActivity.this,"Select a team",Toast.LENGTH_SHORT).show();
                    drawer.openDrawer();
                } else {
                    Intent intent = new Intent(MainActivity.this,AddUsersActivity.class);
                    intent.putExtra("team",teamSelected);
                    startActivity(intent);
                }
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        toolbarTitle=(TextView)findViewById(R.id.toolbar_title);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.navheader)
                .build();

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference();

        String token = FirebaseInstanceId.getInstance().getToken();
        mDatabase.child("users").child(uid).child("fcmId").setValue(token);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);

                headerResult.addProfiles(
                        new ProfileDrawerItem().withName(user.name).withEmail(user.email).withIcon(getResources().getDrawable(R.drawable.ic_person_black_24dp))
                );
                if(user!=null){
                    Timber.i("user found %s %s",uid,user.name);
                } else {
                    Timber.i("user not found");
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        mDatabase.child("users").child(uid).addValueEventListener(postListener);

        //menu = navigationView.getMenu().getItem(0);
        mDatabase.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot team :dataSnapshot.getChildren()){
                    Timber.i("team looped %s",team.getKey());
                    FirebaseMessaging.getInstance().subscribeToTopic(team.getKey().replace(' ','_'));

                    for(DataSnapshot uid : team.child("UIDs").getChildren()){
                        Timber.i("uid looped %s",uid.getValue());
                        if(uid.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            teams_num++;
                            switch ((String)team.child("type").getValue()){
                                case "electronics":{
                                    drawer.addItem(new PrimaryDrawerItem().withIdentifier(2+teams_num).withName(team.getKey()).withIcon(R.drawable.ic_electronics));
                                    Timber.i("team added %s",team.getKey());
                                    break;
                                }
                                case "android":{
                                    drawer.addItem(new PrimaryDrawerItem().withIdentifier(2+teams_num).withName(team.getKey()).withIcon(R.drawable.ic_android_black_24dp));
                                    Timber.i("team added %s",team.getKey());
                                    break;
                                }
                                case "web":{
                                    drawer.addItem(new PrimaryDrawerItem().withIdentifier(2+teams_num).withName(team.getKey()).withIcon(R.drawable.ic_web));
                                    Timber.i("team added %s",team.getKey());
                                    break;
                                }
                                default:{
                                    drawer.addItem(new PrimaryDrawerItem().withIdentifier(2+teams_num).withName(team.getKey()).withIcon(R.drawable.ic_other));
                                    Timber.i("team added %s",team.getKey());
                                    break;
                                }
                            }

                        }
                    }
                }
                if(teams_num>2){
                    //drawer.setSelection(3, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        createProjectTeamItem = new SecondaryDrawerItem().withName("Create Project Team").withIdentifier(1).withIcon(R.drawable.ic_add_black_24dp);
        projectMenuItem = new PrimaryDrawerItem().withIdentifier(2).withName("Project Teams").withSelectable(false).withSubItems(createProjectTeamItem);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(
                        projectMenuItem
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if(teamSelected==null||teamSelected.isEmpty()){
                            Toast.makeText(MainActivity.this,"Please select team",Toast.LENGTH_LONG).show();
                            drawer.openDrawer();
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (view.getId()){
                            case 1:{//create project
                                startActivity(new Intent(MainActivity.this,AddUsersActivity.class));
                                break;
                            }
                            case 2:{//nothing
                                break;
                            }
                            default:{
                                updateUI(((PrimaryDrawerItem)drawerItem).getName());
                                drawer.closeDrawer();
                            }

                        }
                        return true;
                    }
                })
                .build();

        if(teams_num>2){
            //drawer.setSelection(4, true);
        }

        drawer.openDrawer();
    }

    private void updateUI(StringHolder name) {
        teamSelected=name.getText(this);
        toolbarTitle.setText(teamSelected+ " Tasks");
        ((TasksFragment)myPagerAdapter.getItem(0)).onRefresh(teamSelected,uid);
        ((TasksFragment)myPagerAdapter.getItem(1)).onRefresh(teamSelected,uid);
    }

    @Override
    public void onStartLogRequested(Task task, String uid, String projectTeam) {
        Intent intent = new Intent(this,TimerActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra("team",teamSelected);
        intent.putExtra("task",task.description);
        startActivity(intent);
    }

    @Override
    public void viewLogs(Task task, String projectTeam) {
        Intent intent = new Intent(this,LogsActivity.class);
        intent.putExtra("team",teamSelected);
        intent.putExtra("task",task.description);
        startActivity(intent);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        TasksFragment onGoingFragment, doneFragment;

        onGoingFragment= new TasksFragment();
        bundle=new Bundle();
        bundle.putBoolean("done",false);
        onGoingFragment.setArguments(bundle);
        myPagerAdapter.addFragment(onGoingFragment, "On Going");


        doneFragment= new TasksFragment();
        bundle=new Bundle();
        bundle.putBoolean("done",true);

        doneFragment.setArguments(bundle);
        myPagerAdapter.addFragment(doneFragment, "Done");

        viewPager.setAdapter(myPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //startActivity(new Intent(this,SettingsActivity.class));
            return true;
        } else if (id == R.id.action_signout) {
            signOut();
            return true;
        } else if (id == R.id.action_addUsers){
            Intent editUsersIntent = new Intent(MainActivity.this,AddUsersActivity.class);
            editUsersIntent.putExtra("team",teamSelected);
            editUsersIntent.putExtra("action","edit");
            startActivity(editUsersIntent);




        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();

    }





}
