package com.apptronics.matrix.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apptronics.matrix.R;
import com.apptronics.matrix.model.Task;

import java.util.List;

/**
 * Created by Maha Perriyava on 5/4/2018.
 */

public class TasksAdapter extends ArrayAdapter<Task> {

    boolean done;
    String prefix;

    public void setDone(boolean done){
        this.done=done;

        if(done){
            prefix="Done by ";

        } else {
            prefix="Finish by ";
        }

    }
    public TasksAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public TasksAdapter(Context context, int resource, List<Task> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.tasks_list_item, null);
        }

        Task p = getItem(position);

        if (p != null){
            TextView deadline = (TextView) v.findViewById(R.id.taskDeadline);
            TextView description = (TextView) v.findViewById(R.id.taskDescriiption);


            if (deadline != null) {
                deadline.setText(prefix+p.deadline);
            }

            if (description != null) {
                description.setText(p.description);
            }
        }

        return v;
    }

}
