package com.apptronics.matrix.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apptronics.matrix.R;
import com.apptronics.matrix.adapter.TasksAdapter;
import com.apptronics.matrix.model.Task;
import com.apptronics.matrix.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class TasksFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    static String ARG_BOOL_DONE="done";
    boolean done;
    ListView tasksList;
    String uid,projectTeamName;
    DatabaseReference mDatabase;
    TasksAdapter tasksAdapter;
    Task task;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        done=getArguments().getBoolean(ARG_BOOL_DONE,false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        tasksList=rootView.findViewById(R.id.tasksList);
        tasksList.setEmptyView(rootView.findViewById(R.id.empty));
        if(tasksAdapter==null){
            tasksAdapter=new TasksAdapter(getActivity(),R.layout.tasks_list_item);
            tasksAdapter.setDone(done);
        }
        tasksList.setAdapter(tasksAdapter);
        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                task = tasksAdapter.getItem(i);

                if (mListener != null) {
                    if(done){
                        mListener.viewLogs(task,projectTeamName);
                    } else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                        builder.setTitle("Pick an Action")
                                .setItems(R.array.actions_array, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i){
                                            case 0:{ // start task
                                                mListener.onStartLogRequested(task,uid,projectTeamName);
                                                break;
                                            }
                                            case 1:{ // view logs
                                                mListener.viewLogs(task,projectTeamName);
                                                break;
                                            }
                                        }
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

                }




            }
        });
        return rootView;
    }


    public void onRefresh(String projectTeamName,String p_uid){

        uid=p_uid;
        this.projectTeamName=projectTeamName;

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("teams").child(projectTeamName).child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(tasksAdapter==null){
                    tasksAdapter=new TasksAdapter(getActivity(),R.layout.tasks_list_item);
                    tasksAdapter.setDone(done);

                } else {
                    tasksAdapter.clear();
                }

                for(DataSnapshot task :dataSnapshot.getChildren()){
                    Timber.i("task looped %s %s",task.getKey(),uid);


                    if(done){
                        for(DataSnapshot UID :task.child("doneUIDs").getChildren()){
                            if(UID.getValue().equals(uid)){
                                tasksAdapter.add(new Task(task.getKey(), Utils.convertToDate(((Long)task.child("deadline").getValue()).intValue())));
                            }
                        }
                    } else {
                        for(DataSnapshot UID :task.child("onGoingUIDs").getChildren()){
                            Timber.i("%s",UID.getValue());
                            if(UID.getValue().equals(uid)){
                                tasksAdapter.add(new Task(task.getKey(),Utils.convertToDate(((Long)task.child("deadline").getValue()).intValue())));
                                Timber.i("added to adapter");
                            }
                        }
                    }
                }
                tasksList.setAdapter(tasksAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onStartLogRequested(Task task, String uid, String projectTeam);
        void viewLogs(Task task,String projectTeam);
    }
}
