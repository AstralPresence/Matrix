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
import com.apptronics.matrix.model.User;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Maha Perriyava on 5/5/2018.
 */

public class UsersAdapter extends ArrayAdapter<User> {

    User p;
    public ArrayList<User> usersArray;
    ArrayList<String> uids;

    public UsersAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        usersArray=new ArrayList<>();
        uids=new ArrayList<>();
    }

    public UsersAdapter(Context context, int resource, List<User> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.user_list_item, null);
        }

        p = usersArray.get(position);

        if (p != null){
            UserItemHolder userItemHolder = new UserItemHolder(v);

            userItemHolder.userName.setText(p.name);
            userItemHolder.email.setText(p.email);
            userItemHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    p = usersArray.get(position);
                    p.selected=b;
                    if(b){
                        uids.add(p.uid);
                        Timber.i(" added %s",p.uid);
                    } else {
                        uids.remove(p.uid);
                        Timber.i(" removed %s",p.uid);
                    }
                }
            });

        }

        return v;
    }

    @Override
    public void add(@Nullable User object) {
        usersArray.add(object);
        super.add(object);
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return usersArray.get(position);
    }

    public class UserItemHolder {

        TextView userName;
        TextView email;
        CheckBox checkBox;

        public UserItemHolder(View v){
            userName = (TextView) v.findViewById(R.id.user_name);
            email = (TextView) v.findViewById(R.id.email);
            checkBox = (CheckBox)v.findViewById(R.id.user_selected);
        }

    }

    public ArrayList<String> getUids(){
        return uids;
    }
}
