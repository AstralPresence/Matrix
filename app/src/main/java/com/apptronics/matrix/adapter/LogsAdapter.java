package com.apptronics.matrix.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.apptronics.matrix.R;
import com.apptronics.matrix.model.LogDisplay;
import com.apptronics.matrix.model.User;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Maha Perriyava on 5/7/2018.
 */

public class LogsAdapter extends ArrayAdapter<LogDisplay> {

    ArrayList<LogDisplay> logsArray;
    LogDisplay p;


    public LogsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        logsArray=new ArrayList<>();
    }

    public LogsAdapter(Context context, int resource, List<LogDisplay> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.log_list_item, null);
        }

        p = logsArray.get(position);

        if (p != null){
            LogsAdapter.LogItemHolder logItemHolder = new LogsAdapter.LogItemHolder(v);

            logItemHolder.userName.setText(p.userName);
            logItemHolder.timeStamp.setText(p.timeStamp);
            logItemHolder.timeWorked.setText(p.timeWorked);
            logItemHolder.workDescription.setText(p.workDescription);

        }

        return v;
    }

    @Override
    public void add(@Nullable LogDisplay object) {
        logsArray.add(object);
        super.add(object);
    }

    @Nullable
    @Override
    public LogDisplay getItem(int position) {
        return logsArray.get(position);
    }

    public class LogItemHolder {

        TextView userName,timeStamp,timeWorked,workDescription;

        public LogItemHolder(View v){
            userName = (TextView) v.findViewById(R.id.userName);
            timeStamp = (TextView) v.findViewById(R.id.timeStamp);
            timeWorked = (TextView) v.findViewById(R.id.timeWorked);
            workDescription = (TextView) v.findViewById(R.id.workDescription);
        }
    }

}
