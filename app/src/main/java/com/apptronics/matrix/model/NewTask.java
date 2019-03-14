package com.apptronics.matrix.model;

import java.util.ArrayList;

/**
 * Created by Maha Perriyava on 5/6/2018.
 */

public class NewTask {

    public ArrayList<String> assignedUIDs,onGoingUIDs;
    public long deadline;

    public NewTask(ArrayList<String> assignedUIDs,ArrayList<String> onGoingUIDs,long deadline){
        this.assignedUIDs=assignedUIDs;
        this.onGoingUIDs=onGoingUIDs;
        this.deadline=deadline;

    }
}
